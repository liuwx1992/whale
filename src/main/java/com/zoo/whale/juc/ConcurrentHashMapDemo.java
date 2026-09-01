package com.zoo.whale.juc;


import java.util.concurrent.ConcurrentHashMap;

/**
 * @Date: 2026年 02月 23日
 * @Time:00时:22分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class ConcurrentHashMapDemo {

    public static void main(String[] args) {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap();
        map.put("1", "1");
        System.out.println(map.size());
    }


}
