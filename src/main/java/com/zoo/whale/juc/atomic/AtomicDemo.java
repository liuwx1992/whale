package com.zoo.whale.juc.atomic;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Date: 2024年 01月 19日
 * @Time:13时:48分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class AtomicDemo {

    // 共享变量
    private static int count = 0;

    // 操作共享变量的方法
    public static void incr(){
        // 为了演示效果  休眠一下子
        try {
            Thread.sleep(1);
            count ++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Lock lock = new ReentrantLock();

    // 操作共享变量的方法
    public static void incrWithLock(){
        // 为了演示效果  休眠一下子
        try {
            lock.lock();
            Thread.sleep(1);
            count ++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 1000 ; i++) {
            new Thread(()->AtomicDemo.incrWithLock()).start();
        }
        //Thread.sleep(4000);

        ThreadLocal threadLoacal = new ThreadLocal();
        System.out.println("result:" + count);
    }

}
