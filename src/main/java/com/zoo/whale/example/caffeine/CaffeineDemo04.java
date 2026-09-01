package com.zoo.whale.example.caffeine;

import com.github.benmanes.caffeine.cache.*;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Date: 2023年01月28日
 * @Time: 10时:10分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class CaffeineDemo04 {

    public static void main(String[] args) throws InterruptedException {
        weightDemo();
    }



    private static void weightDemo() throws InterruptedException {
        LoadingCache<Integer,String> cache = Caffeine.newBuilder()
                .maximumWeight(300)
                .recordStats()
                .weigher((Weigher<Integer, String>) (key, value) -> {
                    /*if(key % 2 == 0){
                        return 2;
                    }
                    return 1;*/
                    return 3;
                })
                .removalListener(new RemovalListener<Integer, String>() {
                    @Override
                    public void onRemoval(@Nullable Integer key, @Nullable String value, @NonNull RemovalCause cause) {
                        //System.out.println("remove key:" + key + ", " + "value:" + value + ", " + "cause:" + cause);
                    }
                })
                .build( k -> UUID.randomUUID().toString());

        for (int i = 0; i < 300; i++) {
            cache.get(i);
            /*if(i> 200){
                System.out.println(cache.stats().toString());
            }*/
            Thread.sleep(10);
            //System.out.println(cache.estimatedSize());
            printStats(cache.stats());
        }
    }

    private static void printStats(CacheStats stats){
        System.out.println("---------------------");
        System.out.println("stats.hitCount():"+stats.hitCount());//命中次数
        System.out.println("stats.hitRate():"+stats.hitRate());//缓存命中率
        System.out.println("stats.missCount():"+stats.missCount());//未命中次数
        System.out.println("stats.missRate():"+stats.missRate());//未命中率
        System.out.println("stats.loadSuccessCount():"+stats.loadSuccessCount());//加载成功的次数
        System.out.println("stats.loadFailureCount():"+stats.loadFailureCount());//加载失败的次数,返回null
        System.out.println("stats.loadFailureRate():"+stats.loadFailureRate());//加载失败的百分比
        System.out.println("stats.totalLoadTime():"+stats.totalLoadTime());//总加载时间,单位ns
        System.out.println("stats.evictionCount():"+stats.evictionCount());//驱逐次数
        System.out.println("stats.evictionWeight():"+stats.evictionWeight());//驱逐的weight值总和
        System.out.println("stats.requestCount():"+stats.requestCount());//请求次数
        System.out.println("stats.averageLoadPenalty():"+stats.averageLoadPenalty());//单次load平均耗时
    }

    private static void maxNumDemo() throws InterruptedException {
        LoadingCache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .removalListener((RemovalListener<String, String>)
                        (key, value, cause) -> System.out.println("remove key:" + key + ", " + "value:" + value + ", " + "cause:" + cause)
                )
                .build(key -> createExpensiveGraph(key));

        cache.put("a","01");
        cache.put("b","02");
        cache.put("c","03");
        cache.put("d","04");
        cache.put("e","05");
        cache.put("f","06");
        cache.put("g","07");
        cache.put("h","08");

        cache.invalidate("g");


        Thread.sleep(2000);

        System.out.println(cache.estimatedSize());

        System.out.println(cache.getIfPresent("a"));
        System.out.println(cache.getIfPresent("b"));
        System.out.println(cache.getIfPresent("c"));
        System.out.println(cache.getIfPresent("d"));
        System.out.println(cache.getIfPresent("e"));
        System.out.println(cache.getIfPresent("f"));
        System.out.println(cache.getIfPresent("g"));
        System.out.println(cache.getIfPresent("h"));
        System.out.println(cache.getIfPresent("j"));
    }


    private static String createExpensiveGraph(String key) {
        return key + "defaultValue";
    }

}
