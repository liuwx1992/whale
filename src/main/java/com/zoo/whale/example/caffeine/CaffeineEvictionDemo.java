package com.zoo.whale.example.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

/**
 * @ClassName:CaffeineEvictionDemo
 * @Description:Caffeine 提供了三种驱逐策略，分别是基于容量，基于时间和基于引用三种类型。
 * https://github.com/ben-manes/caffeine/wiki/Eviction-zh-CN
 * @Author:weixin.liu
 * @Date:2022/4/6
 * @Time:19:19
 * @Version；1.0
 */
public class CaffeineEvictionDemo {
    // 基于缓存内的元素个数进行驱逐
    static LoadingCache<String, String> cache01 = Caffeine.newBuilder()
            .maximumSize(10)
            .build(key -> createExpensiveGraph(key));

    // 基于缓存内元素权重进行驱逐
    /*static LoadingCache<String, String> graphs02 = Caffeine.newBuilder()
            .maximumWeight(10_000)
            .weigher((String key, String graph) -> graph.size())
            .build(key -> createExpensiveGraph(key));
*/

    private static String createExpensiveGraph(String key) {
        return "defaultValue";
    }

    public static void main(String[] args) {
        eviction01();
    }


    public static void eviction01() {
        /*cache01.put("a","value01");
        cache01.put("b","value02");
        System.out.println(cache01.getIfPresent("a"));
        cache01.put("c","value03");
        cache01.put("d","value04");*/
        for (int i = 0; i < 100; i++) {
            cache01.put(i + "", "value" + i);
        }
        for (int i = 0; i < 100; i++) {
            System.out.println(cache01.getIfPresent("" + i));
        }
        /*System.out.println(cache01.getIfPresent("a"));
        System.out.println(cache01.getIfPresent("b"));
        System.out.println(cache01.getIfPresent("c"));
        System.out.println(cache01.getIfPresent("d"));
        long l = cache01.estimatedSize();
        int size = cache01.asMap().size();
        System.out.println(l);
        System.out.println(size);*/
    }

    /*@Override
    public @Nullable V getIfPresent(Object key, boolean recordStats) {
        //使用node工厂，包装key为node节点，进行map get
        Node<K, V> node = data.get(nodeFactory.newLookupKey(key));
        if (node == null) {
            if (recordStats) {
                //未命中次数统计
                statsCounter().recordMisses(1);
            }
            return null;
        }

        V value = node.getValue();
        //获取一个和机器启动相关的时间戳
        long now = expirationTicker().read();
        //以now进行是否过期判断
        if (hasExpired(node, now) || (collectValues() && (value == null))) {
            if (recordStats) {
                statsCounter().recordMisses(1);
            }
            //缓存整理
            scheduleDrainBuffers();
            return null;
        }

        if (!isComputingAsync(node)) {
            @SuppressWarnings("unchecked")
            K castedKey = (K) key;
            setAccessTime(node, now);
            //访问时间更新
            tryExpireAfterRead(node, castedKey, value, expiry(), now);
        }
        //读后处理
        afterRead(node, now, recordStats);
        return value;
    }*/

}
