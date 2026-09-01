# Netty 面试相关问题理解和答案

> **文档版本**: v1.0
> **创建日期**: 2026-08-31
> **编写人**: weixin.liu
> **Netty 版本**: 4.1.101.Final
> **用途**: 系统梳理 Netty 高频面试题与标准答案，结合 `com.zoo.whale.io.netty` 下可运行示例。每题给"要点速答 + 深入展开"，便于面试快速调用与深度追问

---

## 目录

- [一、基础与定位](#一基础与定位)
- [二、Reactor 线程模型](#二reactor-线程模型)
- [三、EventLoop / EventLoopGroup](#三eventloop--eventloopgroup)
- [四、Channel / ChannelPipeline / ChannelHandler](#四channel--channelpipeline--channelhandler)
- [五、ByteBuf 与内存](#五bytebuf-与内存)
- [六、编解码与粘包/半包](#六编解码与粘包半包)
- [七、零拷贝](#七零拷贝)
- [八、异步模型与 Future](#八异步模型与-future)
- [九、内存管理与泄漏检测](#九内存管理与泄漏检测)
- [十、性能调优](#十性能调优)
- [十一、高频场景题](#十一高频场景题)
- [十二、对比题](#十二对比题)
- [十三、源码级追问](#十三源码级追问)

---

## 一、基础与定位

### Q1. Netty 是什么？为什么要用它而不是 Java NIO？

**要点速答**：Netty 是一个基于 NIO 的、事件驱动的、异步的高性能网络框架。用 Netty 而非裸 NIO 的原因：

1. **API 易用**：NIO 的 `Selector / Channel / ByteBuffer` 使用繁琐，需手动维护 selectionKey、处理空轮询、半包粘包；Netty 全封装。
2. **线程模型**：自带主从 Reactor，NIO 要自己写线程分配。
3. **粘包半包**：自带 `LengthFieldBasedFrameDecoder` 等开箱即用，NIO 全靠手撕。
4. **ByteBuf**：双指针、可池化、堆外、零拷贝组合，远胜 `ByteBuffer` 的单指针 + `flip()`。
5. **稳定性**：屏蔽了 JDK NIO 的 epoll 空轮询 bug（Netty 通过 select 计数 + 重建 Selector 规避）。
6. **生态**：编解码器（HTTP/Protobuf/WebSocket/SSL）、连接池、空闲检测、流量整形等成熟。

**深入展开**：NIO 不是不能用，而是"会用的成本极高"。Netty 本质是 NIO 的工程化封装——底层仍是 `Selector.select()` + `Channel`，但把所有坑都填了。

---

### Q2. Netty 的三大核心组件是什么？

**要点速答**：

1. **Channel + EventLoop**：网络抽象 + 事件循环（IO 与任务执行）。
2. **ChannelPipeline + ChannelHandler**：责任链 + 业务处理器。
3. **ByteBuf + ChannelFuture**：数据载体 + 异步回调。

一句话：**EventLoop 跑线程，Channel 跑网络，Pipeline 跑业务，ByteBuf 跑数据，Future 跑异步**。

---

### Q3. Netty 的核心架构分层？

**要点速答**：

```
引导层        Bootstrap / ServerBootstrap（组装与启动）
↓
事件层        EventLoopGroup -> EventLoop（线程模型、IO 轮询、任务执行）
↓
通道层        Channel（连接与网络操作抽象）
↓
责任链层      ChannelPipeline -> ChannelHandlerContext -> ChannelHandler（入站/出站处理）
↓
数据层        ByteBuf（字节容器，堆内/堆外/池化，零拷贝）
↓
异步层        ChannelFuture / Promise（异步结果与回调）
```

---

## 二、Reactor 线程模型

### Q4. Netty 用的是哪种线程模型？三种 Reactor 模型分别是什么？

**要点速答**：Netty 默认采用**主从 Reactor 多线程模型**（主 Reactor 负责接受连接，从 Reactor 负责已连接通道的 IO）。三种：

| 模型 | 结构 | 特点 |
|---|---|---|
| 单线程 Reactor | 1 线程扛 accept + IO + 业务 | 连接少、业务快 |
| 多线程 Reactor | 1 个 Reactor 接连接，N 个 worker 处理 IO/业务 | Reactor 仍是单点 |
| 主从 Reactor | mainReactor accept，subReactor 处理 IO，业务可选丢业务线程池 | Netty 默认，高并发首选 |

**深入展开**：本项目 `io/netty/NettyServer.java` 即主从模型：

```java
EventLoopGroup bossGroup   = new NioEventLoopGroup();   // mainReactor: accept
EventLoopGroup workerGroup = new NioEventLoopGroup();   // subReactor: read/write
bootstrap.group(bossGroup, workerGroup);
```

boss 拿到新连接后，把 SocketChannel 注册到 worker 的某个 NioEventLoop 上，后续该连接的读写都在那个 worker 线程完成。

---

### Q5. bossGroup 和 workerGroup 的区别？boss 线程数怎么定？

**要点速答**：
- **bossGroup**：只处理连接请求（OP_ACCEPT），不处理业务。
- **workerGroup**：处理已建立连接的读写（OP_READ/OP_WRITE）和业务。
- boss 线程数：服务端通常 **1 个**即可（`new NioEventLoopGroup(1)`），accept 速率远低于 worker；多了浪费。
- worker 线程数：默认 `CPU 核数 * 2`（`MultithreadEventExecutorGroup.DEFAULT_EVENT_LOOP_THREADS`）。

**追问：boss 能用多个吗？** 能，但意义不大，除非要监听多个端口。多端口场景可让 boss 线程数 = 端口数。

**追问：能不能只用一个 group？** 可以，`bootstrap.group(g)` 单参重载会让该组既接连接又处理 IO；生产环境不推荐。

---

### Q6. `handler(...)` 和 `childHandler(...)` 的区别？

**要点速答**：
- `handler` 装配到 **boss 的 pipeline**，处理 ServerSocketChannel 的事件。
- `childHandler` 装配到 **worker 的 pipeline**，处理每个客户端 SocketChannel。

**深入展开**：本项目 `NettyServer` 只用了 `childHandler`（业务在客户端连接上跑），boss 没有自定义 handler，用默认。如果你想在 accept 阶段做权限校验、连接限流，可以用 `handler` 加到 boss 链上。

---

### Q7. Netty 是如何保证一个连接的事件始终由同一个线程处理的？

**要点速答**：boss accept 后，把新 SocketChannel **注册到 workerGroup 中固定的某一个 NioEventLoop** 上（轮询/取模分配）。该 EventLoop 是单线程，此后该连接的所有 IO 事件都在这同一个线程的 `run()` 循环里处理——**串行化无锁**，无需同步。

**深入展开**：这是 Netty 并发设计的精髓：用"绑定线程"代替"加锁"。一个 worker 线程上挂着成百上千个连接，单线程处理所有事件，没有上下文切换开销。代价是：**绝对不能在 EventLoop 线程做阻塞操作**，否则整条线程上所有连接全卡。

---

## 三、EventLoop / EventLoopGroup

### Q8. NioEventLoop 的本质和 `run()` 循环做了什么？

**要点速答**：`NioEventLoop` = **单线程执行器 + Selector + 任务队列**。`run()` 循环三步：

1. **select**：轮询 IO 事件（OP_ACCEPT / OP_READ / OP_WRITE）。
2. **processSelectedKeys**：处理就绪 IO 事件，触发 pipeline 上的 handler。
3. **runAllTasks**：执行 taskQueue / scheduledTaskQueue 里的非 IO 任务。

**深入展开**：select 和任务执行之间有 ioRatio 控制时间比例（默认 50%）。Netty 还会处理 JDK 的 epoll 空轮询 bug：当 select 空返回次数超过阈值（默认 512），会重建 Selector，把旧的 key 迁过去。

---

### Q9. EventLoop 里有两类任务队列，分别是什么？handler 里耗时操作怎么办？

**要点速答**：两类队列：
- **普通任务队列**（taskQueue，MPSC 多生产单消费队列）：`execute` 提交。
- **定时任务队列**（scheduledTaskQueue）：`schedule` 提交。

handler 里做耗时操作（查库、调下游）**必须不能阻塞 EventLoop 线程**，两种解法：
1. **自定义业务线程池**：在 handler 内 `businessExecutor.submit(...)`，处理完用 `ctx.writeAndFlush()` 回写（Netty 自动判断 `inEventLoop`，非 EventLoop 线程会通过 task 派回 EventLoop 执行）。
2. **addLast 时指定 EventExecutorGroup**：`pipeline.addLast(businessGroup, handler)`，让该 handler 跑在独立线程组。

**追问：为什么 `ctx.writeAndFlush()` 能跨线程安全？** 因为 `AbstractChannelHandlerContext.write` 会检查 `executor().inEventLoop()`，不在 EventLoop 线程就 `execute(()->write)` 派回，避免多线程直接操作 socket。

---

### Q10. 为什么 Netty 要把 IO 和非 IO 任务放一个线程？不能分开吗？

**要点速答**：放一起是为了**串行化无锁**——同一个连接的事件同线程处理，省掉锁与上下文切换。代价是阻塞会拖垮同线程所有连接，所以要求业务"快进快出"，慢的丢业务线程池。

**深入展开**：ioRatio 调节 IO 与任务执行的时间比例（默认 50:50）。如果 IO 事件多、业务轻，可调高 ioRatio 让更多时间给 IO；反之调低。

---

## 四、Channel / ChannelPipeline / ChannelHandler

### Q11. Channel 是什么？有哪些常见实现？

**要点速答**：`Channel` 是网络 socket 的抽象，提供 `bind / connect / read / write / flush / close`，全部异步返回 `ChannelFuture`。常见：

| Channel | 用途 |
|---|---|
| `NioServerSocketChannel` | 服务端 accept 通道 |
| `NioSocketChannel` | NIO 连接通道 |
| `EpollServerSocketChannel` | Linux epoll（需 native 库，本项目 pom 引了 epoll 包） |
| `LocalChannel` | 同 JVM 内通信 |
| `KQueueServerSocketChannel` | macOS/BSD |

---

### Q12. ChannelPipeline 是什么？数据怎么流的？

**要点速答**：每个 Channel 持有一条 `ChannelPipeline`，是 `ChannelHandlerContext` 的**双向链表**，每个 context 持一个 handler。

- **入站（Inbound）**：socket → head → tail，由 `ChannelInboundHandler` 处理（解码、业务）。读方向。
- **出站（Outbound）**：tail → head → socket，由 `ChannelOutboundHandler` 处理（编码、flush）。写方向。

```
入站: head -> [FrameDecoder] -> [Decoder] -> [Business] -> tail
出站: tail  <- [Business]  <- [Encoder]  <- [FrameEncoder] <- head
```

**追问：`ctx.writeAndFlush()` 和 `ctx.channel().writeAndFlush()` 区别？**
- `ctx.writeAndFlush()`：从**当前 handler 往前**找下一个出站 handler（出站链从当前点向 head）。
- `ctx.channel().writeAndFlush()`：从 **tail 开始**往前走完整出站链，会经过所有出站 handler（性能略低，但保证所有编码器都生效）。

本项目 `NettyServerHandler.channelReadComplete` 用 `ctx.writeAndFlush`，因为业务 handler 后面就剩 head/flush，没必要从 tail 走。

---

### Q13. 常用的入站回调有哪些？触发时机？

**要点速答**：

| 方法 | 触发时机 |
|---|---|
| `handlerAdded` | handler 加入 pipeline |
| `channelRegistered` | channel 注册到 EventLoop |
| `channelActive` | 连接建立/激活 |
| `channelRead(ctx, msg)` | 读到数据 |
| `channelReadComplete` | 一次读周期完成 |
| `channelInactive` | 连接断开 |
| `channelUnregistered` | channel 从 EventLoop 注销 |
| `exceptionCaught` | 链上任一 handler 抛异常 |

本项目 `NettyServerHandler` 实现了 `channelRead` / `channelReadComplete` / `exceptionCaught`。

---

### Q14. `ChannelInboundHandlerAdapter` 和 `SimpleChannelInboundHandler` 区别？什么时候用哪个？

**要点速答**：
- `ChannelInboundHandlerAdapter`：`channelRead` 收到的是 `Object`，**不自动 release**，需自己处理内存释放。适合需要把消息继续传递（`fireChannelRead`）或精细控制释放时机的场景。
- `SimpleChannelInboundHandler<T>`：泛型指定类型，自动在 `channelRead` 后 `release` 消息，**只读用一次**的消息。适合解码后直接消费的终端业务 handler。

**深入展开**：`SimpleChannelInboundHandler` 内部在 `channelRead` 里调 `channelRead0` 后 `ReferenceCountUtil.release(msg)`。如果你要把 ByteBuf 往下传或累积，用 Adapter；如果消息到此为止消费掉，用 Simple。

---

### Q15. `exceptionCaught` 不实现会怎样？

**要点速答**：异常会沿 pipeline 一路 `fireExceptionCaught` 传到 tail，被 Netty 默认 handler `TailContext` 打日志并**关闭连接**。所以一定要实现 `exceptionCaught`，至少做日志 + 优雅关闭，避免连接僵死或日志刷屏。

---

### Q16. handler 的 `@Sharable` 是什么？

**要点速答**：`@ChannelHandler.Sharable` 标注一个 handler 实例可被多个 pipeline 共享（无状态）。无状态的 handler（如 logger、编码器）可以用一个实例 addLast 到多个 channel；有状态的（持有连接计数等）不可共享。

---

## 五、ByteBuf 与内存

### Q17. ByteBuf 和 NIO ByteBuffer 的区别？

**要点速答**：

| 维度 | NIO ByteBuffer | Netty ByteBuf |
|---|---|---|
| 指针 | 单 `position`，读写要 `flip()` | 双指针 `readerIndex`/`writerIndex`，读写独立 |
| 容量 | 固定 | 可 `ensureWritable` 自动扩容 |
| 池化 | 无 | `PooledByteBufAllocator` 池化，减 GC |
| 堆外 | 手动 `allocateDirect` | `directBuffer` 堆外，配合零拷贝 |
| 引用计数 | 无 | `ReferenceCounted`，用完要 `release()` |
| 组合 | 无 | `slice/duplicate/composite` 逻辑组合不复制 |

---

### Q18. ByteBuf 的三个指针？读写逻辑？

**要点速答**：

```
 +-------------------+------------------+------------------+
 | discardable bytes |  readable bytes  |  writable bytes  |
 |   已读(可丢弃)    |    (内容区)       |    (可写区)      |
 +-------------------+------------------+------------------+
 0      <=      readerIndex   <=   writerIndex    <=    capacity
```

- `readerIndex`：读指针，`readByte()` 后递增。
- `writerIndex`：写指针，`writeByte()` 后递增。
- `capacity`：总容量，可扩容。
- `discardable`：已读部分，可 `discardReadBytes` 压缩腾空间（涉及内存搬移，慎用）。

---

### Q19. 堆内 ByteBuf 和堆外 ByteBuf 的区别？怎么选？

**要点速答**：

| 维度 | 堆内（heap） | 堆外（direct） |
|---|---|---|
| 分配 | 快 | 慢 |
| GC | 能回收 | 不受 GC 直接管，靠 Cleaner/引用计数释放 |
| socket 读写 | 需额外拷一次到直接内存 | 零拷贝，少一次拷贝 |
| 适用 | 小量、短生命周期、调试 | 高并发、大流量、IO 密集 |

**选型**：IO 密集、高吞吐默认堆外池化（Netty 默认 `PooledByteBufAllocator.DEFAULT`）；需要频繁在用户态操作且量小，可堆内。堆外一定要管好释放，否则内存泄漏。

---

### Q20. 什么是池化 ByteBuf？Netty 默认用哪种 Allocator？

**要点速答**：池化 = 预分配大块内存切成 chunk/page，按需分配 ByteBuf，用完归还而非 GC，减少 GC 停顿与分配开销。Netty 默认 `PooledByteBufAllocator.DEFAULT`（堆外池化）。可改 `UnpooledByteBufAllocator` 或设系统参数 `-Dio.netty.allocator.type=unpooled`。

---

## 六、编解码与粘包/半包

### Q21. 什么是 TCP 粘包/半包？为什么会出现？

**要点速答**：TCP 是**流式协议，无边界**。
- **粘包**：发送方多条消息粘成一条，接收方一次读到多条。
- **半包**：一条消息被拆成多次收到。

原因：
1. 应用层 write 速率 > MSS，TCP 分段。
2. 接收方 socket 缓冲区一次 read 可能拿到 0~N 条消息。
3. 链路层 MSS 限制、滑动窗口、Nagle 算法。

**本质**：TCP 不保留消息边界，边界要应用层自己定。

---

### Q22. 解决粘包/半包的常见方案？

**要点速答**：

| 方案 | Netty 实现 |
|---|---|
| 固定长度 | `FixedLengthFrameDecoder` |
| 换行符 | `LineBasedFrameDecoder` |
| 自定义分隔符 | `DelimiterBasedFrameDecoder` |
| **长度域** | `LengthFieldBasedFrameDecoder`（最常用，自定义协议首选） |

**深入展开**：本项目 `io/netty/LengthServer.java` + `LengthClient.java` 即长度域方案，可运行：

```java
p.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));  // 入站拆包
p.addLast(new LengthFieldPrepender(4));                                        // 出站加长度域
p.addLast(new StringDecoder(CharsetUtil.UTF_8));
p.addLast(new StringEncoder(CharsetUtil.UTF_8));
p.addLast(new LengthServerHandler());
```

`LengthClientHandler` 连发 3 条不等长消息制造粘包/半包，服务端仍逐条正确接收。

---

### Q23. `LengthFieldBasedFrameDecoder` 的参数含义？

**要点速答**：构造 `LengthFieldBasedFrameDecoder(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip)`：

- `maxFrameLength`：单帧最大长度，防恶意大包 OOM。
- `lengthFieldOffset`：长度域起始偏移（长度域前可能有协议头）。
- `lengthFieldLength`：长度域字节数（1/2/3/4/8，大端）。
- `lengthAdjustment`：长度值与实际消息体的差值补偿（如长度域记的是"总长含头"时需补偿）。
- `initialBytesToStrip`：拆包后剥掉前几个字节（通常剥掉长度域自身 = `lengthFieldLength`）。

**例子**：协议 `[4字节长度][消息体]`，长度值=消息体字节数 → `(MAX, 0, 4, 0, 4)`。若长度值=总长含长度域 → `(MAX, 0, 4, -4, 4)`。

---

### Q24. 编码器和解码器分别在入站还是出站链？

**要点速答**：
- **解码器（Decoder）是入站**：`ByteBuf → 业务对象`，走 head→tail。如 `LengthFieldBasedFrameDecoder`、`StringDecoder`、`ProtobufDecoder`。
- **编码器（Encoder）是出站**：`业务对象 → ByteBuf`，走 tail→head。如 `LengthFieldPrepender`、`StringEncoder`、`ProtobufEncoder`。

---

## 七、零拷贝

### Q25. Netty 的零拷贝体现在哪里？

**要点速答**：两个层面：

1. **OS 层零拷贝**：堆外 `ByteBuf` + `FileRegion` + Linux `sendfile`/`mmap`，文件传输不经用户态，直接内核态到网卡。
2. **ByteBuf 逻辑零拷贝**：
   - `slice` / `duplicate`：共享底层 buffer，只复制视图指针，不复制数据。
   - `CompositeByteBuf`：把多个 ByteBuf 逻辑合并成一个，无需内存拷贝（NIO 要靠 `ByteBuffer` 数组或合并拷贝）。
   - `Unpooled.wrappedBuffer(...)`：包装已有字节数组为 ByteBuf，不拷贝。

**深入展开**：`CompositeByteBuf` 是经典考点——传统合并多段要用 `System.arraycopy` 拷贝，Composite 用指针数组逻辑拼装，写入时按段写出。

---

### Q26. `FileRegion` 是怎么实现零拷贝的？

**要点速答**：`DefaultFileRegion` 封装 `FileChannel.transferTo`，底层调用 Linux `sendfile(2)`，数据从文件直接进 socket 缓冲区，**零次用户态拷贝**。常用于大文件下发。

注意：`FileRegion` 默认不能复用（已传输的 position 不重置），且依赖 `FileChannel` 不能提前关闭。

---

## 八、异步模型与 Future

### Q27. Netty 的异步模型是什么？ChannelFuture 和 Promise 的关系？

**要点速答**：所有 IO 操作（bind/connect/write）**立即返回 `ChannelFuture`**，真正完成在 EventLoop 线程。

- `ChannelFuture`：只读视图，查结果 + 加监听器。
- `Promise`（`ChannelPromise`）：可写的 Future，操作执行方通过它设成功/失败。

**深入展开**：本项目 `NettyServer` 两种用法都有：

```java
ChannelFuture cf = bootstrap.bind(6668).sync();   // 阻塞直到完成
cf.addListener(future -> {                          // 异步回调, 不阻塞
    if (future.isSuccess()) System.out.println("端口监听成功");
});
```

---

### Q28. `sync()` 和 `addListener()` 的区别？生产用哪个？

**要点速答**：
- `sync()`：**阻塞**当前线程直到操作完成，失败抛异常。
- `addListener(...)`：注册回调，操作完成由 EventLoop **异步**通知，不阻塞。

**生产用 listener**，避免阻塞 EventLoop 或业务线程。注意：`sync()` 千万不能在 EventLoop 线程里调（如 handler 里调 `future.sync()`），会**死锁**——等待的正是自己要执行的结果。

---

### Q29. 为什么不能在 EventLoop 线程里调 `future.sync()`？

**要点速答**：死锁。`sync()` 阻塞等待结果，而结果要靠这个 EventLoop 线程去执行才能产生——线程被自己阻塞，永远等不到结果。Netty 的 `BlockingOperationException` 和检测逻辑会在某些路径抛出。正确做法：在 EventLoop 里用 `addListener` 异步处理。

---

### Q30. `closeFuture().sync()` 在服务端的作用？

**要点速答**：**阻塞主线程**等待 Channel 关闭，防止 `main` 方法执行完 JVM 退出。本项目 `NettyServer`：

```java
channelFuture.channel().closeFuture().sync();   // 守护服务端不退出
```

服务端常驻，主线程阻塞在这里，直到 channel 被关闭（如收到关闭信号）。客户端也常这样等。

---

## 九、内存管理与泄漏检测

### Q31. ByteBuf 的引用计数是什么？什么时候 release？

**要点速答**：ByteBuf 实现 `ReferenceCounted`，初始计数 1，`retain` +1，`release` -1，归 0 时回收内存。

**释放时机原则**：
1. **读到的 ByteBuf**：`channelRead` 中读完即可 release，或用 `SimpleChannelInboundHandler` 自动释放。
2. **自己创建的 ByteBuf**：写出（`writeAndFlush`）后由出站链负责释放，框架会 release。
3. **传递给下游的 ByteBuf**：不能提前 release，要等下游消费。

**深入展开**：常见泄漏是"读到的 ByteBuf 既不释放也不传递"，或"在 handler 里 retain 了忘记 release"。

---

### Q32. Netty 怎么检测内存泄漏？

**要点速答**：`ResourceLeakDetector`。它在分配 ByteBuf 时（按采样率）创建 `WeakReference` 记录分配栈，在 release 时检查；如果 ByteBuf 被 GC 回收时引用计数仍 >0，说明没 release，打印泄漏路径。

四个级别：
- `DISABLED`：关闭。
- `SIMPLE`（默认生产）：1% 采样，只打印是否泄漏。
- `ADVANCED`：1% 采样，打印泄漏点的访问栈。
- `PARANOID`（开发调试）：100% 全检，开销大。

设 `-Dio.netty.leakDetection.level=PARANOID` 开全检。

---

### Q33. 堆外内存泄漏和 OOM 怎么排查？

**要点速答**：
1. 开 `PARANOID` 级别泄漏检测，看日志的分配栈定位没 release 的点。
2. 监控堆外内存（`-XX:NativeMemoryTracking=detail` + `jcmd <pid> VM.native_memory`）。
3. 常见原因：自定义 handler 未释放读到的 ByteBuf、`retain` 后没配对 `release`、`SimpleChannelInboundHandler` 用错（把要传递的 ByteBuf 提前释放）。

---

## 十、性能调优

### Q34. Netty 高性能的来源有哪些？

**要点速答**：
1. 主从 Reactor + 串行化无锁（绑定线程）。
2. 零拷贝（ByteBuf 逻辑层 + OS sendfile）。
3. 池化 ByteBuf，减 GC。
4. 堆外内存，少拷贝。
5. 高效并发数据结构（MPSC 队列）。
6. epoll/kqueue native transport（Linux/macOS）。
7. 无锁化设计 + 事件驱动避免空转。

---

### Q35. Netty 调优有哪些实战参数？

**要点速答**：

| 关注点 | 做法 |
|---|---|
| boss 线程 | `new NioEventLoopGroup(1)` |
| worker 线程 | 默认 `CPU*2`，CPU 密集可调小 |
| ioRatio | IO 重的调高（如 70） |
| 内存 | 默认池化堆外 `PooledByteBufAllocator.DEFAULT` |
| recv/send buffer | `ChannelOption.SO_RCVBUF/SO_SNDBUF` |
| 背压 | `WRITE_BUFFER_HIGH_WATER_MARK` 防 OOM |
| 连接队列 | `SO_BACKLOG`（全连接队列） |
| keepalive | `SO_KEEPALIVE`、`IdleStateHandler` 检测空闲 |
| Linux | `EpollEventLoopGroup` + `EpollServerSocketChannel` |
| TCP_NODELAY | 关 Nagle 减小延迟（客户端常开） |

---

### Q36. 什么是流量整形（Traffic Shaping）？

**要点速答**：`ChannelTrafficShapingHandler` / `GlobalTrafficShapingHandler` 限速，按配置的 bytes/sec 控制读写速率，超速时把数据缓存延迟发送。用于带宽限流、保护下游。

---

### Q37. `IdleStateHandler` 是干什么的？

**要点速答**：空闲检测。在 pipeline 加 `IdleStateHandler(readerIdleTime, writerIdleTime, allIdleTime)`，到时间没读写就触发 `userEventTriggered`，可在此发心跳、关闭半死连接。常配合心跳保活。

---

## 十一、高频场景题

### Q38. Netty 怎么实现心跳保活？

**要点速答**：客户端定时发心跳（如 `IdleStateHandler` 写空闲 30s 触发，在 `userEventTriggered` 里发 ping）；服务端读空闲 60s 触发关闭半死连接。双向心跳解决：
- 客户端：检测服务端是否存活，及时重连。
- 服务端：检测客户端是否假死，回收连接。

**追问：为什么不用 TCP keepalive？** TCP keepalive 默认 2 小时才探测，太慢；应用层心跳可自定义间隔，且能携带业务信息。

---

### Q39. Netty 如何实现重连？

**要点速答**：客户端在 `channelInactive` 或 `userEventTriggered`（读空闲超时）时，用 `EventLoop.schedule` 延迟重连：

```java
public void channelInactive(ChannelHandlerContext ctx) {
    ctx.channel().eventLoop().schedule(() -> reconnect(), 5, TimeUnit.SECONDS);
}
```

要点：用**退避**（指数退避避免风暴）、限制最大重连次数、重连时新建 Bootstrap。注意 `Bootstrap` 可复用（线程组、channel 工厂）。

---

### Q40. Netty 服务端怎么管理大量连接？

**要点速答**：
1. 用集合（`ChannelGroup` 或 `ConcurrentHashMap<ChannelId, Channel>`）管理。
2. 推送时遍历集合，把消息 `writeAndFlush` 到各 channel——可直接 write，或把任务提交到各 channel 的 EventLoop。
3. `ChannelGroup` 写入会自动排除已关闭的，且广播高效。
4. 配合 `IdleStateHandler` 清理半死连接防内存泄漏。

本项目 `NettyServer` 注释提到的"用集合管理 SocketChannel"即此思路。

---

### Q41. Netty 里怎么实现自定义协议？

**要点速答**：定义 `[魔数][版本][长度域][消息体]` 之类格式，pipeline 装配：
- `LengthFieldBasedFrameDecoder` 拆包（可能带 offset 处理魔数+版本头）。
- 自定义 `MessageDecoder extends ByteToMessageDecoder`：解析魔数、版本、命令，反序列化消息体为业务对象。
- 自定义 `MessageEncoder extends MessageToByteEncoder`：把业务对象序列化 + 拼协议头。
- 业务 handler 收到的已是 `Message` 对象。

---

### Q42. Netty 怎么做 HTTP/WebSocket 服务？

**要点速答**：用 `netty-codec-http`：
- HTTP：pipeline 加 `HttpServerCodec`（HttpRequestDecoder + HttpResponseEncoder 合体）、`HttpObjectAggregator`（聚合成 FullHttpRequest）、业务 handler。
- WebSocket：HTTP 链基础上加 `WebSocketServerProtocolHandler("/ws")` 做 handshake，之后业务按 `WebSocketFrame` 处理。

---

### Q43. Netty 怎么集成 SSL/TLS？

**要点速答**：用 `SslContextBuilder` 构建 `SslContext`，pipeline **头部**加 `sslContext.newHandler(ch.alloc())`，它负责握手加解密，后续 handler 看到的已是明文。注意：SSL handler 要在最前，在编解码器之前。

---

## 十二、对比题

### Q44. Netty vs Java NIO？（见 Q1）

### Q45. Netty vs Mina？

**要点速答**：都是 NIO 框架。Mina（Apache）较老、API 略繁、社区活跃度低；Netty 性能更好、生态更全（HTTP/Protobuf/WebSocket/SSL）、API 更现代、被大量项目（gRPC/Spark/Dubbo）采纳。新项目选 Netty。

---

### Q46. Netty 的 NIO vs epoll transport？

**要点速答**：
- **NioEventLoopGroup**：跨平台，基于 JDK Selector，Linux 上有 epoll 但经 JDK 封装有开销。
- **EpollEventLoopGroup**（需 native 库）：Linux 专属，直接调 epoll，支持 `ET 边沿触发`、`sendfile`、`TCP_FASTOPEN` 等，性能更高，延迟更低。

生产 Linux 环境优先 epoll。本项目 pom 引了 `netty-transport-native-epoll` 即为此。

---

### Q47. 单线程 Reactor vs 主从 Reactor 该怎么选？

**要点速答**：
- 连接少、业务极轻：单线程够，省线程开销。
- 高并发：主从。boss 专门 accept，worker 多线程处理，业务可再丢业务线程池。
- 实践：99% 场景用主从 + 业务线程池。

---

## 十三、源码级追问

### Q48. NioEventLoop 是怎么注册 channel 的？accept 后怎么分发给 worker？

**要点速答**：
1. ServerSocketChannel 注册到 boss 的 Selector，关注 OP_ACCEPT。
2. boss `processSelectedKeys` 处理 OP_ACCEPT，`doReadMessages` 调 `serverSocket.accept()` 拿到子 SocketChannel。
3. 把子 channel 经 `ServerBootstrapAcceptor`（一个 ChannelInboundHandler）传到 `workerGroup`，调 `childGroup.register(child)`。
4. `register` 内部选一个 worker 的 NioEventLoop（`EventExecutorChooserFactory` 取模/轮询选），把 channel 的 OP_READ 注册到该 worker 的 Selector。
5. 此后该 channel 的所有事件都在该 worker 线程。

---

### Q49. Netty 怎么规避 JDK epoll 空轮询 bug？

**要点速答**：JDK 在某些 Linux 内核上，`select()` 在没事件时也立即返回（不阻塞），导致 CPU 100%。Netty 的 `NioEventLoop` 用一个计数器记录"select 提前返回且无事件"的次数，超过阈值（默认 512，`SELECTOR_AUTO_REBUILD_THRESHOLD`）就**重建一个新的 Selector**，把旧 Selector 的 key 迁移过去，丢弃坏的。

---

### Q50. ChannelPipeline 的 head 和 tail 是什么？

**要点速答**：pipeline 链表有两个内置节点：
- **HeadContext**：链头，既是入站起点也是出站终点，持有 `unsafe`（实际执行 socket IO：read/write/close）。
- **TailContext**：链尾，入站终点（没被消费的消息在这里 release + 警告）、出站起点（业务调 `channel.writeAndFlush` 从这里开始向前走）。

业务 `addLast` 的 handler 插在 head 和 tail 之间。

---

### Q51. `writeAndFlush` 的完整执行流程？

**要点速答**：
1. 业务调 `ctx.writeAndFlush(msg)`（或 `channel.writeAndFlush`）。
2. 沿出站链 tail→head，依次经过各 `ChannelOutboundHandler`（Encoder 把业务对象→ByteBuf）。
3. 到达 HeadContext，`unsafe.write` 把 ByteBuf 加到 `ChannelOutboundBuffer`（发送缓冲队列）。
4. `unsafe.flush` 触发实际写：遍历缓冲队列，`doWrite` 调底层 `SocketChannel.write` 写到 socket。
5. 写不完（SO_SNDBUF 满）则注册 OP_WRITE，等可写再继续；写完取消 OP_WRITE。

---

### Q52. `ChannelOutboundBuffer` 是什么？和背压什么关系？

**要点速答**：`ChannelOutboundBuffer` 是 channel 的出站缓冲队列，待写数据先入这里再 flush 到 socket。有高低水位线（`WRITE_BUFFER_LOW/HIGH_WATER_MARK`）：
- 待写堆积超高水位 → `isWritable()` 变 false → 触发 `channelWritabilityChanged`，业务应停止写入（背压）。
- 降到低水位 → 恢复可写 → 通知业务可继续写。

防 OOM 的关键：写入快于网络消费时，背压让上游减速。

---

### Q53. Netty 的 FastThreadLocal 是什么？为什么比 JDK ThreadLocal 快？

**要点速答**：`FastThreadLocal` 用**固定索引**（每个 FastThreadLocal 实例一个递增 index）直接数组访问，O(1) 且无 hash 冲突；JDK `ThreadLocal` 用 `ThreadLocalMap` 哈希表，有探测开销。前提是线程是 Netty 的 `FastThreadLocalThread`（EventLoop 线程即是），普通线程会退化到带 hash 的慢路径。常用于 ByteBuf allocator、内部状态等。

---

## 附：高频一句话速答卡（面试临场）

| 问 | 答 |
|---|---|
| Netty 核心组件 | EventLoopGroup/Channel/Pipeline/ByteBuf |
| 线程模型 | 主从 Reactor |
| boss/worker | boss 接连接，worker 干 IO；boss 默认 1 个 |
| 粘包解决 | 长度域 `LengthFieldBasedFrameDecoder` |
| 零拷贝 | ByteBuf 组合/slice + OS sendfile |
| ByteBuf vs ByteBuffer | 双指针、池化、堆外、引用计数 |
| 异步怎么等 | listener 回调，别在 EventLoop 里 sync |
| 内存泄漏检测 | ResourceLeakDetector，PARANOID 全检 |
| 性能优化 | epoll、池化堆外、串行无锁、ioRatio |
| 心跳 | IdleStateHandler + userEventTriggered |
| epoll 空轮询 | 重建 Selector 规避 |

---

> 配套代码：本文涉及的可运行示例见 `com.zoo.whale.io.netty` 包下 `NettyServer/NettyClient/NettyServerHandler` 与 `LengthServer/LengthClient/LengthServerHandler/LengthClientHandler`；架构总览见 [Netty整体架构和深入理解.md](Netty整体架构和深入理解.md)。
