package com.zoo.whale.thread.interrupt;

/**
 * @Date: 2026年 02月 20日
 * @Time:22时:39分
 * @Author: weixin.liu
 * @Version:V1.0
 */
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

public class LockInterruptibleTest {

    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {

        // 主线程先获取锁
        lock.lock();
        System.out.println("【主线程】已获取锁，持有5秒...");

        // 创建子线程，尝试获取锁（可中断）
        Thread worker = new Thread(() -> {
            System.out.println("【子线程】开始尝试获取锁...");
            try {
                // 可中断的锁获取方式
                lock.lockInterruptibly();
                System.out.println("【子线程】成功获取锁！");
                lock.unlock();
            } catch (InterruptedException e) {
                System.out.println("【子线程】被中断！放弃获取锁");
                System.out.println("【子线程】中断状态：" + Thread.currentThread().isInterrupted());
            }
        }, "Worker-Thread");

        worker.start();

        // 等待1秒，确保子线程进入等待状态
        TimeUnit.SECONDS.sleep(1);

        // 中断子线程
        System.out.println("【主线程】中断子线程...");
        worker.interrupt();

        // 等待子线程结束
        worker.join();

        // 释放锁
        lock.unlock();
        System.out.println("【主线程】释放锁，测试结束");
    }
}
