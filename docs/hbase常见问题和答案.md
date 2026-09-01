# HBase 常见问题和答案

> **文档版本**: v1.0
> **创建日期**: 2026-08-31
> **编写人**: weixin.liu
> **用途**: 系统梳理 HBase 高频面试题与标准答案，每题给"要点速答 + 深入展开"，配合 [hbase整体架构和深入理解.md](hbase整体架构和深入理解.md) 阅读

---

## 目录

- [一、基础与数据模型](#一基础与数据模型)
- [二、架构组件](#二架构组件)
- [三、读写流程](#三读写流程)
- [四、Region 与分裂](#四region-与分裂)
- [五、Compaction 合并](#五compaction-合并)
- [六、RowKey 设计](#六rowkey-设计)
- [七、WAL 与容灾](#七wal-与容灾)
- [八、性能与运维](#八性能与运维)
- [九、对比与选型](#九对比与选型)
- [十、源码/进阶追问](#十源码进阶追问)

---

## 一、基础与数据模型

### Q1. HBase 是什么？核心特性？

**要点速答**：HBase 是基于 HDFS 的、面向列族、强一致、可海量扩展的 NoSQL 数据库，BigTable 开源实现。特性：海量存储（PB）、稀疏宽表（百万列）、随机读写、高扩展、高可用、行级强一致。

**深入展开**：本质是 LSM-Tree 存储 + Region 水平分片，写顺序追加极快，读靠 BlockCache + 合并 HFile。

---

### Q2. HBase 的数据模型四要素？

**要点速答**：`RowKey + ColumnFamily + Qualifier + Timestamp` 唯一定位一个 Cell 的值。

- **RowKey**：行键，字典序排序，唯一，决定数据分布。
- **ColumnFamily（列族）**：建表时定义，列的分组，一个表 1~3 个。
- **Qualifier（列）**：列族下的列，运行时动态添加，百万级。
- **Timestamp（版本）**：默认取写入时间戳，可多版本。

---

### Q3. 什么是列族？为什么列族要少而短？

**要点速答**：列族是列的物理分组，**同一列族的数据存在一起**（一个 Store = 一个 HFile 集合）。少（1~3 个）是因为：跨列族查询要扫多个 Store、flush 按列族触发互相拖累、列族名每 Cell 都存所以越短越省空间。

---

### Q4. HBase 的 Cell 是稀疏存储吗？

**要点速答**：是。HBase 只存有值的 Cell，空 Cell 不占空间——这是宽表（百万列但稀疏）省空间的关键。传统行存一行所有列都占位。

---

## 二、架构组件

### Q5. HBase 的核心组件有哪些？

**要点速答**：
- **HMaster**：DDL、Region 分配、负载均衡、故障恢复。**不参与读写**。
- **RegionServer**：承载数据，处理读写、WAL、MemStore、BlockCache、HFile。
- **ZooKeeper**：选主、Region 寻址入口、RS 上下线感知。
- **HDFS**：底层存储，HFile/WAL 多副本。
- **Client**：缓存元数据直连 RS 读写。

---

### Q6. HMaster 挂了影响读写吗？

**要点速答**：**短期不影响**。Client 缓存了 Region 位置，已有读写继续。HMaster 挂只影响：建表/删表、Region 分配与迁移、故障恢复、负载均衡。ZK 会从 backup-master 选新主接管。所以 HMaster 不在数据热点路径。

---

### Q7. Region 寻址过程？

**要点速答**：三步：
1. Client 从 ZK 读 `/hbase/meta-region-server`，找 `hbase:meta` 表位置。
2. 读 `hbase:meta`，按 RowKey 查目标 Region + 承载它的 RegionServer。
3. 直连该 RegionServer 读写，并**缓存** meta，后续直接用。

---

### Q8. RegionServer 内部核心组件？

**要点速答**：WAL（预写日志）+ 多个 Region（每个 Region 多个 Store/列族，每个 Store = MemStore + HFile×N）+ BlockCache（读缓存）。

---

## 三、读写流程

### Q9. HBase 写数据完整流程？

**要点速答**：
1. Client 定位 RegionServer。
2. 写 **WAL**（HDFS append，可选）。
3. 写 **MemStore**（内存有序）。
4. 返回 ack（写成功，不等 HFile）。
5. MemStore 满 → flush → HFile 落 HDFS。

写快因为：WAL append + MemStore 内存写都是**顺序写**，写完内存即返回。

---

### Q10. WAL 的 durability 级别？

**要点速答**：
- `SKIP_WAL`：不写 WAL，最快，可能丢数据。
- `ASYNC_WAL`：异步刷盘，兼顾性能与安全（常用）。
- `SYNC_WAL / FSYNC_WAL`：同步刷盘，最安全，最慢。
- `USE_DEFAULT`：用表配置。

---

### Q11. HBase 读数据完整流程？

**要点速答**：
1. 定位 RegionServer。
2. 查 **BlockCache**（读缓存），命中返回。
3. 查 **MemStore**（未 flush 的新数据）。
4. 查 **HFile**（带布隆过滤，多版本合并）。
5. 合并返回最新版本。

读比写慢，因为一个 RowKey 数据散落 MemStore + 多个 HFile，要扫合并。

---

### Q12. 为什么 HBase 写快读慢？

**要点速答**：写快——WAL+MemStore 顺序写、内存写、写完即返回。读慢——LSM 读要合并 MemStore + 多个 HFile 的多版本，且要靠布隆过滤、BlockCache 缓解，HFile 多时读放大严重。

---

## 四、Region 与分裂

### Q13. 什么是 Region？怎么切分？

**要点速答**：Region 是表按 **RowKey 范围**横向切分的分片，是负载均衡与并行的最小单位。一个 RegionServer 承载多个 Region，Region 大小超阈值（默认 ~10GB）自动 **Split**。

---

### Q14. Region Split 的过程？什么时候触发？

**要点速答**：Region 的 HFile 总大小超 `hbase.hregion.max.filesize`（默认 ~10GB）触发。过程：父 Region → 准备两个 daughter → 父下线 → 两个 daughter 上线。新版用 HFile 引用，分裂瞬间完成，后台补数据。

分裂策略：默认按大小，还有按前缀（`KeyPrefix`）、按分隔符、按 RS 上 Region 数等。

---

### Q15. 什么是热点？怎么解决？

**要点速答**：RowKey 设计不当（自增ID/时间前缀）导致所有写打到**某个 Region**，该 RS 过载，其他空闲。

解决：
1. **预分区**：建表指定 split points，预先多段。
2. **加盐/Hash 前缀**：RowKey 前加哈希散列。
3. **反转 RowKey**：手机号等前缀集中场景（牺牲范围查）。
4. **行键散列 + 时间倒序**组合。

---

### Q16. 预分区怎么做？

**要点速答**：建表时指定 split points：

```java
admin.createTable(tableDescriptor, splitKeys);  // splitKeys 是 byte[][] 边界
```

或 HBase Shell：

```
create 't', 'cf', SPLITS => ['10','20','30']
```

按预期 RowKey 分布定边界，让写一开始就散到多 Region，避免早期单 Region 热点 + 连续 split。

---

## 五、Compaction 合并

### Q17. 为什么需要 Compaction？

**要点速答**：MemStore 不断 flush 产生大量小 HFile，导致**读放大**（一个读扫很多文件）+ 空间放大（多版本/删除标记）+ NameNode 小文件压力。Compaction 合并小 HFile 成大 HFile，清理过期/删除数据。

---

### Q18. Minor 和 Major Compaction 区别？

**要点速答**：

| | Minor | Major |
|---|---|---|
| 文件 | 少量相邻小 HFile | 列族所有 HFile |
| 清理删除/过期 | 不全清 | 全部清理，只留最新版 |
| IO/耗时 | 小 | 大 |
| 频率 | 频繁自动 | 低频 + 手动 |

---

### Q19. Major Compaction 为什么生产常关掉自动？

**要点速答**：Major 是重 IO，合并所有 HFile，会占用大量磁盘带宽和 CPU，引发**读写抖动**。生产通常关掉自动（`hbase.hregion.majorcompaction=0`），改在**业务低峰手动** `major_compact 'table'` 触发，可控抖动。

---

### Q20. HBase 读放大/写放大/空间放大？

**要点速答**：
- **读放大**：HFile 多，一个读扫多文件 → Compaction 缓解。
- **写放大**：一次写被 WAL + MemStore + flush + 多次 Compaction 反复写 → 调 flush size/合并策略缓解。
- **空间放大**：多版本/删除标记占空间 → Major Compaction 清理。

LSM-Tree 三大放大的本质权衡。

---

## 六、RowKey 设计

### Q21. RowKey 设计原则？

**要点速答**：
1. **散列**：防热点，前缀 hash/加盐。
2. **范围**：一起查的数据 RowKey 相邻，复合键。
3. **定长**：补零保证字典序=数值序。
4. **倒序时间**：`Long.MAX_VALUE - ts` 让最新在前。
5. **短**：每 Cell 都存 RowKey，越短越省。

---

### Q22. RowKey 用时间倒序怎么设计？

**要点速答**：`Long.MAX_VALUE - timestamp` 做前缀，让最新数据字典序最小排在最前，范围扫描先扫到最新。但纯时间前缀会热点（同一秒都写一个 Region），需配合 **hash 前缀**：`hash(userid) + (MAX_VALUE - ts)`，既散列又时间倒序。

---

### Q23. 反转 RowKey 的适用场景与代价？

**要点速答**：手机号 `138xxxx` 前缀集中，反转成 `xxxx8831` 后散开，写打散。代价：**范围查询失效**，只能 Get。适合"按精确手机号查"，不适合范围扫。

---

## 七、WAL 与容灾

### Q24. WAL 的作用？RegionServer 挂了怎么恢复？

**要点速答**：WAL（Write-Ahead Log）记录所有写操作，写入先追加 WAL 再写 MemStore。RS 挂了重启时**回放 WAL** 恢复未 flush 的数据。ZK 感知 RS 下线后，HMaster 把它的 Region 重新分配给其他 RS，新 RS 回放 WAL（**Distributed Log Splitting**）。

---

### Q25. Multi-WAL 是什么？

**要点速答**：默认一个 RS 一个 WAL，多 Region 共享，WAL 成为写瓶颈。Multi-WAL 让一个 RS 用多个 WAL（按 Region 分组），提升写吞吐，代价是回放时 log split 更复杂。

---

### Q26. HBase 一致性级别？

**要点速答**：
- **STRONG**（默认）：读主 Region，强一致。
- **TIMELINE**：可读只读副本（`REGION_REPLICATION>1`），副本异步复制，最终一致。用于读吞吐扩展、容忍短暂过期。

---

## 八、性能与运维

### Q27. HBase BlockCache 的两种实现？

**要点速答**：
- **LruBlockCache**：堆内 LRU，默认，简单但占堆影响 GC。
- **BucketCache**：可堆外/SSD，大内存场景减轻 GC，读缓存主体放 BucketCache，索引/布隆留堆内 Lru。

组合模式：`LruBlockCache（索引+布隆） + BucketCache（数据块）`。

---

### Q28. 布隆过滤器在 HBase 的作用？

**要点速答**：HFile 带布隆，读时先判断 RowKey/Cell 是否可能在该 HFile，**不在就跳过该文件**，避免无谓磁盘 IO，缓解读放大。有 `ROW`（按行）和 `ROWCOL`（按行+列）两级。

---

### Q29. RegionServer 内存怎么分配？

**要点速答**：堆内存分给三块：MemStore（写）、BlockCache（读）、其他。`hfile.block.cache.size` + `hbase.regionserver.global.memstore.upperLimit` 之和一般 ≤ 80%，留余量给请求对象与 GC。堆外 BucketCache 额外。写多用大 MemStore，读多用大 BlockCache。

---

### Q30. HBase 性能调优要点？

**要点速答**：

| 项 | 做法 |
|---|---|
| RowKey | 散列+范围+定长+短 |
| 预分区 | 防 hotspot |
| Compaction | 关自动 Major，低峰手动 |
| flush size | 调大减少小 HFile |
| BlockCache | 大内存用 BucketCache 堆外 |
| Region 数 | 单 RS 不超 1000 |
| 列族 | 少而短 |
| GC | 大堆用 G1，避免长停顿 |
| MOB | 中大 value 用 MOB |

---

## 九、对比与选型

### Q31. HBase vs MySQL？

**要点速答**：MySQL 行存+SQL+ACID+二级索引，适合结构化业务、复杂查询、事务。HBase 列族+RowKey+单行强一致，适合海量宽表、写多读少、按 RowKey 随机读写、PB 级扩展。HBase 不支持 SQL 联表和二级索引（Phoenix 可补 SQL）。

---

### Q32. HBase vs Redis？

**要点速答**：Redis 内存级 KV，极快但数据量受内存限、不能海量持久。HBase 磁盘级海量存储、强一致、可扩展，读写慢于 Redis。常组合：Redis 做热点缓存，HBase 做持久宽表。

---

### Q33. HBase vs Cassandra？

**要点速答**：都是宽表列存 LSM。Cassandra 去中心化 P2P、最终一致、多 DC 强、无 Master。HBase 中心化（HMaster+ZK）、强一致、依赖 HDFS 生态。HBase 更适合 Hadoop 生态做强一致海量存储，Cassandra 更适合去中心化多 DC 最终一致。

---

### Q34. HBase 适合什么场景？

**要点速答**：PB 级、宽表稀疏、写多读少、按 RowKey 随机读写/范围扫、单行强一致。典型：订单流水、消息轨迹、用户画像、时序监控、风控特征库。

---

## 十、源码/进阶追问

### Q35. MemStore 的数据结构是什么？

**要点速答**：本质是**跳表（CellSkipListSet / ConcurrentSkipListMap）**，按 KeyValue 排序，支持并发且有序，便于 flush 时顺序写 HFile。新版引入 **MSLAB（MemStore-Local Allocation Buffer）** 和 **CompactingMemStore（_SEGMENT 段式）** 减少 GC 与碎片。

---

### Q36. HFile 的内部结构？

**要点速答**：自底向上：Data Block（64KB KeyValue）→ Meta Block（布隆）→ FileInfo → Load-on-open 区（DataIndex/MetaIndex/BloomIndex，启动载入内存）→ Fixed Trailer（指向各 index 偏移）。Data Block 是 IO 单位，整块入 BlockCache。

---

### Q37. flush 和 compaction 怎么避免阻塞写？

**要点速答**：MemStore 满 flush 是重 IO，期间该 Region 写阻塞。缓解：**MemStore Chunk/MSLAB** 减少 GC、**CompactingMemStore** 段式异步 flush、调大 `hbase.hregion.memstore.flush.size` 减少 flush 次数、Multi-WAL/Multi-Region 并行。RegionServer 整体水位高时触发 backpressure 阻塞写保护内存。

---

### Q38. HBase 怎么做二级索引？

**要点速答**：HBase 原生**无二级索引**（只按 RowKey）。方案：
1. **双写/索引表**：维护一张 `列值 -> RowKey` 的索引表，业务双写。
2. **Phoenix**：提供 SQL + 二级索引。
3. **Lily/HBase-Solr**：把索引字段同步到 Solr/ES 做全文/二级索引。
4. **Coprocessor（Observer）**：在写主表时钩子自动写索引表。

---

### Q39. Coprocessor 是什么？

**要点速答**：协处理器，类似存储过程/触发器，在 RegionServer 端执行代码，分两类：
- **Observer**：钩子（prePut/postPut 等），写主表时自动维护索引、审计。
- **Endpoint**：类似 RPC，在服务端做聚合（sum/avg），减少数据传输。

---

### Q40. HBase 的 LSM-Tree 与 B+ 树对比？

**要点速答**：B+ 树（MySQL）随机写要改页+写日志，写放大、点查快。LSM-Tree（HBase）写只追加 WAL+内存，顺序写极快；读要合并多 SSTable，靠 Compaction+缓存缓解。写多读少 + 海量 → LSM 优；点查频繁 + 强事务 → B+ 优。

---

## 附：一句话速答卡

| 问 | 答 |
|---|---|
| HBase 核心 | 列族宽表+LSM+Region 分片 |
| 组件 | HMaster(元数据) / RS(数据) / ZK(寻址选主) / HDFS |
| 写流程 | WAL → MemStore → flush → HFile |
| 读流程 | BlockCache → MemStore → HFile(布隆) |
| 热点 | 预分区/加盐/反转 RowKey |
| 合并 | Minor(小) / Major(全清,低峰手动) |
| RowKey | 散列+范围+定长+短+倒序时间 |
| 强一致 | 默认 STRONG，读主 Region |
| WAL 恢复 | RS 挂了回放 WAL + 重新分配 Region |
| 适合场景 | PB 级宽表写多读少随机读写 |

---

> 配套架构文档见 [hbase整体架构和深入理解.md](hbase整体架构和深入理解.md)。
