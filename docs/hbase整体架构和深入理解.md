# HBase 整体架构和深入理解

> **文档版本**: v1.0
> **创建日期**: 2026-08-31
> **编写人**: weixin.liu
> **用途**: 从整体架构到存储/读写/合并/分裂/副本机制全面拆解 HBase，供后续大模型阅读与排障

---

## 0. 一句话定位

HBase 是一个**面向列族、强一致、可海量扩展、随机读写**的 NoSQL 数据库，构建在 HDFS 之上，用"LSM-Tree + Region 分片 + WAL"解决传统 RDBMS 在 PB 级数据下的扩展瓶颈。它是 BigTable 的开源实现。

---

## 1. 适用场景与不适用场景

| 维度 | 说明 |
|---|---|
| 适用 | PB 级、宽表（百万列）、稀疏、随机读写、按 rowkey 范围/前缀查询、写多读少 |
| 不适用 | 复杂 SQL 联表、多维聚合分析、跨行事务、全文检索（这些用 MySQL/Spark/ES） |
| 数据模型 | 稀疏的行列矩阵：`行键 RowKey + 列族 ColumnFamily + 列 Qualifier + 时间戳 Version` 定位一个 Cell |

典型场景：订单/消息/轨迹流水、用户画像宽表、时序监控数据（结合 OpenTSDB）、风控特征。

---

## 2. 整体架构总览

```
                ┌──────────────────────────────────────┐
                │              Client                   │
                │  (HBase Shell / Java API / Thrift)   │
                └───────────────┬──────────────────────┘
                                │ DDL/DML, 先查元数据定位
                ┌───────────────▼──────────────────────┐
                │           HMaster (主)                 │
                │  建表/改表/Region 分配/负载均衡/故障恢复   │
                │  (不参与数据读写, 不在热点路径)          │
                └──────┬──────────────────┬─────────────┘
                       │ 分配 Region       │ 元数据
            ┌──────────▼─────┐    ┌────────▼──────────┐
            │ RegionServer   │    │  ZooKeeper         │
            │  (从, 承载数据) │    │  选主/路由入口/状态 │
            │  ┌──────────┐  │    └────────────────────┘
            │  │ WAL(预写)│  │
            │  │ BlockCache│ │      ┌────────────────┐
            │  │ MemStore │  │◀─────│   HDFS         │
            │  │ Region×N │  │ HFile│  DataNode/NN   │
            │  └──────────┘  │读写   └────────────────┘
            └────────────────┘
```

核心组件：

| 组件 | 职责 |
|---|---|
| **Client** | 发起 DDL/DML；缓存元数据（Region 位置），直连 RegionServer 读写，不经过 HMaster |
| **HMaster** | 建表/删表/改表、Region 分配与迁移、负载均衡、故障 RegionServer 恢复、命名空间管理。**不参与读写** |
| **RegionServer** | 数据真正承载者：管多个 Region，处理读写请求、WAL、MemStore、BlockCache、HFile |
| **ZooKeeper** | 选主（HMaster 主备切换）、Region 寻址入口（`/hbase/meta-region-server`）、心跳、RegionServer 上下线感知 |
| **HDFS** | 底层存储：HFile/WAL 落盘，提供多副本持久化 |

---

## 3. Region：数据分片单位

### 3.1 什么是 Region

一张表按 **RowKey 范围**横向切分成多个 **Region**，每个 Region 是"一段连续 RowKey + 所有列族"的数据集合，是 HBase **负载均衡与并行处理的最小单位**。

```
表 T 的 RowKey 空间: [ , ...)
  Region1: [ , rowkey100)   Region2: [rowkey100, rowkey500)   Region3: [rowkey500, )
```

- 一个 Region 默认不会太大（`hbase.hregion.max.filesize`，默认 ~10GB），超过会 **Split**。
- 一个 RegionServer 承载多个 Region（几百到几千）。
- 一个 Region 内每个列族又是一个 **Store**（一个列族 = 一个 Store = 多个 MemStore + 多个 HFile）。

### 3.2 Region 寻址（三步/两步定位）

Client 第一次读写时如何找到目标 RegionServer：

1. 访问 ZK 的 `/hbase/meta-region-server`，拿到 `hbase:meta` 表所在 RegionServer。
2. 读 `hbase:meta` 表，按 RowKey 查出该行属于哪个 Region、由哪个 RegionServer 承载。
3. 直连该 RegionServer 读写。

> 优化：Client 会**缓存** meta 信息，缓存未命中或失效才回查；新版本 HBase 把 meta 合一，定位更简。

### 3.3 Region 状态机

`Offline → Pending/Open → Open → Splitting → ... → Closed`。Region 的分配、迁移、分裂、合并由 HMaster 协调、RegionServer 执行，状态记在 ZK / hbase:meta。

---

## 4. RegionServer 内部结构（核心）

```
RegionServer
  ├── BlockCache (读缓存, LruBlockCache / BucketCache)
  ├── Region
  │     └── Store (列族)
  │           ├── MemStore (写缓存, 内存 LSM)
  │           └── HFile×N (落盘的 SSTable)
  └── WAL (Write-Ahead Log, 预写日志, 跨 Region 共享)
```

关键三件套：

| 组件 | 作用 |
|---|---|
| **WAL** | 写入先追加 WAL（HDFS 上一个文件），故障重启时回放，保证不丢数据。可关闭换性能 |
| **MemStore** | 每个列族一个，写入先入 MemStore（内存有序），写满 flush 成 HFile。LSM-Tree 的 C0 层 |
| **BlockCache** | 读缓存，缓存最近读到的 Block（HFile 的 64KB 块）。LruBlockCache（堆内）+ BucketCache（堆外/SSD） |

---

## 5. 写数据流程

```
Client -> 定位 RegionServer
  -> RegionServer 接收 Put
  -> 1) 写 WAL (HDFS append, 可选 DURABILITY)
  -> 2) 写 MemStore (按 rowkey+cf+qualifier+ts 排序)
  -> 3) 返回 ack 给 Client (写成功)
  [异步/同步] MemStore 写满 -> flush -> 生成新 HFile -> 落 HDFS
```

要点：
1. **顺序写**：WAL 是 append，MemStore 是内存有序，都是顺序写，所以 HBase 写极快。
2. **写完 MemStore 即返回**：不用等 HFile，延迟低。
3. **WAL 的 durability**：`SKIP_WAL` 跳过、`ASYNC_WAL` 异步刷盘、`SYNC_WAL` 同步。多数场景 ASYNC 兼顾安全与性能。

### 5.1 MemStore flush 触发

- MemStore 大小达阈值（`hbase.hregion.memstore.flush.size`，默认 128MB）。
- RegionServer 整体内存水位高（`memstore.upper.limit`），触发 **flush 阻塞写**（backpressure）。
- WAL 文件数超阈值，强制 flush 老数据。
- 手动 `flush '表名'`。

---

## 6. 读数据流程

```
Client -> 定位 RegionServer
  -> RegionServer:
     1) 先查 BlockCache (读缓存), 命中直接返回
     2) 未命中查 MemStore (可能有未 flush 的新数据)
     3) 再查 HFile (带布隆过滤器, 多版本合并)
  -> 合并返回最新版本
```

要点：
1. HBase 读比写慢，因为**一个 RowKey 的数据可能散落在 MemStore + 多个 HFile**，要扫描合并 + 过滤版本。
2. **布隆过滤器**：HFile 带布隆，快速判断某 RowKey/列是否在该 HFile，避免无谓 IO。
3. **BlockCache**：默认 LRU，热点数据常驻内存。大内存建议 BucketCache 堆外，减轻 GC。
4. 读最新版本：合并 MemStore 和 HFile 的所有版本，按时间戳取最新（或按用户指定 version）。

### 6.1 读放大

HFile 越多，一次读要扫的文件越多——读放大。靠 Minor/Major Compaction 合并 HFile 控制。这是 Compaction 的根本动机。

---

## 7. Compaction（合并，HBase 运维核心）

### 7.1 为什么合并

MemStore 不断 flush 出大量小 HFile，导致：
- 读放大（一个读要扫很多 HFile）。
- 空间放大（多版本/删除标记占空间）。
- 小文件多，HDFS NameNode 压力大。

Compaction 把多个小 HFile 合并成大 HFile，清理过期/删除数据。

### 7.2 两种合并

| 类型 | Minor | Major |
|---|---|---|
| 参与文件 | 少量相邻小 HFile | 一个列族所有 HFile |
| 清理删除/过期 | 不全清 | 全部清理（只剩最新版本） |
| 耗时/IO | 小 | 大，重 |
| 触发 | 自动，频繁 | 自动低频 + 手动 |

### 7.3 策略

- **Stripe** / **Date Tiered** / 默认 **Exploring Compaction**：选哪些文件合并的策略。
- Major Compaction 默认 7 天自动跑一次（`hbase.hregion.majorcompaction`），生产常关掉自动、改**业务低峰手动**触发，避免抖动。
- 合并是**重 IO**，是 HBase 抖动首因，必须管控。

---

## 8. Region Split（分裂）

### 8.1 触发与策略

当某个 Region 的 HFile 总大小超过 `hbase.hregion.max.filesize`（默认约 10GB），触发 Split，一分为二。

分裂策略：
- `DefaultSplitPolicy`：按大小。
- `IncreasingToUpperBoundRegionCountSplitPolicy`：考虑 RegionServer 上 Region 数。
- `KeyPrefixRegionSplitPolicy`：按 RowKey 前缀预切（适合前缀分布的时序数据）。
- `DelimitedKeyPrefixRegionSplitPolicy`：按分隔符前缀。

### 8.2 分裂的过程

`Split: 父 Region -> 准备 daughterA/B -> 父 offline -> 两个 daughter 上线`。

老版本有 `split` 状态长时间停留（"split 事务"）；新版本用 **Mob / Ref** 优化，分裂瞬间完成（HFile 引用），后台补数据。

### 8.3 热点与预分区

如果按自增 ID 或时间做 RowKey，所有写都打到**最后一个 Region**（热点）。解法：
1. **预分区**：建表时指定 split points，预先切成多段。
2. **加盐 / Hash 前缀**：RowKey 前加哈希散列，打散写。
3. **反转 RowKey**：适合手机号等前缀集中场景（但范围查询失效）。

---

## 9. Region 合并 Merge

与 Split 相反：当 Region 太小（数据删除后）想合并，用 `merge_region`。一般不常用，主要在删除大量数据后回收。

---

## 10. RowKey 设计原则（性能命脉）

RowKey 决定数据落在哪个 Region、能否范围扫描，是 HBase 性能第一要素：

| 原则 | 说明 |
|---|---|
| **散列** | 避免热点，前缀加 hash/加盐 |
| **范围** | 一起查的数据 RowKey 相邻，用复合键拼接（如 `userid_orderid`） |
| **定长** | RowKey 用补零定长，保证字典序即数值序（`000123` 而非 `123`） |
| **倒序时间** | 时序场景常把 `Long.MAX_VALUE - ts` 放前面，让最新数据在前 |
| **短** | RowKey 存每个 Cell，越短越省内存与磁盘 |

典型订单宽表 RowKey：`[反转用户id后4位]_[时间倒序]_[订单id]`，兼顾散列 + 时间倒序范围扫描。

---

## 11. 列族与版本

- 一张表 1~3 个列族为宜，**列族名短**（每 Cell 都存）。
- 多版本：一个 Cell 可存多个历史版本，`VERSIONS` 控制保留数，读时取最新或指定。
- TTL：数据过期自动清理（按时间戳）。
- **MOB（Medium Object）**：中等大对象（100KB~10MB）单独存 MOB 文件，避免大 value 破坏 BlockCache。

---

## 12. HFile 结构

HFile 是 HBase 的 SSTable，结构（自底向上）：

```
Data Block (64KB, 存 KeyValue)
  -> Meta Block (布隆过滤器元数据)
  -> FileInfo (文件信息)
  -> Load on open 区: Data Index / Meta Index / Bloom Index (启动时载入内存)
  -> Fixed Trailer (指向各 index 的偏移)
```

- **Data Block** 是 IO 单位，整块读入 BlockCache。
- **Data Index** 在 RegionServer 打开 HFile 时常驻内存，二分定位 Block。
- **Bloom Filter** 过滤无谓 Block 读取。

---

## 13. WAL 与故障恢复

- 写入先写 WAL（HDFS 上 `WALs/<rs>/`），RegionServer 挂了重启时回放 WAL 恢复未 flush 的数据。
- **Distributed Log Splitting**：RS 挂了，HMaster 把它的 WAL 拆分给各 Region 的新 RS 回放。新版用 **AsyncDFSWALReplication / WAL Pipeline** 优化。
- WAL 是单点瓶颈（多 Region 共享一个 WAL），可用 **Multi-WAL**（每 RegionServer 多个 WAL）提升写吞吐。

---

## 14. 副本与一致性

- **HBase 层副本**：`REGION_REPLICATION > 1` 开启只读副本（Read Replicas），主 Region 写，副本异步复制。副本读是**最终一致**，用于读吞吐扩展、低延迟容忍过期。
- **HDFS 副本**：HFile/WAL 在 HDFS 上默认 3 副本，是真正的持久化保障。
- 一致性级别：`STRONG`（默认，强一致，读主 Region）、`TIMELINE`（时间线一致，可读副本）。

---

## 15. HBase vs 关系型 vs 其他 NoSQL

| 维度 | HBase | MySQL | Redis | ES |
|---|---|---|---|---|
| 数据模型 | 列族宽表 | 行列表 | KV/结构 | 文档/倒排 |
| 扩展 | 水平PB级 | 垂直+分库难 | 集群 | 集群 |
| 事务 | 单行强，跨行弱 | ACID | 弱 | 弱 |
| 查询 | RowKey/前缀/范围 | SQL全功能 | KV/集合 | 全文/聚合 |
| 读 | 中（合并多文件） | 快(索引) | 极快 | 中(分片) |
| 写 | 极快(顺序写) | 中 | 极快 | 中 |

---

## 16. 性能与运维要点

| 关注点 | 做法 |
|---|---|
| RowKey | 散列 + 范围 + 定长 + 短 |
| 预分区 | 建表指定 split points 防热点 |
| Compaction | 关闭自动 Major，低峰手动；调大 flush size 减少 HFile |
| BlockCache | 大内存用 BucketCache 堆外 |
| MemStore | 调 `memstore.upper.limit`，写多场景给足 |
| Region 数 | 单 RS 不超 1000，过大影响故障恢复 |
| 列族 | 少而短 |
| 版本/TTL | 设合理 VERSIONS/TTL，省空间 |
| GC | RS 堆大、用 G1/堆外，避免长 GC 暂停 |
| HFile | 控制 HFile 数，读多场景靠合并+布隆 |
| MOB | 中大 value 用 MOB |

---

## 17. 一张图串起核心概念

```
表 -> 按 RowKey 切成多个 Region -> 分配到 RegionServer
RegionServer 内:
  写: WAL(顺序) -> MemStore(内存有序) -> flush -> HFile(HDFS)
  读: BlockCache -> MemStore -> HFile(布隆过滤) -> 合并最新版本
后台:
  Compaction 合并小 HFile(去读放大)
  Split 分裂大 Region(去写放大/热点)
  WAL 回放 故障恢复
元数据: ZK -> hbase:meta -> RegionServer(三步寻址)
```

记住这条链，就抓住 HBase 全部骨架：**Region 分片、LSM 写、合并与分裂、WAL 恢复、ZK 寻址**。
