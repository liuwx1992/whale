# RocketMQ 常见问题和答案

> **文档版本**: v1.0
> **创建日期**: 2026-08-31
> **编写人**: weixin.liu
> **用途**: 系统梳理 RocketMQ 高频面试题与标准答案，每题给"要点速答 + 深入展开"，配合 [RocketMQ整体架构和深入理解.md](RocketMQ整体架构和深入理解.md) 阅读

---

## 目录

- [一、基础与架构](#一基础与架构)
- [二、存储机制](#二存储机制)
- [三、消息发送](#三消息发送)
- [四、消息消费](#四消息消费)
- [五、顺序消息](#五顺序消息)
- [六、事务消息](#六事务消息)
- [七、延迟/重试/死信](#七延迟重试死信)
- [八、高可用与可靠性](#八高可用与可靠性)
- [九、对比与进阶](#九对比与进阶)

---

## 一、基础与架构

### Q1. RocketMQ 是什么？核心特性？

**要点速答**：分布式、高吞吐、高可靠消息中间件（阿里开源→Apache）。特性：高可靠（金融级）、**事务消息**、顺序消息、延迟消息、死信、丰富消费模式（集群/广播、推/拉）、NameServer 无中心路由。

---

### Q2. RocketMQ 四大角色？

**要点速答**：
- **NameServer**：路由注册中心，无中心互独立。
- **Broker**：消息存储转发主体，主从。
- **Producer**：生产者，发消息。
- **Consumer**：消费者，拉消息消费。

---

### Q3. 为什么用 NameServer 而非 ZooKeeper？

**要点速答**：NameServer 无中心、互独立、最终一致，每个节点持全量路由，Broker 向每个上报，任一挂不影响。比 ZK 轻：无 leader 选举/数据同步开销、部署简单。客户端定期拉路由，Broker 异常靠心跳超时剔除，路由最终一致。

---

### Q4. NameServer 之间怎么同步？

**要点速答**：**不同步**。NameServer 节点互独立，Broker 主动向每个 NameServer 上报路由。所以各 NameServer 数据可能短暂不一致，靠客户端定期拉 + 心跳超时最终一致。任一 NameServer 可服务。

---

### Q5. Topic / Queue / Group 关系？

**要点速答**：
- **Topic**：逻辑主题。
- **MessageQueue**：Topic 物理队列（默认4个），分布多 Broker，是负载均衡/顺序/并行的单位。
- **Producer/Consumer Group**：同类实例集合。集群消费下同 Group 内 Queue 分摊到各实例。

---

## 二、存储机制

### Q6. RocketMQ 怎么存消息的？

**要点速答**：所有 Topic 消息**全部顺序追加写到 CommitLog**（一个文件），不按 Topic 分。每个 `Topic+QueueId` 维护 **ConsumeQueue** 存该队列消息在 CommitLog 的索引（offset+size+tagHash，20B/条）。消费按 ConsumeQueue 定位再读 CommitLog。

---

### Q7. 为什么所有消息写一个 CommitLog？

**要点速答**：**写性能**。所有消息顺序追加到一个文件，纯顺序写，磁盘顺序写接近内存速度。若按 Topic 分文件，写多个文件随机 IO 性能差。代价是读要靠 ConsumeQueue 索引二次定位，但索引在内存/page cache，随机读也快。

---

### Q8. ConsumeQueue 和 IndexFile 区别？

**要点速答**：
- **ConsumeQueue**：`Topic+QueueId` 维护的索引队列，按消费顺序存消息在 CommitLog 偏移，消费者用它顺序取消息。
- **IndexFile**：按 **MessageKey**（业务 key）的哈希索引，支持"某 key 的消息是否到/历史回溯"，用于查询非按顺序。

---

### Q9. RocketMQ 怎么用零拷贝？

**要点速答**：用 **mmap**（内存映射文件）+ page cache。写 CommitLog 直接写内存（内核页）异步刷盘；读经 ConsumeQueue 定位后从 CommitLog 的 mmap 区读，热数据在 page cache，少一次用户态拷贝。部分版本消息传输用 sendfile。

---

### Q10. 刷盘策略有哪些？

**要点速答**：
- **异步刷盘 ASYNC_FLUSH**（默认）：写 page cache 即返回，后台刷盘，快但宕机可能丢少量。
- **同步刷盘 SYNC_FLUSH**：fsync 落盘才返回，安全慢。
金融级用同步刷盘 + 同步双写。

---

### Q11. CommitLog 文件怎么组织？

**要点速答**：固定 1GB 一个文件，文件名是起始 offset，写满切下一个。追加写不修改。过期靠 **删除最老文件**（按时间/容量）回收空间，非修改。消费未追上时删老文件要谨慎（影响未消费消息）。

---

## 三、消息发送

### Q12. 三种发送方式？

**要点速答**：

| 方式 | 说明 | 场景 |
|---|---|---|
| sync 同步 | 等 Broker ACK | 重要消息 |
| async 异步 | 回调通知 | 高吞吐 |
| oneway 单向 | 只发不等 | 可丢日志 |

---

### Q13. Producer 怎么选 Queue？

**要点速答**：从 NameServer 拿 Topic 的 Queue 列表，默认**轮询**选 Queue → 找 Queue 所在 Broker 发送。失败重试时避开上次失败 Broker（故障隔离）。顺序消息用 `MessageQueueSelector` 按业务 key 取模选同 Queue。

---

### Q14. 发送失败怎么办？

**要点速答**：默认重试 2 次（可配），重试时**故障隔离**（避开失败 Broker，选其他 Broker 的 Queue）。多 Broker 路由分散 + 重试保证发送高可用。仍失败走本地兜底（落库/告警/重投）。

---

## 四、消息消费

### Q15. Push 模式本质是推还是拉？

**要点速答**：本质 **Pull**（消费者主动拉），`DefaultMQPushConsumer` 封装成推外观：内部**长轮询**，有消息立即处理，无消息挂起等待 Broker，兼顾实时性（不空轮询）与吞吐。

---

### Q16. 集群和广播消费区别？

**要点速答**：
- **集群 CLUSTERING**（默认）：同 Group 内消息**分摊**，每条被一个实例消费，常见业务。
- **广播 BROADCASTING**：同 Group 内每个实例消费**全部**消息，配置广播/本地缓存。

---

### Q17. 消费进度存哪里？

**要点速答**：
- 集群模式：存 **Broker**（offset table，持久化 ConsumerOffset 文件），Group 共享。
- 广播模式：存**本地**（各实例独立进度）。
消费完确认 offset 才推进，未确认重启重拉（at-least-once）。

---

### Q18. 什么是 Rebalance？有什么问题？

**要点速答**：Consumer Group 内实例数/Queue 数变化时，重新分配 Queue 到各 Consumer（负载均衡）。问题：Rebalance 期间可能**重复消费**（已消费未确认被重新分配）或暂停，所以消费端要**幂等**。频繁 Rebalance 影响稳定，需控实例稳定。

---

### Q19. Consumer 实例数和 Queue 数关系？

**要点速答**：Consumer 实例数 **≤ Queue 数**才有意义，多了多出的实例空闲（拿不到 Queue）。要提消费并行：加 Topic 的 Queue 数 + 加 Consumer 实例（实例数 ≤ Queue 数）。

---

## 五、顺序消息

### Q20. 全局顺序和分区顺序区别？

**要点速答**：
- **全局顺序**：一个 Queue 串行，吞吐极低，几乎不用。
- **分区顺序（常用）**：按业务 key 路由到同一 Queue，Queue 内有序，多 Queue 并行。

---

### Q21. 顺序消息怎么实现？

**要点速答**：
- **发送**：`MessageQueueSelector` 按业务 key（orderId）取模选 Queue → 同 key 进同 Queue。
- **消费**：`MessageListenerOrderly` 单 Queue 单线程消费（锁住 Queue）保证 Queue 内顺序，而非 `Concurrently`（多线程无序）。

---

### Q22. 顺序消费失败会怎样？

**要点速答**：顺序消费失败会**无限重试**（不能跳过，跳过就乱序），可能阻塞整个 Queue。所以异常要可控（catch + 人工/告警），别让毒消息无限阻塞。集群 Rebalance 时顺序可能短暂受影响。

---

## 六、事务消息

### Q23. 事务消息解决什么问题？

**要点速答**：解决**本地事务 + 消息发送的原子性**，做分布式事务最终一致。避免"本地事务成功但消息没发"或"消息发了但本地事务没成功"的不一致。

---

### Q24. 事务消息流程？

**要点速答**：
1. Producer 发**半消息 HALF** → Broker（半消息对 Consumer 不可见）。
2. 半消息发送成功 → Producer 执行本地事务。
3. 本地事务成功 → commit（消息变可见）；失败 → rollback（删除）。
4. Broker 未收到 commit/rollback → **回查** Producer 的 `checkLocalTransaction` → 返回状态。
详见架构文档 §9。

---

### Q25. 为什么要半消息和回查？

**要点速答**：
- **半消息**：先持久化到 Broker 再执行本地事务，保证"事务成功才消息可见"，避免本地成功消息丢。
- **回查**：Producer 宕机/网络中断没返回 commit/rollback，Broker 定期回查让 Producer 查本地事务状态，保证最终一定 commit 或 rollback，不悬空。

---

### Q26. 事务消息能保证强一致吗？

**要点速答**：不能保证强一致，保证**最终一致**。本地事务与消息发送原子，但下游消费仍可能失败/重复，需消费端幂等 + 重试。它是可靠消息最终一致方案，不是 2PC 强一致。

---

## 七、延迟/重试/死信

### Q27. 延迟消息怎么实现？

**要点速答**：消息发到特殊 Topic `SCHEDULE_TOPIC_XXXX`，按延迟级别存对应 Queue，后台定时任务到点把消息**重新投递**回原 Topic，Consumer 才收到。默认 18 个固定级别（1s~2h），5.x 支持任意时间。

---

### Q28. 延迟消息级别有哪些？

**要点速答**：默认 18 级：1s/5s/10s/30s/1m/2m/3m/4m/5m/6m/7m/8m/9m/10m/20m/30m/1h/2h。非任意时间，需匹配级别。5.x timer message 支持任意时间延迟。

---

### Q29. 消费失败的重试机制？

**要点速答**：消费失败（异常/返回 RECONSUME_LATER）→ 进**重试主题** `%RETRY%Group`，按**递增延迟**重试（1s/5s/...递增到 2h），默认重试 16 次。重试期间原 Topic 不可见。重试缓解瞬时故障。

---

### Q30. 死信是什么？怎么处理？

**要点速答**：重试超次数 → 进**死信主题** `%DLQ%Group`，不再自动投递。毒消息（一直失败）不会无限阻塞队列。处理：人工查死信排查原因，修复后**重投**或丢弃，需平台监控告警。

---

### Q31. 怎么保证消费幂等？

**要点速答**：RocketMQ 是 at-least-once（可能重复消费）。消费端幂等：
1. 业务唯一键去重（消息 `keys`/body 内 id + Redis/DB 唯一约束）。
2. 状态机（已处理状态判断）。
3. 数据库唯一索引兜底。
务必做，否则重复消费导致业务错。

---

## 八、高可用与可靠性

### Q32. 主从复制方式？

**要点速答**：
- **异步复制 ASYNC_MASTER**：主写完即返回，从异步同步，性能高主宕可能丢。
- **同步双写 SYNC_MASTER**：主写从确认才返回，强一致，性能低。
主宕消费者切从读（从不可写），写暂停。

---

### Q33. Dledger 是什么？

**要点速答**：基于 **Raft** 的 Broker 高可用方案。一组 Broker Raft 选主，消息多数派确认才成功，主宕**自动选新主**，写不中断，实现自动故障转移。代价是多副本存储 + Raft 复杂度。5.x 还有 Controller 轻量切换方案。

---

### Q34. 怎么保证消息不丢？

**要点速答**：三层：
- 生产端：同步发送 + 重试 + 确认。
- Broker：同步刷盘 + 同步双写（金融级）。
- 消费端：at-least-once + 幂等 + 消费完确认 offset。
按业务可靠性需求选刷盘/复制级别，吞吐与可靠权衡。

---

### Q35. 消息堆积怎么处理？

**要点速答**：
1. 加 Consumer 实例（≤Queue 数）+ 加 Queue 数提并行。
2. 消费端批量/异步处理，避免阻塞。
3. 临时扩容 + 降级非核心逻辑。
4. 堆积过多考虑跳过/转储（堆积消息转 DB 后慢慢消化）。
5. 根因：消费慢，优化消费逻辑或下游性能。

---

### Q36. 消息过滤怎么实现？

**要点速答**：
- **Tag 过滤**：订阅指定 tag，Broker 按 tag hash 过滤 + Consumer 精筛，简单高效。
- **SQL92 过滤**：按 property 用 SQL 表达式，灵活开销略大。
- 消费端也可自行业务过滤。

---

## 九、对比与进阶

### Q37. RocketMQ vs Kafka？

**要点速答**：

| | RocketMQ | Kafka |
|---|---|---|
| 模型 | Topic+Queue | Topic+Partition+CG |
| 存储 | CommitLog 顺序写+CQ | partition 文件+segment |
| 事务 | 原生支持 | 无 |
| 顺序 | 分区顺序 | partition 内 |
| 延迟 | 内置 | 无原生 |
| 吞吐 | 高 | 极高 |
| 适用 | 业务消息/金融 | 大数据/日志流 |

业务/事务选 RocketMQ，大数据流式选 Kafka。

---

### Q38. RocketMQ vs RabbitMQ？

**要点速答**：RabbitMQ 是 AMQP 协议，Exchange+Queue 复杂路由模型，吞吐中等，适合复杂路由/企业集成。RocketMQ 吞吐高、事务/顺序/延迟原生、金融级可靠，适合高并发业务消息。轻量路由选 RabbitMQ，海量业务消息选 RocketMQ。

---

### Q39. RocketMQ 为什么适合金融场景？

**要点速答**：① 同步刷盘+同步双写/Dledger 保证不丢；② 事务消息保证最终一致；③ 顺序消息保证业务有序；④ 死信+重试保证可靠消费；⑤ 高可用主从/Dledger 自动故障转移。综合可靠性与业务特性（事务/顺序/延迟）契合金融。

---

### Q40. 长轮询是怎么实现"推"的？

**要点速答**：Consumer 拉消息时，若 Broker 暂无新消息，**不立即返回**，而是挂起请求（长轮询，默认挂起 15s），期间有新消息立即唤醒返回；超时无消息返回空，Consumer 立即再拉。兼顾"有消息即时处理"（实时性）与"无消息不空耗"（不频繁空轮询）。

---

## 附：一句话速答卡

| 问 | 答 |
|---|---|
| 角色 | NameServer/Broker/Producer/Consumer |
| 路由 | NameServer 无中心互独立 |
| 存储 | CommitLog 顺序写 + ConsumeQueue 索引 |
| 零拷贝 | mmap + page cache |
| 刷盘 | async(快)/sync(安全) |
| 复制 | async/sync 主从 + Dledger Raft |
| 发送 | sync/async/oneway |
| 消费 | 长轮询拉, 集群/广播 |
| 顺序 | 同key同Queue + Orderly单线程 |
| 事务 | 半消息+本地事务+回查 |
| 延迟 | 18级, 延迟Topic暂存重投 |
| 重试/死信 | %RETRY%递增, %DLQ% |
| 可靠 | 同步刷盘+同步双写, 消费幂等 |
| 不丢 | 生产确认+刷盘双写+消费at-least-once |

---

> 配套架构文档见 [RocketMQ整体架构和深入理解.md](RocketMQ整体架构和深入理解.md)。
