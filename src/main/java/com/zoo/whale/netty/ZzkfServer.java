package com.zoo.whale.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @Date: 2024年 01月 02日
 * @Time:19时:36分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class ZzkfServer {

    public static void main(String[] args) {
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup());

        bootstrap.channel(NioServerSocketChannel.class);

        bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                //...
            }
        });
        // 绑定 8081 端口
        bootstrap.bind(8081);

    }

}
