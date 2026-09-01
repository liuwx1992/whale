package com.zoo.whale.example.juc;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName:MapDemo
 * @Description:
 * @Author:weixin.liu
 * @Date:2022/4/29
 * @Time:17:30
 * @Version；1.0
 */
public class MapDemo {

    public static void main(String[] args) {
        demo01();
    }

    public static void demo01() {
        Map<String, String> map = new ConcurrentHashMap<>(16);

        map.put("a", "40");

        map.computeIfAbsent("a", key -> "45");

        System.out.println(map.size());
        System.out.println(map.get("a"));
    }

}
