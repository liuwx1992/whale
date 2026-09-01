package com.zoo.whale.juc.Semaphore;

/**
 * @Date: 2024年 01月 24日
 * @Time:17时:31分
 * @Author: weixin.liu
 * @Version:V1.0
 */
import java.util.concurrent.Semaphore;

public class SemaphoreDemo {

    public static void main(String[] args) throws InterruptedException {
        // 创建一个 Semaphore，初始许可证数量为 2
        Semaphore semaphore = new Semaphore(0);

        // 创建 10 个线程
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    // 获取许可证
                    semaphore.acquire();

                    // 执行操作
                    System.out.println(Thread.currentThread().getName() + " 获取到许可证");
                    Thread.sleep(1000);

                    // 释放许可证
                    semaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }


}

