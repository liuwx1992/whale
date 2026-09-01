package com.zoo.whale.java.util;

import lombok.Getter;

/**
 * @Date: 2025年 06月 10日
 * @Time:15时:23分
 * @Author: weixin.liu
 * @Version:V1.0
 */
@Getter
public class Data implements Comparable<Data>{

    private int key;



    @Override
    public int compareTo(Data o) {
        return o.getKey();
    }
}
