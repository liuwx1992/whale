package com.zoo.whale.thread.interrupt;

import java.util.concurrent.TimeUnit;

/**
 * @Date: 2026年 02月 20日
 * @Time:22时:44分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class SynchronizedNotInterruptibleTest {

    private static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {

        // 主线程先获取锁
        synchronized (lock) {
            System.out.println("【主线程】已获取锁，持有10秒...");

            // 创建子线程，尝试获取锁
            Thread worker = new Thread(() -> {
                System.out.println("【子线程】开始尝试获取锁...");
                synchronized (lock) {
                    System.out.println("【子线程】成功获取锁！");
                }
            }, "Worker-Thread");

            worker.start();

            // 等待1秒，确保子线程进入等待状态
            TimeUnit.SECONDS.sleep(1);

            // 尝试中断子线程
            System.out.println("【主线程】尝试中断子线程...");
            worker.interrupt();

            // 等待2秒观察
            TimeUnit.SECONDS.sleep(2);

            System.out.println("【主线程】子线程是否存活：" + worker.isAlive());
            System.out.println("【主线程】子线程中断状态：" + worker.isInterrupted());

            // 主线程释放锁后，子线程才能继续
        }

        System.out.println("【主线程】释放锁，测试结束");
    }
}
