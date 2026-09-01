package com.zoo.whale.thread.interrupt;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Date: 2026年 02月 20日
 * @Time:20时:45分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class InterruptedDemo {

    public static void main(String[] args) throws InterruptedException {
        ThreadTaskLock lock = new ThreadTaskLock();
        ThreadTaskSync lock2 = new ThreadTaskSync();
        Thread t1 = new Thread(lock);
        Thread t2 = new Thread(lock2);

        t1.start();
        t2.start();

        t1.interrupt();
        t2.interrupt();
    }

    private static void lockDemo() throws InterruptedException {
        Lock lock = new ReentrantLock();
        lock.lockInterruptibly();  // 等待时可响应中断
        try {
            // 业务逻辑
            Thread.sleep(5000);
        } finally {
            lock.unlock();
        }
    }

    // synchronized 等待时无法中断
    public synchronized void method() throws InterruptedException {
        // 线程阻塞时，interrupt()无法唤醒
        this.wait();
    }


}
