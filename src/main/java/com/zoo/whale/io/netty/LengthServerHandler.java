package com.zoo.whale.io.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * LengthServer 的业务 handler
 *
 * 由于前面装配了 StringDecoder, 这里 channelRead 收到的已是 String 而非 ByteBuf
 * 收到消息后回写(回写经 StringEncoder + LengthFieldPrepender 自动加长度域)
 *
 * @Date: 2026年 08月 31日
 * @Time:16时:42分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class LengthServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 经过 StringDecoder 后, msg 已是 String
        String req = (String) msg;
        System.out.println("服务器读取线程 " + Thread.currentThread().getName() + " 收到: " + req);

        // 模拟业务: 把消息大写后回显
        String resp = "ack: " + req.toUpperCase();
        ctx.writeAndFlush(resp);   // 经 StringEncoder + LengthFieldPrepender 加长度域后发出
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
