package com.zoo.whale.juc.condition;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Date: 2024年 01月 29日
 * @Time:10时:49分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class Test {

    static ExecutorService executorService = Executors.newSingleThreadExecutor();

    static ExecutorService executorService2 = Executors.newSingleThreadExecutor();

    public static void main(String[] args) throws InterruptedException {

        ConditionTest conditionTest = new ConditionTest(2);

        Thread thread01 = new Thread(new SetRunnable(conditionTest));
        Thread thread02 = new Thread(new GetRunnable(conditionTest));
//        Thread thread03 = new Thread(new Set2Runnable(conditionTest));

        executorService.submit(thread01);
        executorService2.submit(thread02);
//        executorService2.submit(thread03);

        System.out.println("start...");
    }

}
