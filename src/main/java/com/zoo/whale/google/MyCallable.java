package com.zoo.whale.google;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * @Date: 2024年 01月 04日
 * @Time:16时:47分
 * @Author: weixin.liu
 * @Version:V1.0
 */
@Slf4j
@RequiredArgsConstructor
public class MyCallable implements Callable<Long> {
    /**
     * 任务执行时间
     */
    private final long execTime;

    @Override
    public Long call() throws Exception {
        if (execTime <= 0) {
            throw new RuntimeException("执行时间必须大于0");
        }
        log.warn("任务执行，耗时：" + execTime + " ms");
        Thread.sleep(execTime);
        return execTime;
    }



}
