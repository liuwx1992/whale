# Elasticsearch 整体架构和深入理解

> **文档版本**: v1.0
> **创建日期**: 2026-08-31
> **编写人**: weixin.liu
> **用途**: 从整体架构到倒排索引/分片副本/读写/聚合/检索机制全面拆解 Elasticsearch，供后续大模型阅读与排障

---

## 0. 一句话定位

Elasticsearch 是一个**分布式、RESTful、近实时（NRT）**的搜索与分析引擎，基于 Apache Lucene，用"倒排索引 + 分片副本 + 网关持久化"实现海量全文检索与多维聚合。它是"存算一体"的检索数据库。

---

## 1. 适用场景

| 维度 | 说明 |
|---|---|
| 适用 | 全文检索、日志/指标分析（ELK）、多维聚合、模糊/拼音/地理位置查询、近实时大数据分析 |
| 不适用 | 强事务、复杂联表、精确点查高频更新（这些用 MySQL/HBase） |
| 近实时 | 写入到可检索有 ~1s 延迟（refresh 间隔），非毫秒强一致 |

典型场景：日志检索（ELK）、商品/订单搜索、监控指标、安全分析、应用搜索。

---

## 2. 整体架构总览

```
                     ┌─────────────────────────┐
                     │      REST Client / Kibana │
                     └────────────┬─────────────┘
                                  │ HTTP 9200
                     ┌────────────▼─────────────┐
                     │   Node (Elasticsearch 实例)│
                     │  ┌────────────────────┐   │
                     │  │  Master / Data /    │   │
                     │  │  Coordinating /    │   │
                     │  │  Ingest / ML 节点角色│   │
                     │  └─────────┬──────────┘   │
                     │            │ 持有若干分片   │
                     │  ┌─────────▼──────────┐   │
                     │  │ Index              │   │
                     │  │  ├ Shard(主)        │   │
                     │  │  └ Replica(副本)    │   │
                     │  │     └ Segment(段)   │   │
                     │  └────────────────────┘   │
                     └────────────┬───────────────┘
                                  │ 集群发现/选主
                                  ▼
                          ┌──────────────────┐
                          │  Cluster State   │
                          │ (master 维护广播) │
                          └──────────────────┘
```

核心概念分层（**重要**，初学最容易混）：

| 层级 | 概念 | 说明 |
|---|---|---|
| 逻辑层 | **Index（索引）** | 类比"数据库/表"，文档集合 |
| 逻辑层 | **Document（文档）** | 一条 JSON 记录 |
| 逻辑层 | **Field/Mapping** | 字段 + 类型映射（类似 schema） |
| 物理层 | **Shard（分片）** | 一个 Index 横向切成多个 Shard，一个 Shard = 一个 Lucene 索引 |
| 物理层 | **Replica（副本）** | 每个 Shard 的副本，高可用 + 读扩展 |
| 物理层 | **Segment（段）** | Lucene 真正的倒排索引文件，**不可变**，写入即生成 |

---

## 3. 集群与节点角色

一个 ES 集群 = 多个 Node。Node 角色可组合：

| 角色 | 职责 |
|---|---|
| **master-eligible** | 候选主，参与选主；当选后管集群状态（索引/分片位置/节点状态） |
| **data** | 承载数据分片，负责读写与检索、聚合 |
| **coordinating** | 协调节点：接收请求、分发到分片、汇总结果（默认所有节点都可协调） |
| **ingest** | 预处理管道：写入前转换/enrich（类似 logstash filter） |
| **ml** | 机器学习任务节点 |
| **hot/warm/cold** | 数据分层（节点属性 + ILM），热温冷分级存储 |

**选主**：master-eligible 节点间基于 **Raft 变种（ZenDiscovery / ClusterBootstrap）** 选主，过半数（quorum）当选。7.x 后用 `cluster.initial_master_nodes` 引导，自动发现。

**脑裂防护**：要求 master 候选过半才形成集群；data 节点不参与选主，避免数据节点膨胀影响选主稳定性。

---

## 4. 索引、分片与副本（物理分布核心）

### 4.1 Index → Shard → Replica

```
Index "order" (shards=3, replicas=1)
  Shard0:  主P0(NodeA)  副R0(NodeB)
  Shard1:  主P1(NodeB)  副R1(NodeC)
  Shard2:  主P2(NodeC)  副R2(NodeA)
```

- 一个 Index 配 `number_of_shards`（主分片数，**建表时定，不可改**）和 `number_of_replicas`（副本数，可动态改）。
- 主分片散在不同 data 节点；副本分片不能与主分片同节点（高可用）。
- **读**：主+副本都可读（读扩展）。
- **写**：只写主分片，主异步复制到副本（**一致性级别**：one/quorum/all）。

### 4.2 路由：文档落在哪个分片

```
shard = hash(routing) % numberOfPrimaryShards
```

默认 `routing = _id`。所以分片数一旦定就不能改（改了路由变，文档找不到）。要改只能 **reindex / split / shrink**。

### 4.3 主分片数怎么定

- 单分片大小建议 **30~50GB**（搜索型）或更小（日志）。
- 分片数 = 预估数据量 / 单分片目标。
- 过多分片：状态大、开销大、聚合慢；过少：单分片过大、无法横向扩展、查询慢。

---

## 5. 倒排索引（ES 检索的灵魂）

### 5.1 正排 vs 倒排

- **正排**：文档 → 包含哪些词（MySQL like 走全表扫，慢）。
- **倒排**：词 → 出现在哪些文档（词项字典 Term Dictionary + 倒排链表 Posting List），搜索直接定位，极快。

### 5.2 倒排索引结构

```
Term Dictionary（词项字典, 字符串 -> 偏移）
    │
    ▼ FST 有限状态机压缩, 落 Term Index (.tip)
Posting List（倒排链表: docId 列表 + 词频 + 位置 + offset）
```

- **Term Dictionary**：所有词项，存磁盘。
- **Term Index**：FST（Finite State Transducer）压缩的词项索引，常驻内存，快速定位 Term Dictionary 偏移。
- **Posting List**：每个 Term 对应的文档列表，用 **Roaring Bitmap / FOR/Roaring** 压缩，存 docId、词频(tf)、位置(position)、偏移(offset)。
- **Doc Values**：正排（docId → 字段值），用于**排序、聚合、script**，列存格式。文本默认不存 Doc Values。

### 5.3 检索流程（AND 查询为例）

```
"elasticsearch AND cluster"
  -> 用 Term Index 定位两个词的 Posting List
  -> 取两个倒排链表的交集(docId)
  -> 按 TF-IDF / BM25 打分(相关性)
  -> 取 docId 取 _source 返回
```

### 5.4 相关性打分 BM25

ES 默认用 **BM25**（Okapi BM25），比 TF-IDF 更优：
- 词频饱和：tf 不再线性增长，避免某词高频刷分。
- 字段长度归一化：短字段匹配权重高。
- 关键参数 `k1`（词频饱和度，默认 1.2）、`b`（长度归一化，默认 0.75）。

---

## 6. 写入流程（核心高频）

```
Client -> Coordinating Node
  -> 1) 路由: shard = hash(_id) % primaries, 定位主分片所在 Node
  -> 2) Primary Node 写入:
        a. 写 translog(预写日志, 落盘保证不丢)
        b. 写 Indexing Buffer(内存)
  -> 3) Primary 复制到 Replica(并行, 按 consistency 等待)
  -> 4) 达成一致性(one/quorum/all) -> 返回 client 成功

[后台, 默认 1s]
  refresh: Indexing Buffer -> 生成新 Segment(可被搜索)   <- 近实时的来源
[后台, 默认 30min 或 translog 满]
  flush: Segment 落盘 fsync + 清 translog              <- 持久化
[后台]
  merge: 多个小 Segment 合并成大 Segment, 物理删除已删文档
```

### 6.1 三个关键后台动作

| 动作 | 周期 | 作用 | 性能影响 |
|---|---|---|---|
| **refresh** | 默认 1s | Buffer → Segment（内存），让数据可搜 | 生成新段，太多小段慢；写多可调大间隔提吞吐 |
| **flush** | 默认 30min 或 translog 满 | Segment fsync 落盘 + 清 translog | 重 IO，但保证持久 |
| **merge** | 后台策略 | 合并小 Segment → 大 Segment，物理删已删文档 | 重 IO/磁盘 |

### 6.2 translog 与持久化

- **translog**：写前先追加 translog（fsync），保证 refresh/flush 之间宕机不丢数据。
- translog 落盘频率：`index.translog.durability`（`request` 每次请求 fsync 最安全但慢；`async` 异步 fsync 快但可能丢最近一段）。

### 6.3 一致性级别

- `one`：主分片写成功即返回（最快）。
- `quorum`（默认）：主 + 过半副本。
- `all`：主 + 全部副本（最安全最慢）。
- `wait_for_active_shards`：现代写法，等 N 个分片活跃。

---

## 7. Segment 段（Lucene 层）

- **Segment 不可变**：写入生成新段，不修改老段。这是 ES 强在"写多读"和"读并发无锁"的根因。
- **删除 = 墓碑标记**：删一个文档只是标记，物理空间靠 merge 回收。
- **更新 = 删旧 + 写新**：ES 没有"原地更新"，更新即 tombstone + 新文档。
- **merge**：合并段时才物理清除墓碑。

### 7.1 为什么 refresh 后才可搜

未 refresh 的数据在 Indexing Buffer（内存），搜不到；refresh 把 buffer 生成 Segment 才进入可搜集合。所以有 ~1s 近实时延迟。批量导入可手动 `refresh=false` 提速，最后再 refresh。

---

## 8. Mapping 与数据类型

- **Mapping**：定义字段类型 + 分词器 + 是否索引 + 是否聚合（doc_values）。
- **动态映射**：首条文档自动猜类型，生产建议**禁用 dynamic** 显式 mapping，避免类型漂移（如某字段首条是数字后变字符串导致映射冲突）。
- 关键类型：`text`（分词全文检索）vs `keyword`（精确值、聚合、不分词）。

### 8.1 text vs keyword（高频）

| | text | keyword |
|---|---|---|
| 分词 | 是，建倒排 | 否，整体 |
| 全文检索 | 适合 | 不适合（除非 term 精确） |
| 聚合/排序 | 慢/不支持 | 适合（用 doc_values） |
| 用途 | 标题/正文 | id/标签/枚举 |

常配 `text + keyword` 子字段（`fields.keyword`）兼顾检索与聚合。

---

## 9. 分词器 Analyzer

`Analyzer = Character Filter + Tokenizer + Token Filter`。

- **Character Filter**：如 `html_strip` 去标签。
- **Tokenizer**：分词，如 `standard`、`ik_max_word`（中文）。
- **Token Filter**：小写、停用词、同义词、词干化。

中文必须配 IK/Pinyin 分词器，否则按字切检索效果差。

---

## 10. 查询与聚合

### 10.1 查询分类

- **Query DSL**：JSON 描述查询，分 `Leaf（match/term/range）` 和 `Compound（bool: must/should/must_not/filter）`。
- **must/should/filter 区别**：
  - `must`：必须匹配，参与打分。
  - `should`：至少满足 N（对 bool 内无 must 时默认至少 1）。
  - `filter`：必须匹配，**不打分**（结果可缓存，过滤型查询首选）。
  - `must_not`：必须不匹配，不打分。

### 10.2 聚合

- **Bucket 桶聚合**：`terms/range/date_histogram`，分桶。
- **Metric 指标聚合**：`sum/avg/max/min/cardinality`。
- **Pipeline 管道聚合**：对聚合结果再聚合。

聚合靠 **Doc Values（列存）**，所以数值/keyword 默认开 doc_values，text 默认关。

---

## 11. 读流程（搜索）

```
Client -> Coordinating Node
  -> 1) 拆分请求到各相关分片(主或副本)
  -> 2) 各分片本地查询 + 取 TopN 候选(返回 docId + 打分)
  -> 3) Coordinating 合并各分片候选, 重新排序取 TopN
  -> 4) 按 docId 向分片取 _source 返回
```

**两阶段查询**：先各分片取候选（轻），再协调节点合并取最终（取 _source 重）。这是 ES 分布式检索的典型 scatter-gather。

### 11.1 深度分页

- `from + size`：协调节点要向每个分片取 `from + size` 条再合并，深分页（如 from=10000）内存爆炸——默认上限 `index.max_result_window=10000`。
- **scroll**：游标式，适合全量导出，但占资源、不能实时。
- **search_after**：基于上一页排序值翻页，实时且省内存，生产推荐。

---

## 12. 段合并 Merge

- 小段太多 → 查询要扫多个段，慢。
- 合并策略：TieredMergePolicy，按段大小分层合并。
- 合并是重 IO，写入高峰需限流（`indices.store.index.refresh`/merge 线程）。
- 合并才能**物理删除**墓碑文档，回收空间。

---

## 13. 高可用与故障恢复

- **主分片所在节点挂**：选一个副本提升为主（cluster state 重分配），副本不足则集群 `red`。
- **副本**：高可用 + 读扩展，但不提升写吞吐（写仍只写主）。
- **gateway**：集群级元数据持久化，节点重启恢复索引/分片位置。
- **recovery**：分片恢复（主 → 副本同步、translog replay）。

---

## 14. 性能与运维要点

| 关注点 | 做法 |
|---|---|
| 分片数 | 单分片 30~50GB，避免过多小分片 |
| 副本 | 搜索读多用 1~2 副本，写为主 |
| mapping | 禁 dynamic，显式；text/keyword 区分 |
| doc_values | 聚合字段开，text 关 |
| refresh | 写多调大间隔（如 30s）提吞吐，搜多默认 1s |
| bulk | 批量写，单批 5~15MB |
| 深分页 | search_after，别用深 from+size |
| filter | 过滤型用 filter（缓存、不打分） |
| 冷热分层 | hot SSD / warm HDD / cold 对象存储，ILM 索引生命周期 |
| 堆 | ≤ 31GB（指针压缩阈值），留一半内存给 Lucene 段/文件系统缓存 |
| GC | 大堆用 G1/ZGC，监控 old GC 长停顿 |
| 别用 ES 当主存 | 不支持强事务，做检索/分析，主数据放 MySQL/HBase |

---

## 15. ELK/EFK 生态

- **Beats**（Filebeat/Metricbeat）：轻量采集。
- **Logstash**：管道，解析/过滤/转发。
- **Elasticsearch**：存储检索。
- **Kibana**：可视化。
- EFK = 用 Fluentd/Fluent Bit 替 Logstash。

---

## 16. ES vs 其他

| 维度 | ES | MySQL | HBase | Redis |
|---|---|---|---|---|
| 数据模型 | 文档/倒排 | 行列表 | 列族 | KV |
| 检索 | 全文+聚合强 | like 慢 | RowKey | KV/集合 |
| 事务 | 弱 | ACID | 单行强 | 弱 |
| 近实时 | ~1s | 实时 | 实时 | 实时 |
| 写 | 中 | 中 | 极快 | 极快 |
| 适合 | 检索/日志/分析 | 业务/事务 | 海量宽表 | 缓存/快取 |

---

## 17. 一张图串起核心概念

```
集群 -> 多 Node(角色: master/data/coordinating)
索引 Index -> 切成 Shard(主) + Replica(副本), 路由 hash(_id)%primaries
Shard = 一个 Lucene 索引 -> Segment 段(不可变, 倒排索引)
写: translog(预写) -> Buffer -> refresh(1s, 成段可搜) -> flush(落盘) -> merge(合并清理)
读: coordinating 分发 -> 各分片两阶段查询(候选+取源) -> 合并返回
打分: BM25; 聚合: Doc Values 列存
```

记住这条链，就抓住 ES 全部骨架：**倒排索引、分片副本、写近实时(refresh/flush/merge)、读 scatter-gather、BM25 + Doc Values**。
