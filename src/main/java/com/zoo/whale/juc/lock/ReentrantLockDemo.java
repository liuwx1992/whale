package com.zoo.whale.juc.lock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @Date: 2024年 01月 24日
 * @Time:15时:55分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class ReentrantLockDemo {
    public static void main(String[] args) {
        // 创建一个ReentrantLock对象
        ReentrantLock lock = new ReentrantLock();
        // 创建一个线程
        Thread thread1 = new Thread(() -> {
            // 获取锁
            lock.lock();
            // 执行操作
            System.out.println("线程1开始执行");
            // 再次获取锁
            lock.lock();
            // 执行操作
            System.out.println("线程1再次获取锁");
            // 延迟1秒
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 释放锁
            lock.unlock();
            // 释放锁
            lock.unlock();
        });
        // 启动线程
        thread1.start();
        // 获取锁
        lock.lock();
        // 执行操作
        System.out.println("主线程获取锁");
        // 释放锁
        lock.unlock();
    }
}

