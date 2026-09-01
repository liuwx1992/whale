# Netty 整体架构和深入理解

> **文档版本**: v1.0
> **创建日期**: 2026-08-31
> **编写人**: weixin.liu
> **Netty 版本**: 4.1.101.Final（见 `pom.xml` 中 `<netty.version>`）
> **用途**: 从架构总览到核心组件逐一拆解 Netty 的运行机制，并结合本项目 `com.zoo.whale.io.netty` 下的可运行示例说明，供后续大模型阅读与排障

---

## 0. 一句话定位

Netty 是一个**基于 NIO 的、事件驱动的、异步的**高性能网络通信框架。它把 Java 原生 NIO 那套繁琐且容易出错的 `Selector / Channel / ByteBuffer` 包装成**Reactor 线程模型 + 责任链 Pipeline + 零拷贝 ByteBuf**，让你只关心"读到什么、写回什么"（即 `ChannelHandler`），而不用关心"怎么读、怎么写、谁来读"。

---

## 1. 整体架构总览

```
                       ┌────────────────────────────────────────────┐
                       │             Bootstrap (引导器)              │
                       │   ServerBootstrap(服务端) / Bootstrap(客户端) │
                       └───────────────┬────────────────────────────┘
                                       │ 配置
                  ┌────────────────────┼─────────────────────┐
                  ▼                    ▼                     ▼
          ┌──────────────┐    ┌──────────────────┐   ┌────────────────┐
          │ EventLoopGroup│    │  Channel(NIO/OIO)│   │ ChannelHandler │
          │  boss + worker│    │  通道 + 网络操作  │   │  业务处理器     │
          └──────┬───────┘    └────────┬─────────┘   └────────┬───────┘
                 │                     │                       │
                 └──────────┬──────────┘                       │
                            ▼                                   │
                  ┌──────────────────┐   装配(handler 链)        │
                  │   ChannelPipeline │◄────────────────────────┘
                  │   责任链(双向)    │
                  └────────┬─────────┘
                           │ 读写数据载体
                           ▼
                  ┌──────────────────┐
                  │     ByteBuf      │
                  │  堆内/堆外/池化   │
                  └──────────────────┘
```

核心五大件：

| 组件 | 作用 | 本项目对应代码 |
|---|---|---|
| Bootstrap / ServerBootstrap | 引导器，组装线程组、通道类型、处理器链并启动 | `io/netty/NettyServer.java`、`io/netty/NettyClient.java` |
| EventLoopGroup / EventLoop | 事件循环组，本质是一组带 Selector 的线程，负责所有 IO 与任务 | `new NioEventLoopGroup()` |
| Channel | 网络通道抽象（连接、读、写、绑定），封装了底层 Socket | `NioServerSocketChannel` / `NioSocketChannel` |
| ChannelPipeline / ChannelHandler | 责任链 + 业务处理器，数据在链上经入站/出站处理流转 | `NettyServerHandler` 经 `addLast` 加入 |
| ByteBuf | Netty 自研的字节容器，替代 NIO 的 `ByteBuffer` | `io.netty.buffer.ByteBuf` |

---

## 2. Reactor 线程模型

Netty 采用的是**主从 Reactor 多线程模型**，这也是它高性能的根本。

### 2.1 三种 Reactor 模型对照

| 模型 | 结构 | 适用场景 |
|---|---|---|
| 单线程 Reactor | 一个线程扛连接 + IO + 业务 | 连接少、业务快 |
| 多线程 Reactor | 1 个 Reactor 接连接，N 个 worker 线程处理 IO/业务 | 大多数场景 |
| **主从 Reactor** | boss 接连接后**派发**给 subReactor，sub 各自负责一批连接的 IO | Netty 默认，高并发首选 |

### 2.2 本项目的 boss / worker 体现

`io/netty/NettyServer.java`：

```java
// 1. 创建两个线程组：boss 接连接，worker 干业务
EventLoopGroup bossGroup   = new NioEventLoopGroup();   // 默认线程数 = CPU 核数 * 2
EventLoopGroup workerGroup = new NioEventLoopGroup();

ServerBootstrap bootstrap = new ServerBootstrap();
bootstrap.group(bossGroup, workerGroup)        // boss + worker = 主从 Reactor
         .channel(NioServerSocketChannel.class)
         .option(ChannelOption.SO_BACKLOG, 128) // 全连接队列长度
         .childHandler(new ChannelInitializer<SocketChannel>() {  // 给 worker 的 pipeline 装配 handler
             @Override
             protected void initChannel(SocketChannel ch) {
                 ch.pipeline().addLast(new NettyServerHandler());
             }
         });
```

要点：
1. `bossGroup` **只处理连接请求**（OP_ACCEPT），拿到新 SocketChannel 后，把它**注册到 workerGroup 的某个 NioEventLoop** 上。
2. `workerGroup` 负责该连接后续的所有读写与业务。一个连接从建立到关闭，**始终绑定在同一个 NioEventLoop（同一个线程）上**——这就是 Netty 的串行化无锁设计，避免了多线程并发操作同一连接。
3. 两个组都是"无限循环"（`for(;;)` 跑 Selector.select），通过 `shutdownGracefully()` 优雅关闭。
4. `NioEventLoop` 默认线程数 = `CPU 核数 * 2`（见 `MultithreadEventExecutorGroup` 的 `DEFAULT_EVENT_LOOP_THREADS`）。

### 2.3 boss / worker 的几个高频追问

- **能不能只用一个 group？** 可以。`bootstrap.group(g)` 单参重载会让同一个组既当 boss 又当 worker；但生产环境几乎都用双 group。
- **boss 只用 1 个线程够吗？** 够。boss 只做 accept，1 个线程足以扛满千兆网卡的连接速率，多了反而浪费。生产实践常写 `new NioEventLoopGroup(1)`。
- **`handler(...)` 和 `childHandler(...)` 的区别？** `handler` 装配到 **boss 的 pipeline**（处理 ServerSocketChannel 的事件）；`childHandler` 装配到 **worker 的 pipeline**（处理每个客户端 SocketChannel）。本项目只用 `childHandler`，boss 没有自定义 handler。

---

## 3. EventLoop / EventLoopGroup 深入

### 3.1 本质

`NioEventLoop` 本质 = **一个单线程执行器 + 一个 Selector + 一个任务队列（MPSC）**。它的 `run()` 方法干三件事，循环往复：

```
1. select：轮询 IO 事件（OP_ACCEPT / OP_READ / OP_WRITE）
2. processSelectedKeys：处理就绪的 IO 事件，触发 pipeline 上的 handler
3. runAllTasks：执行 taskQueue / scheduledTaskQueue 里的非 IO 任务
```

### 3.2 任务队列的用途

handler 是在 EventLoop 线程里跑的。如果一个 handler 要做**耗时的非 IO 操作**（如查库），直接写就会**阻塞这个 EventLoop**——而这个 EventLoop 上还绑着成百上千个连接，全跟着卡。两种解法：

1. **业务自定义线程池**：handler 里把耗时任务 `submit` 到自己的业务线程池，处理完再用 `ctx.writeAndFlush()` 回写（注意 write 必须回到 EventLoop 线程，Netty 内部会自动 `inEventLoop` 判断并切换）。
2. **addLast 时指定 EventExecutorGroup**：`pipeline.addLast(businessGroup, handler)`，让这个 handler 在独立线程组执行，其余 handler 仍走 IO 线程。

> 本项目 `NettyServerHandler.channelRead` 里直接 `System.out.println` 模拟业务，是教学代码；生产环境务必避免在 EventLoop 线程做阻塞操作。

### 3.3 串行化无锁

因为一个连接的所有事件都由同一个 EventLoop 线程处理，所以 handler 内部对同一连接的操作天然单线程，**不需要加锁**。这也是 `channelRead` 里看到的 `ctx.channel()`、`ctx.pipeline()` 都是同一个上下文、可安全使用的原因。

---

## 4. Channel 与 ChannelPipeline

### 4.1 Channel

`Channel` 是对网络 socket 的抽象。常见实现：

| Channel | 用途 |
|---|---|
| `NioServerSocketChannel` | 服务端监听通道（accept） |
| `NioSocketChannel` | NIO 客户端/服务端连接通道 |
| `EpollServerSocketChannel` | Linux epoll 实现，零拷贝、更高性能（需 native 库，本项目 pom 引了 `netty-transport-native-epoll`） |
| `LocalChannel` | 同 JVM 内通信，不走网络 |

Channel 提供的关键能力：`bind / connect / read / write / flush / close`，全部**异步**返回 `ChannelFuture`。

### 4.2 ChannelPipeline 责任链

每个 `Channel` 拥有一个 `ChannelPipeline`，pipeline 是一条**双向链表**，节点是 `ChannelHandlerContext`，每个 context 持有一个 `ChannelHandler`。

```
入站方向(head -> tail)        出站方向(tail -> head)
  Socket 读到的字节              业务要写出的字节
      │                              ▲
   head │                              │ head
   [Decoder]  入站               出站 [Encoder]
   [Business] 入站               出站
   tail │                              │ tail
      └────────────  Channel  ─────────┘
```

- **入站（Inbound）**：数据从 socket 进来，从 head 往 tail 流，由 `ChannelInboundHandler` 处理（如解码、读业务）。本项目 `NettyServerHandler extends ChannelInboundHandlerAdapter` 即入站。
- **出站（Outbound）**：数据要写出去，从 tail 往 head 流，由 `ChannelOutboundHandler` 处理（如编码、flush）。
- `addLast` 默认加到 tail 前；`addFirst` 加到 head 后。

`NettyServerHandler` 里打印的这行说明了 channel 和 pipeline 的关系：

```java
Channel channel   = ctx.channel();     // 当前连接
ChannelPipeline pipeline = ctx.pipeline();  // 该连接的 pipeline
// ctx.channel().pipeline() == ctx.pipeline() == pipeline，三者等价指向同一条链
```

### 4.3 常用入站回调（生命周期 + 数据）

| 方法 | 触发时机 |
|---|---|
| `channelActive` | 连接建立（激活）时 |
| `channelRead(ctx, msg)` | 读到数据时 |
| `channelReadComplete` | 一次读周期完成时 |
| `channelInactive` | 连接断开时 |
| `exceptionCaught` | 链上任一 handler 抛异常时 |
| `handlerAdded/Removed` | handler 加入/移出 pipeline |

本项目 `NettyServerHandler` 实现了三个：`channelRead`（读消息）、`channelReadComplete`（回写）、`exceptionCaught`（关闭通道）。

---

## 5. ChannelHandler 编写规范

`io/netty/NettyServerHandler.java`：

```java
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;                       // Netty 的 ByteBuf，非 NIO 的 ByteBuffer
        System.out.println("客户端发送消息是:" + buf.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        // writeAndFlush = write + flush：写入发送缓冲并刷新出去
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~", CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();   // 异常时关闭通道，避免连接泄漏
    }
}
```

要点：
1. 自定义 handler **必须继承** Netty 规定的某个适配器（`ChannelInboundHandlerAdapter` / `ChannelOutboundHandlerAdapter` / `SimpleChannelInboundHandler<T>`），否则没法被 pipeline 识别。
2. `ctx.writeAndFlush()` 默认从**当前 handler 往前**走出站链；想从 head 开始可用 `ctx.channel().writeAndFlush()`（性能略低）。
3. `exceptionCaught` 一定要处理，否则异常会一直传到 tail 被 Netty 默默吞掉并打日志，连接可能僵死。

---

## 6. ByteBuf

### 6.1 与 NIO ByteBuffer 的区别

| 维度 | NIO ByteBuffer | Netty ByteBuf |
|---|---|---|
| 读写指针 | 单 `position`，读写要 `flip()` 切换 | 双指针 `readerIndex` / `writerIndex`，读写独立 |
| 容量 | 固定，需手动扩容 | `capacity`、可自动 `ensureWritable` 扩容 |
| 池化 | 无 | `PooledByteBufAllocator` 池化，减少 GC |
| 堆外 | `allocateDirect` 手动管理 | `directBuffer` 堆外，配合零拷贝 |
| 引用计数 | 无 | `ReferenceCounted`，用完要 `release()` |

### 6.2 三个指针

```
 +-------------------+------------------+------------------+
 | discardable bytes |  readable bytes  |  writable bytes  |
 |   已读(可丢弃)    |    (内容区)       |    (可写区)      |
 +-------------------+------------------+------------------+
 0      <=      readerIndex   <=   writerIndex    <=    capacity
```

### 6.3 两种内存

- **堆内（heap）**：分配快、GC 能回收，但 socket 写要额外拷一次到直接内存。
- **堆外（direct）**：少一次拷贝（零拷贝），读写快，但分配慢、不受 GC 管理，需手动释放。

本项目客户端读到的 `ByteBuf` 是 Netty 从 socket 读出后分配的（默认池化堆外），用完框架会自动 release；你若自己 `Unpooled.copiedBuffer(...)` 创建用于写出，flush 后框架也会释放。

---

## 7. 异步模型与 ChannelFuture

Netty 所有 IO 都是异步的。`bind / connect / write` 立即返回 `ChannelFuture`，真正完成在 EventLoop 线程。

`io/netty/NettyServer.java`：

```java
ChannelFuture channelFuture = bootstrap.bind(6668).sync();

// 给 future 注册监听器，完成时回调（异步）
channelFuture.addListener(new ChannelFutureListener() {
    @Override
    public void operationComplete(ChannelFuture future) {
        if (future.isSuccess()) System.out.println("监听端口 6668 成功");
        else                    System.out.println("监听端口 6668 失败");
    }
});

// 阻塞等待通道关闭（守护主线程不退出）
channelFuture.channel().closeFuture().sync();
```

要点：
1. `sync()` = 阻塞当前线程直到异步操作完成，完成失败会抛异常。
2. `addListener(...)` = 注册回调，操作完成时由 EventLoop 线程异步通知，**不阻塞**。生产推荐用 listener 而非 sync。
3. `closeFuture().sync()` 是服务端惯用套路：主线程阻塞在这里等服务器关闭信号，避免 main 方法结束后 JVM 退出。

---

## 8. 客户端引导 Bootstrap

`io/netty/NettyClient.java`：

```java
EventLoopGroup group = new NioEventLoopGroup();
Bootstrap bootstrap = new Bootstrap();     // 注意：客户端用 Bootstrap，不是 ServerBootstrap

bootstrap.group(group)                      // 客户端只有一个 group（既是 boss 又是 worker）
         .channel(NioSocketChannel.class)
         .handler(new ChannelInitializer<SocketChannel>() {
             @Override
             protected void initChannel(SocketChannel ch) {
                 ch.pipeline().addLast(new NettyClientHandler());
             }
         });

ChannelFuture cf = bootstrap.connect("127.0.0.1", 6668).sync();
cf.channel().closeFuture().sync();
```

与 ServerBootstrap 的差异：
| 项 | ServerBootstrap | Bootstrap |
|---|---|---|
| 线程组 | `group(boss, worker)` 双参 | `group(group)` 单参 |
| 通道 | `NioServerSocketChannel` | `NioSocketChannel` |
| 处理器装配 | `childHandler`（worker 的） | `handler`（自己的） |
| 启动动作 | `bind(port)` | `connect(host, port)` |

---

## 9. 一次完整通信时序（服务端视角）

```
1. bossGroup 的 NioEventLoop 轮询到 OP_ACCEPT
2. 调 NioServerSocketChannel.accept() 拿到 SocketChannel
3. boss 把 SocketChannel 注册到 workerGroup 的某个 NioEventLoop（轮询/分配）
4. 该 worker 的 Selector 轮询到 OP_READ
5. 读到字节 -> 包装成 ByteBuf -> 触发 pipeline 入站链
6. head -> [Decoder] -> NettyServerHandler.channelRead(ctx, ByteBuf)
7. channelReadComplete -> ctx.writeAndFlush -> 触发出站链
8. head flush -> socket 发出
9. 客户端断开 -> channelInactive -> exceptionCaught/close
```

整个过程，从读到写都在**同一个 worker 线程**里完成，跨 handler 不跨线程。

---

## 10. 进阶：粘包/半包与编解码

TCP 是流式协议，没有边界，必然出现粘包/半包。Netty 预置了拆包器，在 pipeline 头部加入：

| 拆包器 | 策略 |
|---|---|
| `LineBasedFrameDecoder` | 按换行符 `\n` |
| `DelimiterBasedFrameDecoder` | 按自定义分隔符 |
| `FixedLengthFrameDecoder` | 固定长度 |
| `LengthFieldBasedFrameDecoder` | 长度域（最常用，自定义协议首选） |

典型装配：

```java
ch.pipeline()
  .addLast(new LengthFieldBasedFrameDecoder(maxLen, 0, 4, 0, 4))  // 拆包
  .addLast(new StringDecoder(CharsetUtil.UTF_8))                   // ByteBuf -> String
  .addLast(new StringEncoder(CharsetUtil.UTF_8))                   // String -> ByteBuf
  .addLast(new NettyServerHandler());                              // 业务
```

本项目教学代码没加拆包器，所以多包一次发、少包一次收是正常的——这也是从教学到生产必须补的一环。已在本仓库补了一套**可运行示例**，可直接 `main` 启动验证：`io/netty/LengthServer.java`（端口 6669）+ `io/netty/LengthClient.java`，pipeline 对称装配如下：

```java
ch.pipeline()
  .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))  // 入站按长度域拆包, 剥掉长度域
  .addLast(new LengthFieldPrepender(4))                                      // 出站自动前置 4 字节长度域
  .addLast(new StringDecoder(CharsetUtil.UTF_8))                             // ByteBuf -> String
  .addLast(new StringEncoder(CharsetUtil.UTF_8))                             // String -> ByteBuf
  .addLast(new LengthServerHandler());                                       // 业务(收到的已是 String)
```

参数解读（`LengthFieldBasedFrameDecoder(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip)`）：
- `maxFrameLength=Integer.MAX_VALUE`：单帧上限，生产应设合理上限防 OOM。
- `lengthFieldOffset=0`：长度域从第 0 字节开始。
- `lengthFieldLength=4`：长度域占 4 字节（big-endian）。
- `lengthAdjustment=0`：长度值即消息体长度，无需补偿。
- `initialBytesToStrip=4`：拆包后剥掉长度域，只把消息体交给后续 handler。

客户端 `LengthClientHandler` 在 `channelActive` 连发 3 条不等长消息，正好制造粘包/半包；服务端 `LengthServerHandler` 仍能逐条正确接收并回显，可直接运行对照。

---

## 11. 零拷贝

Netty 的"零拷贝"有两层：

1. **ByteBuf 层面**：`slice / duplicate / composite` 逻辑组合多个 ByteBuf，不复制底层数据。
2. **OS 层面**：堆外 `ByteBuf` + `FileRegion` + Linux `sendfile`，文件传输直接从内核态到网卡，不进用户态。

堆外内存认准 `sendfile`，是大文件下发、网关转发的关键优化点。

---

## 12. 性能与稳定性要点（实战清单）

| 关注点 | 做法 |
|---|---|
| boss 线程数 | `new NioEventLoopGroup(1)`，多了浪费 |
| worker 线程数 | 默认 `CPU*2` 已合适，CPU 密集型场景可调小 |
| 业务阻塞 | 绝不在 EventLoop 线程做阻塞 IO，必须丢业务线程池 |
| 粘包 | 入站链首加拆包器，按协议定长/分隔/长度域 |
| 内存泄漏 | 自建 ByteBuf 用完 `release()`，或用 `SimpleChannelInboundBuffer` 自动释放 |
| 内存 | 高并发优先池化堆外（`PooledByteBufAllocator.DEFAULT`，默认即此） |
| 日志 | 接入 `LoggingHandler` 排查，上线移除 |
| Linux | 用 `EpollEventLoopGroup` + `EpollServerSocketChannel`，性能优于 NIO |
| 背压 | 高写流量关注 `ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK`，防止 OOM |
| 优雅关闭 | `shutdownGracefully()` 而非 `shutdown()` |

---

## 13. 涉及类清单（本项目）

| 类 | 路径 | 职责 |
|---|---|---|
| NettyServer | `io/netty/NettyServer.java` | 服务端引导：双 group + ServerBootstrap + bind 6668 |
| NettyServerHandler | `io/netty/NettyServerHandler.java` | 入站 handler：读消息 / 回写 / 异常关闭 |
| NettyClient | `io/netty/NettyClient.java` | 客户端引导：单 group + Bootstrap + connect |
| NettyClientHandler | `io/netty/NettyClientHandler.java` | 客户端入站 handler |
| **LengthServer** | `io/netty/LengthServer.java` | **长度域协议服务端：bind 6669，装配拆包器** |
| **LengthServerHandler** | `io/netty/LengthServerHandler.java` | **业务 handler：收 String，回显 ack** |
| **LengthClient** | `io/netty/LengthClient.java` | **长度域协议客户端：与服务端对称 pipeline** |
| **LengthClientHandler** | `io/netty/LengthClientHandler.java` | **连发 3 条不等长消息触发粘包/半包** |
| NettyServer | `io/nio/netty/NettyServer.java` | 早期 demo：仅演示 `NioEventLoopGroup(1)` 构造 |
| UserClient | `netty/UserClient.java` | 客户端最小骨架（无 handler 实现） |
| ZzkfServer | `netty/ZzkfServer.java` | 服务端最小骨架，bind 8081 |

包根 `src/main/java/com/zoo/whale/`，依赖由 `pom.xml` 的 `netty-all 4.1.101.Final` 与 `netty-transport-native-epoll` 提供。

---

## 14. 一张图串起核心概念

```
ServerBootstrap.group(boss, worker)            // Reactor 线程模型
    .channel(NioServerSocketChannel.class)     // Channel：网络抽象
    .option(SO_BACKLOG, 128)                    // 全连接队列
    .childHandler(new ChannelInitializer() {   // ChannelPipeline：责任链
        initChannel(ch) {
            ch.pipeline().addLast(             // ChannelHandler：业务
                new LengthFieldBasedFrameDecoder(...),  // 编解码(解决粘包)
                new NettyServerHandler()                 // 业务处理
            );
        }
    })
    .bind(6668).sync()                         // ChannelFuture：异步模型
    .channel().writeAndFlush(buf);             // ByteBuf：数据载体(零拷贝)
```

记住这条链，就抓住了 Netty 的全部骨架：**Reactor 跑线程、Channel 跑网络、Pipeline 跑业务、ByteBuf 跑数据、Future 跑异步**。
