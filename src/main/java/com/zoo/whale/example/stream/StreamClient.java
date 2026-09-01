package com.zoo.whale.example.stream;

import com.github.benmanes.caffeine.cache.CacheLoader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @Date: 2022年08月02日
 * @Time: 15时:55分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class StreamClient {
    public static void main(String[] args) {
        List<Integer> t56NBankOrgNumList = new ArrayList<>();
        for (int i = 0;i < 100;i++){
            t56NBankOrgNumList.add(i);
        }
        //Optional<Integer> max = t56NBankOrgNumList.stream().max(Comparator.comparing(Function.identity()));
        //Optional<Integer> max = t56NBankOrgNumList.stream().max(Comparator.comparingInt(Integer::valueOf));
        //Optional<Integer> max = t56NBankOrgNumList.stream().max(Comparator.comparingInt(Integer :: valueOf));
        Optional<Integer> max = t56NBankOrgNumList.stream().max(Comparator.comparingInt(t -> t));
        Integer d56MaxValue = max.get();
        System.out.println("max value:"+d56MaxValue);

        for (Integer i : t56NBankOrgNumList){
            System.out.println(i);
        }
    }
}
