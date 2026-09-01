package com.zoo.whale.thread;

/**
 * @Date: 2023年 12月 29日
 * @Time:10时:53分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class TestThreadLocal {

    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void main(String[] args) {
        threadLocal.set("today");

        threadLocal.set("future");
    }

}
