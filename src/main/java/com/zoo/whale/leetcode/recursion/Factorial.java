package com.zoo.whale.leetcode.recursion;

/**
 * @Date: 2026年 02月 26日
 * @Time:11时:46分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class Factorial {

    public static void main(String[] args) {
        System.out.println(factorial(5));
    }

    public static int factorial(int n) {
        if (n <= 1) {
            return 1;
        }
        return factorial(n - 1) * n;
    }

}
