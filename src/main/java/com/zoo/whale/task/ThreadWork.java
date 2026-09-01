package com.zoo.whale.task;

/**
 * @Date: 2026年 01月 09日
 * @Time:16时:31分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class ThreadWork implements Runnable{
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + "执行了");
        try {
            Thread.sleep(5000000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
