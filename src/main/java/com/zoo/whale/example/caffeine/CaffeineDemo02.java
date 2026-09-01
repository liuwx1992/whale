package com.zoo.whale.example.caffeine;

import com.github.benmanes.caffeine.cache.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName:CaffeineDemo02
 * @Description:
 * @Author:weixin.liu
 * @Date:2022/4/6
 * @Time:16:04
 * @Version；1.0
 */
@Slf4j
public class CaffeineDemo02 {
    static Cache<String, String> cache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    static LoadingCache<String, String> cache02 = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(key -> createExpensiveGraph(key));

    static AsyncCache<String, String> cache03 = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .buildAsync();

    static AsyncLoadingCache<String, String> cache04 = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            // 你可以选择: 去异步的封装一段同步操作来生成缓存元素
            .buildAsync(key -> createExpensiveGraph(key));
    // 你也可以选择: 构建一个异步缓存元素操作并返回一个future
    //.buildAsync((key, executor) -> createExpensiveGraphAsync(key, executor));

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println(getValue04("a"));
    }

    public static String getValue01(String key) {
        //cache.put(key, "graph");
        // 查找一个缓存元素， 没有查找到的时候返回null
        String graph = cache.getIfPresent(key);
        System.out.println(graph);
        // 查找缓存，如果缓存不存在则生成缓存元素,  如果无法生成则返回null
        graph = cache.get(key, k -> createExpensiveGraph(key));
        System.out.println(graph);
        // 添加或者更新一个缓存元素
        cache.put(key, graph);

        // 移除一个缓存元素
        cache.invalidate(key);
        return graph;
    }

    public static String getValue02(String key) {
        // 查找缓存，如果缓存不存在则生成缓存元素,  如果无法生成则返回null
        String graph = cache02.get(key);
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        cache02.put("a", "value");
        // 批量查找缓存，如果缓存不存在则生成缓存元素
        Map<String, String> graphs = cache02.getAll(list);
        for (Map.Entry<String, String> entry : graphs.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
        return graph;
    }

    public static String getValue03(String key) throws ExecutionException, InterruptedException {
        // 查找一个缓存元素， 没有查找到的时候返回null
        CompletableFuture<String> graph = cache03.getIfPresent(key);
        //System.out.println(graph.get());
        // 查找缓存元素，如果不存在，则异步生成
        graph = cache03.get(key, k -> createExpensiveGraph(key));
        System.out.println(graph.get());

        // 添加或者更新一个缓存元素
        cache03.put(key, graph);
        // 移除一个缓存元素
        cache03.synchronous().invalidate(key);
        return graph.get();
    }

    public static String getValue04(String key) throws ExecutionException, InterruptedException {
        // 查找缓存元素，如果其不存在，将会异步进行生成
        CompletableFuture<String> graph = cache04.get(key);
        System.out.println(graph.get());
        List<String> keys = new ArrayList<>();
        keys.add("a");
        keys.add("b");
        keys.add("c");
        // 批量查找缓存元素，如果其不存在，将会异步进行生成
        CompletableFuture<Map<String, String>> graphs = cache04.getAll(keys);
        Map<String, String> map = graphs.get();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
        return graph.get();
    }

    /**
     *
     */
    private static String createExpensiveGraph(String key) {
        return "defaultValue";
    }

    private static String createExpensiveGraphAsync(String key, Executor executor) {
        return "defaultValue";
    }
}
