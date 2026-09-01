package com.zoo.whale.juc.CountDownLatch;

import java.util.concurrent.CountDownLatch;

/**
 * @Date: 2024年 01月 25日
 * @Time:11时:06分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class CountDownLatchDemo {
    public static void main(String[] args) {
        // 创建一个 CountDownLatch，初始计数器值为 3
        CountDownLatch latch = new CountDownLatch(3);
        // 创建 3 个子线程
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                // 模拟执行任务
                System.out.println(Thread.currentThread().getName() + " 执行任务");
                try {
                    //模拟执行时间
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // 计数器减 1
                latch.countDown();
            }).start();
        }
        // 主线程等待所有子线程执行完毕
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 所有子线程都执行完毕，执行主线程任务
        System.out.println("主线程继续执行");
    }
}
