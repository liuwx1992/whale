package com.zoo.whale.redis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Date: 2023年 12月 22日
 * @Time:15时:04分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class IncrDemoTest {

    private static final  ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws Exception {

        Thread thread = new Thread(new IncrDemo());
        Thread thread01 = new Thread(new IncrDemo01());
        Thread thread02 = new Thread(new IncrDemo02());
        Thread thread03 = new Thread(new IncrDemo03());
        Thread thread04 = new Thread(new IncrDemo04());
        Thread thread05 = new Thread(new IncrDemo05());
        Thread thread06 = new Thread(new IncrDemo05());
        Thread thread07 = new Thread(new IncrDemo07());

        executorService.submit(thread);
        executorService.submit(thread01);
        executorService.submit(thread02);
        executorService.submit(thread03);
        executorService.submit(thread04);
        executorService.submit(thread05);
        executorService.submit(thread06);
        executorService.submit(thread07);
    }
}
