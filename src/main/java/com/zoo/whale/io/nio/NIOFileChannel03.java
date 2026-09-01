package com.zoo.whale.io.nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 使用 FileChannel（通道）和方法 read、write，完成文件的拷贝
 * 拷贝一个文本文件 1.txt，放在项目下即可
 * @Date: 2023年 12月 08日
 * @Time:14时:52分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class NIOFileChannel03 {

    public static void main(String[] args) throws IOException {
        FileInputStream fileInputStream = new FileInputStream("E:\\壁纸和图片\\pictures\\jindie\\file01.txt");
        FileChannel inputStreamChannel = fileInputStream.getChannel();

        FileOutputStream fileOutputStream = new FileOutputStream("E:\\壁纸和图片\\pictures\\jindie\\file02.txt");
        FileChannel outputStreamChannel = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        while (true) {
            byteBuffer.clear();

            int read = inputStreamChannel.read(byteBuffer);
            System.out.println("read = " + read);
            if(read == -1){
                //表示读完
                break;
            }

            //将 buffer 中的数据写入到 fileChannel02--2.txt
            byteBuffer.flip();

            outputStreamChannel.write(byteBuffer);
        }

        fileInputStream.close();
        fileOutputStream.close();

    }

}
