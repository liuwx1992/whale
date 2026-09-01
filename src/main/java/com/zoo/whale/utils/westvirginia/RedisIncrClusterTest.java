package com.zoo.whale.utils.westvirginia;

import com.brgroup.redis.client.BrRedisClient;
import com.zoo.whale.redis.RedisClient;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Date: 2025年 11月 17日
 * @Time:14时:37分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class RedisIncrClusterTest {

    static String key = "test:incr";

    @Test
    public void test() throws Exception {

        // --- 1. 初始化 Redis Cluster ---
        BrRedisClient cluster = RedisClient.getRedisCluster1Limier();

        // 清空 key
        cluster.del(key);

        // --- 2. 启动压测线程 ---
        int threads = 50;
        int loops = 2000;

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            pool.submit(() -> {
                for (int i = 0; i < loops; i++) {

                    // 使用 incr 模拟 qps
                    Long current = cluster.incr(key);

                    // 打印 current，观察跳增
                    if (i % 50 == 0) { // 每 50 次打印一次
                        System.out.println(Thread.currentThread().getName() + " current=======" + current);
                    }

                }
                latch.countDown();
            });
        }



        latch.await();
        pool.shutdown();

        cluster.del(key);

        System.out.println("---- DONE ----");
    }

}
