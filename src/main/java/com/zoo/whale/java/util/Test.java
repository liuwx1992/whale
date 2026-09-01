package com.zoo.whale.java.util;

import java.util.TreeMap;

/**
 * @Date: 2025年 06月 10日
 * @Time:15时:23分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class Test {

    public static void main(String[] args) {
        TreeMap<Data,Data> treeMap = new TreeMap<>();

        Data data = new Data();

        treeMap.put(data,data);

        System.out.println(treeMap.size());

    }

}
