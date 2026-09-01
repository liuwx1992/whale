package com.zoo.whale.google;

import com.google.common.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * @Date: 2024年 01月 04日
 * @Time:17时:31分
 * @Author: weixin.liu
 * @Version:V1.0
 */
@Slf4j
public class Test01 {

    /**
     * 测试任务执行失败回调
     */
    @Test
    public void addCallbackOfFailure() throws InterruptedException {
        ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

        ListenableFuture<Long> future = listeningExecutorService.submit(new MyCallable(-1));

        // 增加回调
        FutureCallback<Long> futureCallback = new FutureCallback<Long>() {

            /**
             * 成功时调用
             * @param result
             */
            @Override
            public void onSuccess(Long result) {
                try {
                    log.warn("任务执行成功，结果为：" + future.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            /**
             * 失败时调用
             * @param t
             */
            @Override
            public void onFailure(Throwable t) {
                log.error("任务执行失败，异常原因：" + t.getMessage());
            }

        };

        Futures.addCallback(future, futureCallback, listeningExecutorService);

        // 为了验证测试结果，阻塞主线程，等待监听器任务异步执行
        // 真实使用不需要阻塞主线程
        Thread.sleep(3000);
        listeningExecutorService.shutdown();
    }
}
