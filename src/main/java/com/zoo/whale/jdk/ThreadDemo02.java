package com.zoo.whale.jdk;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Date: 2023年 11月 01日
 * @Time:15时:54分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class ThreadDemo02 {

    public static void main(String[] args) throws InterruptedException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("name = " + Thread.currentThread().getName());
            }
        };

        ThreadDemo.threadPoolExecutor.execute(runnable);

    }

}
