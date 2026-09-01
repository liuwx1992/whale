# RocketMQ 整体架构和深入理解

> **文档版本**: v1.0
> **创建日期**: 2026-08-31
> **编写人**: weixin.liu
> **用途**: 从整体架构到存储/消息收发/高可用/事务消息/顺序/重试/延迟全面拆解 RocketMQ，供后续大模型阅读与排障

---

## 0. 一句话定位

RocketMQ 是一个**分布式、高吞吐、高可靠、模型简洁**的消息中间件（阿里开源，捐 Apache）。它用"NameServer 无中心路由 + Broker 主从 + CommitLog 顺序写 + ConsumeQueue 索引"实现海量消息收发，**原生支持事务消息、顺序消息、延迟消息、死信**，是金融级业务消息首选。

---

## 1. 适用场景

| 维度 | 说明 |
|---|---|
| 适用 | 异步解耦、削峰填谷、分布式事务、顺序消息、延迟消息、广播/集群消费、日志同步 |
| 不适用 | 极致低延迟微秒级（用专用低延迟 MQ）、纯流式计算管道（用 Kafka/Pulsar） |
| 特性 | 高可靠（金融级）、事务消息、顺序、延迟、死信、丰富的消费模式 |

典型场景：订单/支付异步、分布式事务最终一致、秒杀削峰、延迟取消订单、广播配置/通知、binlog 同步。

---

## 2. 整体架构总览

```
       Producer(生产者)            Consumer(消费者)
            │   send                    │ consume
            ▼                            ▼
   ┌──────────────────┐         ┌──────────────────┐
   │   NameServer 集群│◀─路由──│      Broker       │
   │ (无中心, 互独立) │         │  (主从, 承载消息) │
   │  topic/queue 路由│         │  CommitLog+CQ+索引│
   └──────────────────┘         └──────────────────┘
            ▲  心跳/路由上报            │ 主从同步/异步复制
            │                          ▼
   Producer/Consumer/Broker 都连 NameServer        从 Broker(只读副本)
```

四大角色：

| 角色 | 职责 |
|---|---|
| **NameServer** | 路由注册中心，**无状态、互独立**（非 ZK 集群那种强一致）。Broker 上报路由，Producer/Consumer 拉取路由 |
| **Broker** | 消息存储与转发主体，主从架构，承载 Topic/Queue/消息文件 |
| **Producer** | 生产者，从 NameServer 路由找 Broker 发消息，支持同步/异步/单向/oneway |
| **Consumer** | 消费者，从 NameServer 路由找 Broker 拉消息，集群/广播模式 |

### 2.1 为什么用 NameServer 而非 ZooKeeper

- NameServer **无中心、互独立、最终一致**：每个 NS 节点持有全量路由，Broker 向每个 NS 上报。任一 NS 挂不影响（客户端连任一可用 NS）。
- 比 ZK 轻：不维护强一致状态、无 leader 选举/数据同步开销、部署简单。
- 客户端定期（30s）拉路由，Broker 异常靠**心跳超时**（默认 120s）剔除，路由最终一致。

---

## 3. 消息模型：Topic / Queue / Group

### 3.1 三层模型

```
Topic(逻辑主题, 业务区分)
  └─ MessageQueue(队列, 默认4个/Topic, 分布式/并发/顺序的单位)
       └─ Message(具体消息, 含 tag/keys/property/body)
```

- **Topic**：逻辑主题，一类消息。
- **MessageQueue**：Topic 的物理队列，分布在不同 Broker。**负载均衡与顺序的单位**：一个 Queue 内有序，多 Queue 并行。
- **Producer/Consumer Group**：一组同类生产/消费实例，共享身份。集群消费下同组内消息分摊（负载均衡），不同组各消费一份（广播效果）。

### 3.2 队列与并行

- 4 个 Queue 分布在多 Broker → 多 Broker 并行 → 吞吐。
- 一个 Consumer Group 内，Queue 被分配给各 Consumer 实例（Rebalance）→ 消费并行。
- 顺序消息：一个 Queue 内有序，单 Queue 单线程消费保证顺序。

---

## 4. 存储机制（RocketMQ 高性能核心）

### 4.1 CommitLog（所有消息顺序写一个文件）

**关键设计**：所有 Topic 的所有消息**全部顺序追加写到一个 CommitLog 文件**（不按 Topic 分文件）。

- **写极快**：纯顺序写，磁盘顺序写接近内存。
- CommitLog 文件固定 1GB，写满切下一个（`00000000000000000000`...）。
- 缺点：按 Topic/QueueId 读时要查（靠 ConsumeQueue 索引）。

### 4.2 ConsumeQueue（逻辑队列索引）

每个 `Topic + QueueId` 维护一个 ConsumeQueue，存该队列消息在 CommitLog 的**索引**：

```
ConsumeQueue 条目(20字节):
  offset(8B CommitLog偏移) + size(4B 消息长度) + tagHashCode(8B)
```

- 消费者按 ConsumeQueue 找消息在 CommitLog 的物理位置，再去 CommitLog 读消息体。
- ConsumeQueue 可放内存（MappedFile + page cache），随机读快。
- 这就是"写用 CommitLog 顺序写保证性能，读用 ConsumeQueue 索引保证按 Topic/Queue 快速定位"。

### 4.3 IndexFile（消息索引，按 key 查）

哈希索引，支持按 `MessageKey`（业务 key）查消息，用于"消息是否到/历史回溯"。`build index by key`。

### 4.4 MappedFile 与零拷贝

- RocketMQ 用 **mmap**（内存映射文件）+ **page cache** 读写文件，少一次用户态拷贝。
- 写：mmap 直接写内存（内核页），异步刷盘。
- 读：mmap + page cache，热数据在内存。
- 4.5+ 引入 **Dledger / 多副本**，部分版本支持 `transferTo`（sendfile）零拷贝。

### 4.5 刷盘策略

| 策略 | 说明 |
|---|---|
| **异步刷盘 ASYNC_FLUSH**（默认） | 写 page cache 即返回，后台刷盘，性能高，宕机可能丢少量 |
| **同步刷盘 SYNC_FLUSH** | 写后 fsync 落盘才返回，安全，性能低 |
| **同步双写 SYNC_MASTER** | 主从同步复制（不是刷盘），主写从再返回，保证副本一致 |

金融级常：同步刷盘 + 同步双写（`SYNC_FLUSH + SYNC_MASTER`）。

---

## 5. 高可用：主从与 Dledger

### 5.1 主从复制

- **Master**：读写，**Slave**：只读副本。
- 复制方式：
  - **异步复制 ASYNC_MASTER**：主写完立即返回，从异步同步。性能高，主宕可能丢未同步。
  - **同步双写 SYNC_MASTER**：主写从确认后才返回，强一致，性能低。
- 主宕：消费者切到从读（从不能写），写暂停直到主恢复或切换。

### 5.2 Dledger（基于 Raft 的高可用）

旧版主从无自动故障转移（主宕写不可用）。**Dledger** 引入：
- 一组 Broker 用 Raft 选主。
- 消息走 Dledger commit log，多数派确认才成功。
- 主宕自动选新主，**自动故障转移**，写不中断。
- 代价：多副本存储开销 + Raft 复杂度。

### 5.3 Controller 模式（5.x）

5.x 引入 Controller（可选），轻量级主从自动切换，介于传统主从和 Dledger 之间，平衡可用性与复杂度。

---

## 6. 消息发送

### 6.1 三种发送方式

| 方式 | 说明 | 场景 |
|---|---|---|
| **同步 sync** | 等 Broker 确认才返回 | 重要消息，要确知结果 |
| **异步 async** | 发送后回调通知 | 高吞吐非强同步 |
| **单向 oneway** | 只发不等，无确认 | 日志/监控等可丢 |

### 6.2 发送路由选择

- Producer 从 NameServer 拿 Topic 的 Queue 列表。
- 轮询（默认）选 Queue → 找到 Queue 所在 Broker 发送。
- **发送失败重试**：默认重试 2 次，重试时避开上次失败的 Broker（`faultAvoid`）。
- **发送延迟**：同步发送等 Broker 确认有延迟。

### 6.3 发送高可用

- 队列路由分散多 Broker，单 Broker 故障不影响其他。
- 重试 + 故障隔离 Broker。
- 客户端定期刷新路由。

---

## 7. 消息消费

### 7.1 拉模式 vs 推模式

RocketMQ 本质是**Pull 模式**（消费者主动拉），但封装成**Push**外观（`DefaultMQPushConsumer`）：内部长轮询，有消息立即处理，无消息挂起等待，兼顾实时性与吞吐。

### 7.2 集群 vs 广播

| 模式 | 说明 |
|---|---|
| **集群 CLUSTERING**（默认） | 同 Consumer Group 内消息**分摊**（每条被一个实例消费），常见业务消费 |
| **广播 BROADCASTING** | 同 Group 内每个实例都消费**全部**消息，配置广播/本地缓存同步 |

### 7.3 消费进度

- 集群模式：进度存 **Broker**（offset table），持久化到 ConsumerOffset 文件。
- 广播模式：进度存**本地**（各实例独立）。
- 消费完确认 offset 才推进，未确认重启后重拉（at-least-once）。

### 7.4 Rebalance 负载均衡

Consumer Group 内实例数变化、Queue 数变化时，**Rebalance** 重新分配 Queue 到各 Consumer。策略：平均分配、一致性哈希等。Rebalance 期间可能短暂重复消费/暂停，需幂等。

---

## 8. 顺序消息

### 8.1 全局顺序 vs 分区顺序

- **全局顺序**：一个 Queue，串行，吞吐极低，几乎不用。
- **分区顺序（Partition Order）**：按业务 key 路由到同一 Queue，Queue 内有序，多 Queue 并行。**生产实践用这种**。

### 8.2 实现原理

发送端：
- 用 `MessageQueueSelector`，按业务 key（如 orderId）取模选 Queue → 同 key 消息进同一 Queue。
消费端：
- 用 `MessageListenerOrderly`，**单 Queue 单线程消费**（锁住 Queue），保证 Queue 内顺序。
- 而非 `MessageListenerConcurrently`（多线程，无序）。

### 8.3 注意

- 顺序消费失败会**无限重试**（不跳过，否则乱序），阻塞队列，需控制异常。
- 集群 Rebalance 时顺序可能短暂受影响。

---

## 9. 事务消息（RocketMQ 招牌特性）

实现**本地事务 + 消息发送的原子性**，做分布式事务最终一致。

### 9.1 两阶段 + 回查

```
1. Producer 发"半消息"(HALF) -> Broker(半消息, 对 Consumer 不可见)
2. 半消息发送成功 -> Producer 执行本地事务
3. 本地事务成功 -> commit(消息变可见, Consumer 可消费)
   本地事务失败 -> rollback(消息删除)
4. 若 Broker 未收到 commit/rollback(网络/Producer 宕) ->
   Broker 定期"回查" Producer 的 checkLocalTransaction ->
   Producer 查本地事务状态 返回 commit/rollback
```

### 9.2 为什么这样设计

- 半消息先落 Broker（持久化），再执行本地事务，避免"本地事务成功但消息没发出去"。
- 回查机制解决 Producer 宕机/网络中断后事务状态未知，保证最终一致（commit 或 rollback）。
- 适用：订单创建成功才发下游消息、库存扣减与通知一致。

---

## 10. 延迟消息

RocketMQ 内置**固定级别延迟**（非任意时间）：

```
1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h (18 级)
```

原理：消息发到特殊 Topic `SCHEDULE_TOPIC_XXXX`，按级别存对应 Queue，后台定时任务到点把消息从延迟 Topic **重新投递**回原 Topic，Consumer 才收到。

- 5.x 支持**任意时间延迟**（timer message）。
- 适用：30 分钟未支付取消订单、定时通知。

---

## 11. 重试与死信

### 11.1 重试

- 消费失败（异常/返回 RECONSUME_LATER）→ 消息进**重试主题** `%RETRY%Group`，按**递增延迟**重试（1s/5s/...递增到 2h）。
- 重试次数默认 16，可配。重试期间该消息对原 Topic 不可见。

### 11.2 死信 DLQ

- 重试超次数 → 进入**死信主题** `%DLQ%Group`，不再自动投递。
- 死信需人工处理（排查/重投/丢弃）。
- 防止"毒消息"无限重试阻塞队列。

---

## 12. 消息可靠性

### 12.1 三级保证

| 级别 | 配置 | 丢失风险 |
|---|---|---|
| 最高 | 同步刷盘 + 同步双写 | 几乎不丢，性能低 |
| 中 | 异步刷盘 + 同步双写 | 主宕可能丢少量 |
| 高吞吐 | 异步刷盘 + 异步复制 | 宕机可能丢，性能高 |

### 12.2 生产者端可靠性

- 同步发送 + 重试 + 确认。
- 失败走本地兜底（落库/重投）。
- `send` 同步等 ACK。

### 12.3 消费者端可靠性

- at-least-once：消费完确认 offset 才推进；未确认重投。
- **幂等**：可能重复消费，消费端必须幂等（业务唯一键去重/状态机）。

---

## 13. 消息过滤

- **Tag 过滤**：订阅指定 tag，Broker 端按 tag hash 过滤，Consumer 端再精筛。简单高效。
- **SQL92 过滤**：按 property 用 SQL 表达式过滤，灵活但开销略大。
- **类过滤**：服务端跑过滤类（慎用）。

---

## 14. 性能与运维要点

| 关注点 | 做法 |
|---|---|
| 顺序写 | CommitLog 顺序写，已天然 |
| 刷盘 | 高吞吐异步，金融同步刷盘+同步双写 |
| 队列数 | 按 Topic 调 Queue 数提升并行 |
| 消费并行 | Consumer 实例数 ≤ Queue 数（多了空闲） |
| 消费幂等 | 必做，防重复消费 |
| 消费速度 | 避免阻塞，慢消费加实例/Queue |
| 大消息 | 控制消息大小（默认 4MB），大消息走 OSS |
| 集群 | 多 Broker + 主从/Dledger 高可用 |
| 监控 | 堆积/延迟/重试/死信/TPS |
| 顺序消费 | 别异常无限重试，控制重试 |
| 事务消息 | 回查实现 + 本地事务幂等 |

---

## 15. RocketMQ vs Kafka vs RabbitMQ

| 维度 | RocketMQ | Kafka | RabbitMQ |
|---|---|---|---|
| 模型 | Topic+Queue | Topic+Partition+ConsumerGroup | Exchange+Queue 路由 |
| 吞吐 | 高 | 极高 | 中 |
| 可靠 | 金融级 | 高（靠副本） | 高 |
| 事务消息 | **原生支持** | 无 | 弱 |
| 顺序 | 分区顺序 | partition 内 | 队列内 |
| 延迟 | 内置级别 | 无原生 | 插件 |
| 消费 | 推(拉封装)/集群广播 | 拉为主 | 推 |
| 适用 | 业务消息/事务/金融 | 大数据/日志流 | 复杂路由/AMQP |

业务消息/事务/金融选 RocketMQ；大数据流式/Kafka 生态选 Kafka；复杂路由选 RabbitMQ。

---

## 16. 一张图串起核心概念

```
路由: NameServer(无中心互独立, Broker上报, 客户端30s拉)
存储: CommitLog(所有消息顺序写1G文件) + ConsumeQueue(每Topic+QueueId索引, 20B/条) + IndexFile(按key查)
   mmap + pagecache 零拷贝; 刷盘 async/sync; 主从 async/sync复制或Dledger Raft
发送: sync/async/oneway + 路由选Queue + 失败重试隔离Broker
消费: Push=长轮询拉; 集群(分摊)/广播(全量); offset持久化; Rebalance分配Queue
顺序: 同key选同Queue + Orderly单线程消费Queue内
事务: 半消息HALF(不可见) -> 本地事务 -> commit/rollback -> 回查兜底
延迟: 18级固定级别(5.x任意时间), 延迟Topic暂存到点重投
重试: 失败进%RETRY%递增延迟, 超限进%DLQ%死信
可靠: 同步刷盘+同步双写最高; 消费at-least-once需幂等
```

记住这条链，就抓住 RocketMQ 全部骨架：**NameServer 路由、CommitLog 顺序写+CQ 索引、主从/Dledger、事务消息、顺序/延迟/重试死信**。
