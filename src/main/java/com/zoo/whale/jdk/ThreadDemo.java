package com.zoo.whale.jdk;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Date: 2023年 11月 01日
 * @Time:15时:54分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class ThreadDemo {


    //核心线程数为5，最大线程数为10
    public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1,20,
            5, TimeUnit.SECONDS,new ArrayBlockingQueue<>(1));


    public static void main(String[] args) throws InterruptedException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("name = " + Thread.currentThread().getName());
            }
        };

        threadPoolExecutor.execute(runnable);

        Thread.sleep(20000);

        threadPoolExecutor.execute(runnable);

        /*threadPoolExecutor.execute(runnable);

        Thread.sleep(120000);
        threadPoolExecutor.execute(runnable);

        while (true) {
            Thread.sleep(10000);

            System.out.println("核心线程数 = " + threadPoolExecutor.getCorePoolSize());
            System.out.println("当前线程池线程数 = "+threadPoolExecutor.getPoolSize());
            System.out.println("maxPoolNum = " + threadPoolExecutor.getMaximumPoolSize());
            System.out.println("当前队列中的任务数 queue size = " + threadPoolExecutor.getQueue().size());
        }*/

    }

}
