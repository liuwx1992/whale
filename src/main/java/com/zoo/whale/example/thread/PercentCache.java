package com.zoo.whale.example.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class PercentCache {

    private static final Logger logger = LoggerFactory.getLogger(PercentCache.class);

    private PercentCache() {
    }

    private static class CacheInstance {
        private static PercentCache instance = new PercentCache();
    }

    public static PercentCache getInstance() {
        return CacheInstance.instance;
    }

    private Map<Long, AtomicInteger> percentMap = new ConcurrentHashMap<Long, AtomicInteger>();

    public Map<Long, AtomicInteger> getMap() {
        logger.info("percentMap:{}", percentMap);
        return percentMap;
    }

    public void initPercentMap(Long taskId) {
        //logger.info("初始化taskId:{}", taskId);
        percentMap.put(taskId, new AtomicInteger(0));
    }

    public void incrementCount(Long taskId) {
        if (!percentMap.isEmpty()) {
            percentMap.get(taskId).incrementAndGet();
            //logger.info("taskId:{},count:{}", taskId, percentMap.get(taskId).get());
        }
    }

    public static void main(String[] args) {
        PercentCache instance1 = PercentCache.getInstance();
        PercentCache instance2 = PercentCache.getInstance();
        System.out.println(instance1);
        System.out.println(instance2);
        System.out.println(instance1.getMap().get(new Long(1)));
    }
}
