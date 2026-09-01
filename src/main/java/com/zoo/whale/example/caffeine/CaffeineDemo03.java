package com.zoo.whale.example.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;

/**
 * @ClassName:CaffeineDemo03
 * @Description:
 * @Author:weixin.liu
 * @Date:2022/4/15
 * @Time:16:15
 * @Version；1.0
 */
public class CaffeineDemo03 {

    public static void main(String[] args) {
        writerTest();
    }

    public static void writerTest() {
        Cache<String, String> cacheOne = Caffeine.newBuilder()
                .maximumSize(1)
                .build();
        cacheOne.put("k1", "v1");
        System.out.printf("key: %s , value: %s", "k1", cacheOne.getIfPresent("k1"));
        cacheOne.put("k2", "v2");
        System.out.printf("key: %s , value: %s", "k1", cacheOne.getAllPresent(Lists.newArrayList("k1", "k2")));
    }

}
