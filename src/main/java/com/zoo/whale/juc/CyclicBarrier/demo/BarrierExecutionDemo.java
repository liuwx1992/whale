package com.zoo.whale.juc.CyclicBarrier.demo;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Date: 2026年 03月 12日
 * @Time:15时:14分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class BarrierExecutionDemo {

    public static void main(String[] args) throws InterruptedException {
        int parties = 3;
        ExecutorService executor = Executors.newFixedThreadPool(parties);

        // 1. 创建你的 "Thread" 对象
        DacProductsHandlerThread handler = new DacProductsHandlerThread("Handler-A");

        // 2. 【错误/特殊用法】：直接把它传给 CyclicBarrier
        // 注意：这里没有调用 handler.start()!
        CyclicBarrier barrier = new CyclicBarrier(parties, handler);

        System.out.println("=== 开始提交 " + parties + " 个工作线程 ===\n");

        for (int i = 0; i < parties; i++) {
            final int taskId = i;
            executor.submit(() -> {
                try {
                    System.out.println("[工作线程-" + taskId + "] (" + Thread.currentThread().getName() + ") 开始工作...");
                    TimeUnit.MILLISECONDS.sleep((long) (Math.random() * 1000)); // 随机耗时

                    System.out.println("[工作线程-" + taskId + "] (" + Thread.currentThread().getName() + ") 工作完成，到达屏障...");

                    // 关键时刻：调用 await
                    barrier.await();

                    System.out.println("[工作线程-" + taskId + "] (" + Thread.currentThread().getName() + ") 穿过屏障，继续后续工作。");

                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("\n=== 实验结束 ===");
        System.out.println("观察重点：执行 [RUN 执行] 的线程名字，是不是 'Independent-Thread-Handler-A'？");
        System.out.println("如果不是，说明它并没有作为独立线程运行，而是被某个‘工作线程’借用了！");
    }
}