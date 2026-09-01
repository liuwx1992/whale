package com.zoo.whale.leetcode.easy;

/**
 * @ClassName:LehgthOfLongestSubstringSolution
 * @Description:重复子串问题
 * @Author:weixin.liu
 * @Date:2021/10/8
 * @Time:19:51
 * @Version；1.0
 */
public class LengthOfLongestSubstringSolution {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            int ret = fibonacci(i);
            long ret2 = fibonacci2(i);
            System.out.printf("Fibonacci of %d is: %d\n", i, ret);
            System.out.printf("Fibonacci of %d is: %d\n", i, ret2);
        }
    }

    public int lengthOfLongestSubstring(String s) {
        return 0;
    }

    /**
     * 斐波那契数列
     */
    public static int fibonacci(int n) {
        if (n < 2) {
            return n;
        }
        int first = 0, second = 1, ret = 0;
        for (int i = 2; i <= n; i++) {
            ret = first + second;
            first = second;
            second = ret;
        }
        return ret;
    }

    /**
     * 斐波那契数列递归
     */
    public static long fibonacci2(int n) {
        if (n < 2) {
            return n;
        } else {
            return fibonacci2(n - 1) + fibonacci2(n - 2);
        }
    }


}
