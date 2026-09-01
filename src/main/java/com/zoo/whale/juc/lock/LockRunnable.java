package com.zoo.whale.juc.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Date: 2024年 01月 23日
 * @Time:10时:54分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class LockRunnable implements Runnable {

    private Lock lock;

    public LockRunnable(Lock lock) {
        this.lock = lock;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            lock.lock();
            System.out.println(Thread.currentThread().getName() + "get lock");
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + "over...");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
