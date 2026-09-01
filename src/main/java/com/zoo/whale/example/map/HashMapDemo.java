package com.zoo.whale.example.map;

import java.util.HashMap;

/**
 * @Date: 2026年 03月 22日
 * @Time:23时:37分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class HashMapDemo {

    public static void main(String[] args) {
        HashMap<String, String> map = new HashMap();
        System.out.println("start");
        map.put("a", "b");
        map.put("a", "bc");
        System.out.println(map.get("a"));
    }


}
