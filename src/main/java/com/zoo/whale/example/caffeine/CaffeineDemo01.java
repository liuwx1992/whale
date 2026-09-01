package com.zoo.whale.example.caffeine;/*
package com.zoo.whale.example.caffeine;

import com.github.benmanes.caffeine.cache.*;
import com.sun.istack.internal.NotNull;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

*/
/**
 * @ClassName:CaffeineDemo01
 * @Description:
 * @Author:weixin.liu
 * @Date:2022/4/6
 * @Time:10:11
 * @Version；1.0
 *//*

@Slf4j
public class CaffeineDemo01 {


    private static LoadingCache<String, String> cache = Caffeine.newBuilder()
            //最大个数限制
            .maximumSize(256L)
            //初始化容量
            .initialCapacity(1)
            //访问后过期（包括读和写）
            .expireAfterAccess(2, TimeUnit.DAYS)
            //写后过期
            .expireAfterWrite(2, TimeUnit.DAYS)
            //写后自动异步刷新
            .refreshAfterWrite(1, TimeUnit.HOURS)
            //记录下缓存的一些统计数据，例如命中率等
            .recordStats()
            //cache对缓存写的通知回调
            .writer(new CacheWriter<Object, Object>() {
                @Override
                public void write(@NotNull Object key, @NotNull Object value) {
                    log.info("key={}, CacheWriter write", key);
                    //写入redis和mysql
                    log.info("写入Redis和Mysql");
                }

                @Override
                public void delete(@NotNull Object key, @Nullable Object value, @NotNull RemovalCause removalCause) {
                    log.info("key={}, cause={}, CacheWriter delete", key, removalCause);
                }

            })
            //使用CacheLoader创建一个LoadingCache
            .build(new CacheLoader<String, String>() {

                //同步加载数据
                @Nullable
                @Override
                public String load(@NotNull String key) throws Exception {
                    String value = "value_" + key;
                    log.info("get value{}", value);
                    return value;
                }

                @NotNull
                @Override
                public Map<String, String> loadAll(@NotNull Iterable<? extends String> keys) throws Exception {
                    return null;
                }

                @NotNull
                @Override
                public CompletableFuture<String> asyncLoad(@NotNull String key, @NotNull Executor executor) {
                    return null;
                }

                @NotNull
                @Override
                public CompletableFuture<Map<String, String>> asyncLoadAll(@NotNull Iterable<? extends String> keys, @NotNull Executor executor) {
                    return null;
                }

                //异步加载数据
                @Nullable
                @Override
                public String reload(@NotNull String key, @NotNull String oldValue) throws Exception {
                    return "value_" + key;
                }

                @NotNull
                @Override
                public CompletableFuture<String> asyncReload(@NotNull String key, @NotNull String oldValue, @NotNull Executor executor) {
                    return null;
                }
            });

    public static void main(String[] args) {
        //cache.put("a1","value1");
        System.out.println(cache.get("a1"));
        //cache.put("a1","value1");
        System.out.println(cache.getIfPresent("a1"));
    }
}
*/
