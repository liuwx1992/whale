package com.zoo.whale.juc;

/**
 * @version 1.0
 * @Class StringDemo
 * @Author weixin.liu
 * @Date 2026/6/23 11:13
 */
public class StringDemo {

    public static void main(String[] args) {
        String processorName = "mealName";
        String outVersion = "outVersion";
        String mealName = processorName;
        processorName = processorName + "_" + outVersion;
        System.out.println(mealName);
        System.out.println(processorName);
    }

}
