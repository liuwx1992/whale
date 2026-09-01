package com.zoo.whale.leetcode.datastructure;

/**
 * @Date: 2022年09月21日
 * @Time: 20时:32分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class SubtractProductAndSum {

    public static void main(String[] args) {

    }

    public static int subtractProductAndSum(int n) {
        int sum = 0;
        int mul = 1;
        while (n > 0){
            int digit = n % 10;
            n = n/10;
            sum += digit;
            mul *= digit;
        }
        return mul - sum;
    }

}
