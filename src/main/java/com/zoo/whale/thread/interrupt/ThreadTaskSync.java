package com.zoo.whale.thread.interrupt;

/**
 * @Date: 2026年 02月 20日
 * @Time:21时:05分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class ThreadTaskSync implements Runnable{
    @Override
    public void run() {
        try {
            method();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // synchronized 等待时无法中断
    public synchronized void method() throws InterruptedException {
        // 线程阻塞时，interrupt()无法唤醒
        this.wait();
    }
}
