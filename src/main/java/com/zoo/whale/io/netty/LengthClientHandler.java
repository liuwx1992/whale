package com.zoo.whale.io.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * LengthClient 的业务 handler
 *
 * 连接建立后(channelActive)连发 3 条消息, 演示粘包/半包场景下
 * 服务端依然能按长度域正确拆出每一条
 *
 * @Date: 2026年 08月 31日
 * @Time:16时:45分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class LengthClientHandler extends ChannelInboundHandlerAdapter {

    private static final List<String> MESSAGES = Arrays.asList(
            "hello",
            "this is a longer message to show half-packet handling",
            "X"
    );

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 连续写出多条, 触发 TCP 粘包/半包, 验证长度域拆包
        for (String msg : MESSAGES) {
            ctx.writeAndFlush(msg);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 经 StringDecoder 后已是 String
        System.out.println("客户端收到回复: " + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
