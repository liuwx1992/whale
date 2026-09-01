package com.zoo.whale.map;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Date: 2023年03月27日
 * @Time: 15时:05分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class TreeMapDemo {

    public static void main(String[] args) {
        Map<String, String> map = new TreeMap<>(Collections.reverseOrder());
        Map<String, String> map2 = new TreeMap<>();
        for (int i = 1;i < 50;i++){
            int num = i * 1000;
            String key = num + "#" + "20220327" + "#" + "391";
            map.put(key,"valve");
            map2.put(key,"valve");
        }

        for (Map.Entry<String,String> entry : map.entrySet()){
            System.out.println(entry.getKey());
        }
        System.out.println("-------------------------------------");
        for (Map.Entry<String,String> entry : map2.entrySet()){
            System.out.println(entry.getKey());
        }

        System.out.println("-------------------------------------");

        String key = map.entrySet().stream().findFirst().get().getKey();
        String key2 = map2.entrySet().stream().findFirst().get().getKey();

        System.out.println(key);
        System.out.println(key2);


    }

}
