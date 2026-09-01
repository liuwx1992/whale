package com.zoo.whale.thread;

import com.zoo.whale.utils.RedisClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @Date: 2025年 06月 18日
 * @Time:20时:42分
 * @Author: weixin.liu
 * @Version:V1.0
 */
@Slf4j
public class RedisOperatorThread implements Runnable {


    private String mealNameAndVersion;

    public RedisOperatorThread(String mealNameAndVersion) {
        this.mealNameAndVersion = mealNameAndVersion;
    }

    @Override
    public void run() {
        String redisKey = "hx_global_limit:" + mealNameAndVersion;
        //异步redis计数(偶发的计数失败，可以忽略，不百分百严格要求)
        boolean incrRet = RedisClient.incrRedisKey(redisKey);
        if (!incrRet) {
            log.warn("incrRet执行结果返回false");
        }
    }
}
