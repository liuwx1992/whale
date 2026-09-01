package com.zoo.whale.leetcode;

/**
 * @Date: 2026年 04月 05日
 * @Time:22时:49分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class MaxSubArray {

    public static void main(String[] args) {
        int[] nums = new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4};
        int i = maxSubArray(nums);
        System.out.println(i);
    }


    public static int maxSubArray(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int[] dp = new int[nums.length];
        dp[0] = nums[0];

        int ans = nums[0];
        for (int i = 1; i < nums.length; i++) {
            dp[i] = Math.max(nums[i], dp[i - 1] + nums[i]);

            ans = Math.max(dp[i], ans);
        }
        return ans;
    }

    /*public static int maxSubArray(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int sum = 0;
        int ans = nums[0];
        for (int num : nums) {
            if (sum > 0) {
                sum += num;
            } else  {
                sum = num;
            }
            ans = Math.max(sum, ans);
        }
        return ans;
    }*/
}
