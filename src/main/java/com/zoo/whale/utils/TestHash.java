package com.zoo.whale.utils;


import cn.hutool.core.lang.hash.MurmurHash;

/**
 * @Date: 2026年 03月 09日
 * @Time:16时:33分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class TestHash {

    public static void main(String[] args) {
        String id = "110101199001011234";

        long hash = MurmurHash.hash64(id.getBytes());

        System.out.println(hash);
    }

}
