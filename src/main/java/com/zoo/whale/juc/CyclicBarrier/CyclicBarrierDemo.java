package com.zoo.whale.juc.CyclicBarrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


/**
 * @Date: 2024年 01月 18日
 * @Time:14时:11分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class CyclicBarrierDemo {
    public static void main(String[] args) {
        // 创建一个 CyclicBarrier，初始计数器值为 3
        CyclicBarrier barrier = new CyclicBarrier(3);

        // 创建 3 个子线程
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                // 模拟执行任务
                System.out.println(Thread.currentThread().getName() + " 执行任务");

                try {
                    // 到达栅栏
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }

                // 所有线程都到达栅栏，继续执行
                System.out.println("所有线程都到达栅栏，继续执行");
            }).start();
        }
    }
}
