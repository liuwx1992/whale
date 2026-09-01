package com.zoo.whale.juc.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Date: 2024年 01月 23日
 * @Time:10时:53分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class LockDemo {
    public static void main(String[] args) throws InterruptedException {

        Lock lock = new ReentrantLock(false);

        Thread thread01 = new Thread(new LockRunnable(lock));
        thread01.start();

        Thread.sleep(10);

        Thread thread02 = new Thread(new LockRunnable(lock));
        thread02.start();

        Thread.sleep(10);

        Thread thread03 = new Thread(new LockRunnable(lock));
        thread03.start();

        Thread.sleep(10);

        Thread thread04 = new Thread(new LockRunnable(lock));
        thread04.start();

        Thread.sleep(10);

        Thread thread05 = new Thread(new LockRunnable(lock));
        thread05.start();

        //System.out.println("do start");
    }
}
