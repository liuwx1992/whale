package com.zoo.whale.leetcode;

/**
 * @Date: 2024年 12月 03日
 * @Time:17时:41分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class IsPowerOfTwo {

    public static void main(String[] args) {
        boolean powerOfTwo = isPowerOfTwo(6);
        System.out.println(powerOfTwo);
    }
    public static boolean isPowerOfTwo(int n) {
        if(1 == n){
            return true;
        }
        int i = 1;
        return isPower(i, n);
    }

    private static boolean isPower(int i, int n) {
        if(Math.pow(2,i) == n){
            return true;
        } else if (Math.pow(2,i) > n) {
            return false;
        } else {
            return isPower(i+1,n);
        }
    }
}
