package com.zoo.whale.example.map;

import org.springframework.web.cors.reactive.PreFlightRequestWebFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Date: 2022年11月02日
 * @Time: 18时:10分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class MapDemo01 {

    private static final Map<String, Map<String,String>> creationLocks = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        //String key = "key";
        creationLocks.computeIfAbsent("key", key -> new HashMap<>());
        creationLocks.computeIfAbsent("key", key->getValue(key));
        creationLocks.computeIfAbsent("key", key->getValue(key));
        System.out.println(creationLocks);
    }

    private static Map<String,String> getValue(String key){
        Map<String,String> map = new HashMap<>();
        map.put("key","value");
        return map;
    }
}
