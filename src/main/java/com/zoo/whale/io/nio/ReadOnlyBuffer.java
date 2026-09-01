package com.zoo.whale.io.nio;

import java.nio.ByteBuffer;

/**
 * @Date: 2023年 12月 08日
 * @Time:16时:46分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class ReadOnlyBuffer {
    public static void main(String[] args) {
        //创建一个 buffer
        ByteBuffer buffer = ByteBuffer.allocate(64);

       //循环put值
        for (int i = 0;i < 64;i++) {
            buffer.put((byte) i);
        }

        //读取
        buffer.flip();

        //得到一个只读的 Buffer
        ByteBuffer readOnlyBuffer = buffer.asReadOnlyBuffer();
        System.out.println(readOnlyBuffer.getClass());


        //读取
        while (readOnlyBuffer.hasRemaining()){
            System.out.println(readOnlyBuffer.get());
        }

        //ReadOnlyBufferException
        readOnlyBuffer.put((byte)100);

    }
}
