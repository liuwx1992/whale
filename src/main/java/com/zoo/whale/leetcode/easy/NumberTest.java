package com.zoo.whale.leetcode.easy;

/**
 * @ClassName:NumberTest
 * @Description:
 * @Author:weixin.liu
 * @Date:2022/3/2
 * @Time:17:31
 * @Version；1.0
 */
public class NumberTest {
    public static void main(String[] args) {
        String num = "13055558888";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 20000; i++) {
            builder.append(num).append(",");
        }
        int length = builder.toString().length();
        System.out.println(builder.toString());
        System.out.println(length);
        System.out.println(1024 * 1024);
        System.out.println(length > 1024 * 1024);
    }
}
