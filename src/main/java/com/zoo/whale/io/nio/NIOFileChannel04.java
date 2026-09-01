package com.zoo.whale.io.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * 3.6.5 应用实例4 - 拷贝文件 transferFrom 方法
 * 实例要求：
 * 使用 FileChannel（通道）和方法 transferFrom，完成文件的拷贝
 * 拷贝一张图片
 *
 * @Date: 2023年 12月 08日
 * @Time:15时:39分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class NIOFileChannel04 {

    public static void main(String[] args) throws IOException {
        FileInputStream fileInputStream = new FileInputStream("E:\\壁纸和图片\\pictures\\jindie\\esc111.jpg");

        FileOutputStream fileOutputStream = new FileOutputStream("E:\\壁纸和图片\\pictures\\jindie\\cloud.jpg");

        FileChannel sourceChannel = fileInputStream.getChannel();

        FileChannel destChannel = fileOutputStream.getChannel();

        //使用 transferForm 完成拷贝
        destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());

        //关闭相关通道和流
        sourceChannel.close();
        destChannel.close();
        fileInputStream.close();
        fileOutputStream.close();

    }

}
