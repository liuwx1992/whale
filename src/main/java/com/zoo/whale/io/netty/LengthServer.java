package com.zoo.whale.io.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * 基于"长度域"协议的 Netty 服务端示例
 *
 * 协议格式: [4字节长度][消息体 UTF-8]
 *
 * 用 LengthFieldBasedFrameDecoder 解决 TCP 粘包/半包:
 *   maxFrameLength       = Integer.MAX_VALUE  单帧上限
 *   lengthFieldOffset    = 0                  长度域从第 0 字节开始
 *   lengthFieldLength    = 4                  长度域占 4 字节
 *   lengthAdjustment     = 0                  长度值就是消息体长度, 无需调整
 *   initialBytesToStrip  = 4                  解析后剥掉长度域, 只把消息体交给后续 handler
 *
 * 配合 LengthFieldPrepender(4) 在写出时自动前置 4 字节长度
 *
 * @Date: 2026年 08月 31日
 * @Time:16时:40分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class LengthServer {

    public static void main(String[] args) {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            // 入站: 按长度域拆包, 剥掉长度域 -> ByteBuf(消息体)
                            p.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            // 出站: 写出前自动前置 4 字节长度域
                            p.addLast(new LengthFieldPrepender(4));
                            // 编解码: ByteBuf <-> String
                            p.addLast(new StringDecoder(CharsetUtil.UTF_8));
                            p.addLast(new StringEncoder(CharsetUtil.UTF_8));
                            // 业务
                            p.addLast(new LengthServerHandler());
                        }
                    });

            System.out.println(".....LengthServer is ready... 端口 6669");

            ChannelFuture channelFuture = bootstrap.bind(6669).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
