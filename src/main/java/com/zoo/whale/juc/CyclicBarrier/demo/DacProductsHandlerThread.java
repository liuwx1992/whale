package com.zoo.whale.juc.CyclicBarrier.demo;

/**
 * @Date: 2026年 03月 12日
 * @Time:15时:14分
 * @Author: weixin.liu
 * @Version:V1.0
 */
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 模拟你的类：继承 Thread 实现 Runnable
 */
class DacProductsHandlerThread extends Thread {
    private final String name;

    public DacProductsHandlerThread(String name) {
        super("Independent-Thread-" + name); // 设置线程名
        this.name = name;
    }

    @Override
    public void run() {
        // 【关键点】：这里打印当前线程的名字，以此判断是谁在执行
        System.out.println(">>> [RUN 执行] 我是 [" + Thread.currentThread().getName() + "], 正在执行汇总逻辑 (name=" + name + ")");

        try {
            TimeUnit.SECONDS.sleep(10000); // 模拟耗时汇总
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("<<< [RUN 结束] 汇总逻辑完成");
    }
}
