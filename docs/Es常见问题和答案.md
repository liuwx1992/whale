# Elasticsearch 常见问题和答案

> **文档版本**: v1.0
> **创建日期**: 2026-08-31
> **编写人**: weixin.liu
> **用途**: 系统梳理 ES 高频面试题与标准答案，每题给"要点速答 + 深入展开"，配合 [Es整体架构和深入理解.md](Es整体架构和深入理解.md) 阅读

---

## 目录

- [一、基础与概念](#一基础与概念)
- [二、集群与节点](#二集群与节点)
- [三、分片副本与路由](#三分片副本与路由)
- [四、倒排索引](#四倒排索引)
- [五、Mapping 与分词](#五mapping-与分词)
- [六、读写流程](#六读写流程)
- [七、查询与聚合](#七查询与聚合)
- [八、深分页与性能](#八深分页与性能)
- [九、高可用与运维](#九高可用与运维)
- [十、对比与选型](#十对比与选型)
- [十一、进阶追问](#十一进阶追问)

---

## 一、基础与概念

### Q1. ES 是什么？核心特性？

**要点速答**：基于 Lucene 的分布式、RESTful、近实时（NRT）搜索与分析引擎。特性：全文检索、海量分布式、近实时、RESTful JSON、聚合分析、高可用（分片副本）。

---

### Q2. ES 的近实时（NRT）是什么意思？

**要点速答**：写入到可检索有 ~1s 延迟（默认 refresh 间隔 1s），不是毫秒强一致。原因：数据先写内存 Buffer，refresh 后才生成 Segment 可被搜索。追求写吞吐可调大 refresh 间隔。

---

### Q3. ES 集群、节点、索引、分片、文档的关系？

**要点速答**：
- **Cluster**：多 Node 组成，由 master 维护 cluster state。
- **Node**：一个 ES 实例，有角色（master/data/coordinating/ingest）。
- **Index**：文档集合，逻辑层。
- **Shard**：Index 横切分片，物理层，一个 Shard = 一个 Lucene 索引。
- **Document**：一条 JSON。

分主分片（primary）+ 副本分片（replica）。

---

## 二、集群与节点

### Q4. ES 节点角色有哪些？master 怎么选？

**要点速答**：master-eligible（候选主，管集群状态）、data（数据）、coordinating（协调，所有节点默认）、ingest（预处理）、ml（机器学习）。master 由 master-eligible 节点间**过半数选主**（Raft 变种）。data 不参与选主，避免大集群选主不稳。

---

### Q5. master 挂了怎么办？脑裂怎么防？

**要点速答**：master 挂，剩余 master-eligible 重新选主（过半），cluster state 由新主维护，分片重分配。脑裂防护：选主要求**过半 quorum**，不满足过半不形成集群，避免双主。7.x 后 bootstrap 用 `cluster.initial_master_nodes` 引导。

---

### Q6. coordinating 节点的作用？能去掉吗？

**要点速答**：协调节点接收请求、路由到分片、汇总结果（scatter-gather）。默认所有节点都能协调。大集群可设**专用协调节点**（master+data 角色都关），分担搜索的合并压力，保护 data 节点。

---

## 三、分片副本与路由

### Q7. 分片和副本的区别？读写在主还是副本？

**要点速答**：
- **主分片 primary**：数据原始分片，**写只写主**。
- **副本 replica**：主的副本，**可读**，高可用 + 读扩展。
- 路由：`shard = hash(routing) % number_of_primary_shards`，默认 routing=`_id`。
- 主副本不能同节点（高可用）。

---

### Q8. 为什么主分片数建表后不能改？

**要点速答**：因为路由公式 `hash(_id) % primaries` 依赖主分片数。改了，文档落到不同分片，旧数据找不到。要变只能 **split/shrink（倍数缩放）/ reindex 到新索引**，再切换别名。

---

### Q9. 主分片数怎么定？

**要点速答**：单分片建议 **30~50GB**（搜索）或更小（日志）。`shards ≈ 预估总量 / 单分片目标`。兼顾：分片太少→单分片过大查慢、扩展难；太多→cluster state 大、开销大、聚合慢。预留扩展余量但别过度。

---

### Q10. 副本数能动态改吗？改了影响什么？

**要点速答**：能，`number_of_replicas` 动态可改。加副本提升读吞吐 + 高可用，但增加写放大（主复制到更多副本）和磁盘占用。写为主副本可少甚至 0，搜索读多用 1~2。

---

## 四、倒排索引

### Q11. 倒排索引是什么？为什么 ES 搜索快？

**要点速答**：倒排 = 词 → 出现在哪些文档（Posting List），区别于正排（文档→词）。搜索时直接按词定位文档，O(1) 级，远快于 MySQL like 的全表扫。结构：Term Index（FST 压缩，常驻内存）→ Term Dictionary（磁盘）→ Posting List（docId+tf+position，Roaring Bitmap 压缩）。

---

### Q12. FST 是什么？Term Index 的作用？

**要点速答**：FST（Finite State Transducer）有限状态机，把词项字典压缩到内存常驻的 Term Index。检索时先用 FST 在内存定位 Term Dictionary 的磁盘偏移，再读 Posting List，避免每次扫整个字典磁盘，极快。

---

### Q13. Posting List 怎么压缩？

**要点速答**：用 **Roaring Bitmap / FOR（Frame of Reference）** 等压缩。把递增 docId 差分 + 位打包，按 65536 分块用位图或数组。集合交集/并集高效，支持 filter 缓存。

---

### Q14. Doc Values 是什么？为什么需要它？

**要点速答**：Doc Values 是**正排列存**（docId → 字段值），与倒排（词→docId）互补。排序、聚合、script 需要"按文档取字段值"，倒排不好做，靠 Doc Values 列存快速取。数值/keyword 默认开，text 默认关（text 不常用于聚合）。

---

### Q15. BM25 打分是什么？

**要点速答**：ES 默认相关性算法 BM25（Okapi），优于 TF-IDF。词频饱和（tf 不再线性涨）、字段长度归一化（短字段权重高）。参数 `k1`（默认1.2，词频饱和度）、`b`（默认0.75，长度归一）。

---

## 五、Mapping 与分词

### Q16. text 和 keyword 区别？

**要点速答**：
- **text**：分词建倒排，适合全文检索（标题/正文），不支持/慢于聚合。
- **keyword**：不分词整体存，适合精确值、聚合、排序（id/标签/枚举），用 Doc Values 聚合。
常配 `text + keyword 子字段`兼顾。详见架构文档 §8.1。

---

### Q17. 动态映射为什么生产要禁？

**要点速答**：首条文档自动猜类型，后续类型不符会冲突报错（如某字段先入数字后入字符串）。生产用 `"dynamic": false` 或 strict，显式定义 mapping 防类型漂移。或用 Index Template/Component Template 规范。

---

### Q18. 分词器 Analyzer 的组成？

**要点速答**：`Character Filter + Tokenizer + Token Filter`。Char Filter 预处理（去 HTML）、Tokenizer 切词（standard/ik）、Token Filter 加工（小写/停用词/同义词/词干）。中文用 IK（ik_max_word/ik_smart）或拼音分词器。

---

### Q19. 自定义分词器怎么配？

**要点速答**：在 index settings 的 `analysis` 里定义：

```json
"settings": {
  "analysis": {
    "analyzer": {
      "my_analyzer": {
        "type": "custom",
        "tokenizer": "ik_max_word",
        "filter": ["lowercase", "stop", "synonym"]
      }
    }
  }
}
```
mapping 字段 `"analyzer": "my_analyzer"`。

---

## 六、读写流程

### Q20. ES 写入完整流程？

**要点速答**：
1. 协调节点路由 `hash(_id)%primaries` 定位主分片。
2. 主节点：写 **translog**（fsync 持久）+ 写 **Indexing Buffer**（内存）。
3. 主复制到副本（并行，按一致性等）。
4. 达成一致性（one/quorum/all）返回成功。
5. 后台 refresh（1s）Buffer→Segment 可搜；flush（30min）落盘清 translog；merge 合并段。

详见架构文档 §6。

---

### Q21. refresh / flush / merge 区别？

**要点速答**：

| | refresh | flush | merge |
|---|---|---|---|
| 周期 | 默认 1s | 默认 30min/translog满 | 后台策略 |
| 作用 | Buffer→内存段（可搜） | 段 fsync 落盘+清 translog | 合并小段→大段，物理清墓碑 |
| 影响 | 近实时来源 | 持久化 | 重IO，回收空间 |

---

### Q22. 为什么 ES 是近实时而非实时？

**要点速答**：写先入内存 Buffer 不可搜，要等 refresh（1s）生成 Segment 才可搜。所以刚写的数据 ~1s 后才能查到。要实时可调 refresh 间隔或写后手动 `?refresh=true`（牺牲性能）。

---

### Q23. translog 的作用？durability 怎么选？

**要点速答**：translog 是预写日志，保证 refresh/flush 之间宕机不丢。`index.translog.durability`：`request`（每次请求 fsync 最安全慢）、`async`（异步 fsync 快但可能丢最近一段）。批量导入可用 async 提速。

---

### Q24. ES 更新文档怎么实现的？

**要点速答**：ES 没有"原地更新"，**更新 = 标记旧文档为删除（墓碑）+ 写入新文档**。物理空间靠 merge 回收。所以频繁更新会产生墓碑和段膨胀，需 merge 清理，性能不如新增。

---

### Q25. 一致性级别 one/quorum/all？

**要点速答**：
- `one`：主写成功即返回（快，可能少副本）。
- `quorum`（默认）：主+过半副本。
- `all`：主+全部副本（最安全慢）。
现代用 `wait_for_active_shards=N` 指定活跃分片数。

---

## 七、查询与聚合

### Q26. bool 查询的 must/should/filter/must_not 区别？

**要点速答**：
- `must`：必须匹配，参与打分。
- `should`：可选，参与打分（无 must 时至少满足 1）。
- `filter`：必须匹配，**不打分**（结果缓存，过滤型首选）。
- `must_not`：必须不匹配，不打分。

能用 filter 别用 must（不打分+缓存）。

---

### Q27. match 和 term 区别？

**要点速答**：
- `match`：对查询词分词后查倒排，适合 text 全文检索。
- `term`：不分词精确匹配，适合 keyword/数值/布尔。误用 term 查 text（已分词的小写词项）会查不到。

---

### Q28. 聚合分几种？靠什么实现？

**要点速答**：三种：Bucket（桶，terms/range/date_histogram）、Metric（指标，sum/avg/cardinality）、Pipeline（对结果再聚合）。聚合靠 **Doc Values 列存**，所以数值/keyword 默认开，text 默认关。

---

### Q29. cardinality 聚合为什么是近似？

**要点速答**：`cardinality`（去重计数）用 **HyperLogLog++** 算法，近似估算省内存，精度可调（`precision_threshold`）。精确去重内存爆炸，所以大数据去重用近似。

---

## 八、深分页与性能

### Q30. 深分页为什么慢？ES 默认上限？

**要点速答**：`from + size` 深分页时，协调节点要向每个分片取 `from+size` 条再合并，from 越大内存越大（如 from=10000 各分片取 1 万再合并）。默认上限 `index.max_result_window=10000`。

---

### Q31. 深分页方案？

**要点速答**：
- **scroll**：游标式快照，适合全量导出，占资源、非实时。
- **search_after**：基于上一页排序值翻页，**实时省内存**，生产推荐（要求排序字段唯一）。
- 避免深 from+size；业务上限制翻页深度。

---

### Q32. ES 写入怎么优化？

**要点速答**：
1. **bulk 批量**：单批 5~15MB，兼顾吞吐与延迟。
2. 调大 `refresh_interval`（如 30s）减少段生成。
3. `translog.durability=async` + 加大 translog flush 阈值。
4. 副本设 0（批量导入期间），导入后恢复。
5. 用 bulk + 多线程客户端，分散到多分片。

---

## 九、高可用与运维

### Q33. 主分片所在节点挂了怎么办？

**要点速答**：选一个副本提升为新主，cluster state 重新分配。副本不足则集群状态变 `red`（有主分片缺失）。`yellow`：有副本未分配；`green`：全部分片就绪。

---

### Q34. 集群 red/yellow/green 含义？

**要点速答**：
- **green**：主副本全就绪。
- **yellow**：主全在，有副本未分配（如单节点无副本）。
- **red**：有主分片缺失，数据不完整。

---

### Q35. ES 堆内存为什么建议 ≤ 31GB？

**要点速答**：JVM 指针压缩（CompressedOops）在 32GB 以下生效，省内存。超 31GB 指针非压缩反而可能更慢。且要**留一半物理内存给 Lucene 段/系统文件缓存**（ES 大量靠 mmap 读段），所以堆不超物理内存一半，且 ≤31GB。

---

### Q36. 冷热分层怎么做？

**要点速答**：节点打 `node.hot/warm/cold` 属性，索引设 `index.routing.allocation.include` 路由。配 **ILM（Index Lifecycle Management）**：热期 hot SSD → 转 warm HDD → 转 cold 对象存储 → 删除。日志场景典型。

---

### Q37. 为什么不建议用 ES 做主数据库？

**要点速答**：ES 不支持强事务、不支持 join、更新性能差（墓碑+重写）、最终一致（近实时）、成本高。适合检索/分析侧，主数据放 MySQL/HBase，ES 做搜索副本（CDC 同步）。

---

## 十、对比与选型

### Q38. ES vs MySQL？

**要点速答**：MySQL 行存+SQL+事务+索引，业务主存。ES 倒排+全文+聚合，搜索分析。MySQL like 全表慢，ES 倒排极快。常组合：MySQL 主存 + ES 搜索副本（同步 binlog/Canal）。

---

### Q39. ES vs Solr？

**要点速答**：都基于 Lucene。Solr 传统、强 facet/文本处理、schema 强；ES 分布式原生、RESTful、近实时、聚合、生态（ELK）现代。新项目选 ES。

---

### Q40. ES 适合什么场景？

**要点速答**：全文检索、日志/指标分析（ELK）、商品/订单搜索、多维聚合、模糊/拼音/地理位置、近实时大数据分析。不适合强事务、复杂联表、精确点查高频更新。

---

## 十一、进阶追问

### Q41. ES 怎么实现分布式检索的 scatter-gather？

**要点速答**：两阶段。**Phase 1（scatter）**：协调节点把请求分发到相关分片（主或副本），各分片本地查 + 取 TopN 候选（docId+score）返回。**Phase 2（gather）**：协调节点合并各分片候选重新排序取最终 TopN，再按 docId 向分片取 `_source` 返回。深分页因 gather 要 from+size 而膨胀。

---

### Q42. Segment 不可变有什么好处坏处？

**要点速答**：
- 好处：读无锁并发安全、易缓存、易压缩、可 mmap。
- 坏处：删除/更新要墓碑，物理空间靠 merge 回收；段太多查询要扫多段（读放大）。

---

### Q43. ES 的 cache 有哪些？

**要点速答**：
- **Node Query Cache**：filter 查询结果缓存（按 segment）。
- **Shard Request Cache**：整请求结果缓存（size=0 的聚合/count 缓存）。
- **Field Data**：text 聚合时的堆内列存（默认关，慎用易 OOM）。
- **Page Cache（OS）**：段文件 mmap 走系统缓存，最重要。

---

### Q44. ES 怎么保证写入不丢？

**要点速答**：translog fsync（durability=request 每请求落盘）保证 refresh/flush 之间宕机可回放。flush 后 Segment 落盘 fsync + 清 translog，持久完成。副本异步复制到副本节点多副本容灾。

---

### Q45. reindex 和 update_by_query 区别？

**要点速答**：
- `reindex`：把一个索引文档复制到另一个索引（常用于改 mapping/分片数），源不变。
- `update_by_query`：在原索引按条件批量更新文档（仍是墓碑+重写）。

改 mapping/分片数只能 reindex 到新索引再切别名。

---

## 附：一句话速答卡

| 问 | 答 |
|---|---|
| ES 核心 | Lucene+倒排+分片副本+近实时 |
| 近实时原因 | refresh 1s 才成段可搜 |
| 倒排 | 词→文档，FST+TermDict+PostingList |
| 打分 | BM25 |
| 聚合靠 | Doc Values 列存 |
| text vs keyword | text 分词检索，keyword 精确聚合 |
| 分片数 | 建表定不可改，单分片30-50G |
| 路由 | hash(_id)%primaries |
| 深分页 | search_after |
| 写三动作 | refresh/flush/merge |
| 堆 | ≤31G，留一半给缓存 |
| 不做主存 | 无事务更新差 |

---

> 配套架构文档见 [Es整体架构和深入理解.md](Es整体架构和深入理解.md)。
