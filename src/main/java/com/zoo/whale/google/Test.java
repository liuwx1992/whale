package com.zoo.whale.google;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * @Date: 2024年 01月 04日
 * @Time:17时:27分
 * @Author: weixin.liu
 * @Version:V1.0
 */
@Slf4j
public class Test {

    /**
     * 增加监听器
     */
    @org.junit.Test
    public void testAddListener() throws InterruptedException {

        ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

        ListenableFuture<Long> future = listeningExecutorService.submit(new MyCallable(1000));

        // 增加监听器
        future.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    log.warn("任务执行结束，结果为：" + future.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }, listeningExecutorService);

        // 为了验证测试结果，阻塞主线程，等待监听器任务异步执行
        // 真实使用不需要阻塞主线程
        Thread.sleep(3000);
        listeningExecutorService.shutdown();
    }


}
