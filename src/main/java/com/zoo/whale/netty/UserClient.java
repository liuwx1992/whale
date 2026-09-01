package com.zoo.whale.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @Date: 2024年 01月 02日
 * @Time:20时:01分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class UserClient {

    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(new NioEventLoopGroup());

        bootstrap.channel(NioSocketChannel.class);

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                //...

            }
        });

        bootstrap.connect();

        bootstrap.connect("127.0.0.1",8081).sync().channel();
    }

}
