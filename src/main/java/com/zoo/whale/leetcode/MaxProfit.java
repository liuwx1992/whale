package com.zoo.whale.leetcode;

import java.util.Arrays;

/**
 * @ClassName MaxProfit
 * @Description TODO
 * @Author weixin.liu
 * @Date 2026/5/17 21:44
 */
public class MaxProfit {


    public static void main(String[] args) {
        MaxProfit solver = new MaxProfit();
        runCase(solver, new int[]{7, 1, 5, 3, 6, 4}, 5);
        runCase(solver, new int[]{7, 6, 4, 3, 1}, 0);
        runCase(solver, new int[]{2, 4, 1}, 2);
        runCase(solver, new int[]{1}, 0);
        runCase(solver, null, 0);
    }

    private static void runCase(MaxProfit solver, int[] prices, int expected) {
        int actual = solver.maxProfit(prices);
        System.out.println(
                "prices=" + Arrays.toString(prices)
                        + ", expected=" + expected
                        + ", actual=" + actual
                        + ", pass=" + (actual == expected));
    }

    /*public int maxProfit(int[] prices) {
        if (prices == null || prices.length == 1) {
            return 0;
        }
        int ans = 0;
        for (int i = 0; i < prices.length; i++) {
            for (int j = i + 1;j < prices.length;j++) {
                int tempAns = prices[j] - prices[i];
                ans = Math.max(tempAns, ans);
            }
        }
        return ans;
    }*/

    public int maxProfit(int[] prices) {
        if (prices == null || prices.length == 1) {
            return 0;
        }
        int minprice = Integer.MAX_VALUE;
        int maxProfit = 0;
        for (int i = 0; i < prices.length; i++) {
            int current = prices[i];
            if (current < minprice) {
                minprice = current;
            } else if ((current - minprice > maxProfit)) {
                maxProfit = current - minprice;
            }
        }
        return maxProfit;
    }

}
