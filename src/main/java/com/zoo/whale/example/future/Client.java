package com.zoo.whale.example.future;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Date: 2022年09月22日
 * @Time: 16时:16分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class Client {

    public static void main(String[] args) throws InterruptedException {
        // 单次计数器
        CountDownLatch countDownLatch = new CountDownLatch(1000);
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 0; i <= 1000;i++){
            executorService.submit(new MainApp(i+"","{}",countDownLatch));
        }
        countDownLatch.await();
        long end = System.currentTimeMillis();
        System.out.println("结束时间==="+end);

    }

}
