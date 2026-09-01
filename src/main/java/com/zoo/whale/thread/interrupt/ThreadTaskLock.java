package com.zoo.whale.thread.interrupt;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Date: 2026年 02月 20日
 * @Time:21时:04分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class ThreadTaskLock implements Runnable {
    @Override
    public void run() {
        try {
            Lock lock = new ReentrantLock();
            lock.lockInterruptibly();  // 等待时可响应中断
            try {
                // 业务逻辑
                Thread.sleep(5000);
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
