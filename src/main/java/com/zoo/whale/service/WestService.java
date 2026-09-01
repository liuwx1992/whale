package com.zoo.whale.service;

import com.brgroup.redis.client.BrRedisClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoo.whale.redis.RedisClient;
import com.zoo.whale.utils.FlowRuleManagerUtils;
import com.zoo.whale.utils.ice.JsonProcessUtil;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Date: 2025年 11月 17日
 * @Time:13时:48分
 * @Author: weixin.liu
 * @Version:V1.0
 */
@Service
public class WestService {


    public String doLimit(String apiCode, String jsonData) throws JsonProcessingException {

        ObjectMapper mapper = JsonProcessUtil.getMapperInstance();

        JsonNode jsonNode = mapper.readTree(jsonData);

        String mealStr = jsonNode.get("mealStr").asText();

        boolean canPass = FlowRuleManagerUtils.isCanPass(mealStr);

        if (canPass) {
            return "success";
        } else {
            return "false";
        }
    }

    public void doTest() throws InterruptedException {

        String key = "test:incr";

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
                    /*if (i % 50 == 0) { // 每 50 次打印一次
                        System.out.println(Thread.currentThread().getName() + " current=======" + current + "时间:" + System.currentTimeMillis());
                    }*/

                    System.out.println(Thread.currentThread().getName() + " current=======" + current + "时间:" + System.currentTimeMillis());

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
