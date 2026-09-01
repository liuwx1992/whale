package com.zoo.whale.io.nio;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 使用前面学习后的 ByteBuffer（缓冲）和 FileChannel（通道），将 "hello,尚硅谷" 写入到 file01.txt 中
 * 文件不存在就创建
 * @Date: 2023年 12月 08日
 * @Time:14时:18分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class NIOFileChannel01 {

    public static void main(String[] args) throws IOException {
        String str = "hello,world";
        //创建一个输出流 -> channel
        FileOutputStream fileOutputStream = new FileOutputStream("E:\\壁纸和图片\\pictures\\jindie\\file01.txt");

        //通过 fileOutputStream 获取对应的 FileChannel
        //这个 fileChannel 真实类型是 FileChannelImpl
        FileChannel fileChannel = fileOutputStream.getChannel();

        //创建一个缓冲区 ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        //将 str 放入 byteBuffer
        byteBuffer.put(str.getBytes());

        //对 byteBuffer 进行 flip
        byteBuffer.flip();

        //将 byteBuffer 数据写入到 fileChannel
        fileChannel.write(byteBuffer);

        fileOutputStream.close();



    }
}
