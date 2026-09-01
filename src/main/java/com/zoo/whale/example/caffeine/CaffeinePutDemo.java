package com.zoo.whale.example.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Weigher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Date: 2023年01月10日
 * @Time: 17时:12分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class CaffeinePutDemo {
    public static void main(String[] args) {
        LoadingCache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(key -> createExpensiveGraph(key));

        cache.put("key","value");

        // 查找缓存，如果缓存不存在则生成缓存元素,  如果无法生成则返回null
        String graph = cache.get("key");

        System.out.println(graph);

        List<String> keys = new ArrayList<>();
        keys.add("key1");
        keys.add("key2");
        keys.add("key3");
        keys.add("key4");
        // 批量查找缓存，如果缓存不存在则生成缓存元素
        Map<String, String> graphs = cache.getAll(keys);
        for (Map.Entry entry : graphs.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }



    }

    private static String createExpensiveGraph(String key) {
        return key + "defaultValue";
    }

    private static void demo01(){
        LoadingCache<String, String> cache = Caffeine.newBuilder()
                .maximumWeight(10000)
                .recordStats()
                .weigher((Weigher<String, String>) (key, value) -> {
                    //根据key或者value等数据自定义权重规则
                    if (key.hashCode() % 2 == 0) {
                        return 2;
                    }
                    return 1;
                }).build(key -> createExpensiveGraph(key));

        for (int i = 0; i < 300; i++) {
            cache.get(i + "");
            if (i > 200) {
                System.out.println(cache.stats().toString());
            }
        }
    }

}
