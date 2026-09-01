package com.zoo.whale.task;

import java.util.concurrent.*;

/**
 * @Date: 2026年 01月 09日
 * @Time:16时:24分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class ThreadPoolTest {

    static ExecutorService coreDataPool = new ThreadPoolExecutor(
            5,
            5,
            60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(3),
            new ThreadPoolExecutor.AbortPolicy());

    /*static ExecutorService coreDataPool = new ThreadPoolExecutor(
            5,
            5,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            new ThreadPoolExecutor.AbortPolicy());*/


    public static void main(String[] args) {

        for (int i = 0;i < 10;i++) {
            try {
                coreDataPool.execute(new ThreadWork());
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.println();
        }
    }



}
