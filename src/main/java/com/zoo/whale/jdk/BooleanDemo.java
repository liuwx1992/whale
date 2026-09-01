package com.zoo.whale.jdk;

/**
 * @Date: 2023年 12月 07日
 * @Time:15时:45分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class BooleanDemo {

    public static void main(String[] args) {
        if(!get()){
            System.out.println("GET...");
        }
        long a = 1682331087148L - 1682331087111L;
        System.out.println(a);
        retry:
        for (int i = 0;i < 5;i++) {
            for (int j = 0;j < 5;j++) {
                System.out.println(i + "_" + j);
                break retry;
            }
        }
    }

    private static boolean get(){
        return true;
    }

}
