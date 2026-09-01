package com.zoo.whale.example.list;

import java.util.*;

/**
 * @Date: 2022年12月28日
 * @Time: 14时:57分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class SortListDemo {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();

        Set<String> set = new TreeSet<>();
        list.add("D");                
        list.add("B");
        list.add("C");
        list.add("E");
        list.add("A");

        set.add("D");
        set.add("B");
        set.add("C");
        set.add("E");
        set.add("A");

        String s = list.stream().max(Comparator.comparing(x -> x)).get();
        System.out.println(s);
        System.out.println(set.stream().max(Comparator.comparing(x -> x)).get());
        System.out.println();

    }

}
