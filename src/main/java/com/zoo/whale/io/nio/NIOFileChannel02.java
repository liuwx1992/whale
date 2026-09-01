package com.zoo.whale.io.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 使用前面学习后的 ByteBuffer（缓冲）和 FileChannel（通道），将 file01.txt 中的数据读入到程序，并显示在控制台屏幕
 * 假定文件已经存在
 * @Date: 2023年 12月 08日
 * @Time:14时:42分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class NIOFileChannel02 {

    public static void main(String[] args) throws Exception {
        //创建文件的输入流
        File file = new File("E:\\壁纸和图片\\pictures\\jindie\\file01.txt");
        FileInputStream fileInputStream = new FileInputStream(file);

        //通过 fileInputStream 获取对应的 FileChannel -> 实际类型 FileChannelImpl
        FileChannel fileChannel = fileInputStream.getChannel();

        //创建缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate((int)file.length());

        //将通道的数据读入到 Buffer
        fileChannel.read(byteBuffer);

        //将 byteBuffer 的字节数据转成 String
        System.out.println(new String(byteBuffer.array()));
        fileChannel.close();
    }

}
