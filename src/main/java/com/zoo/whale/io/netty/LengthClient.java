package com.zoo.whale.io.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * 基于"长度域"协议的 Netty 客户端示例
 *
 * pipeline 必须和服务端对称:
 *   同样的 LengthFieldBasedFrameDecoder / LengthFieldPrepender / StringDecoder / StringEncoder
 * 否则一端加了长度域另一端没解, 必然解析失败
 *
 * @Date: 2026年 08月 31日
 * @Time:16时:44分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class LengthClient {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            p.addLast(new LengthFieldPrepender(4));
                            p.addLast(new StringDecoder(CharsetUtil.UTF_8));
                            p.addLast(new StringEncoder(CharsetUtil.UTF_8));
                            p.addLast(new LengthClientHandler());
                        }
                    });

            System.out.println("客户端 ok.. 连接 127.0.0.1:6669");
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6669).sync();
            channelFuture.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully();
        }
    }
}
