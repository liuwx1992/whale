package com.zoo.whale.leetcode;

import java.util.Arrays;

/**
 * @ClassName Rob
 * @Description 打家劫舍：
 * 你是一个专业的小偷，计划偷窃沿街的房屋。每间房内都藏有一定的现金，影响你偷窃的唯一制约因素就是相邻的房屋装有相互连通的防盗系统，
 * 如果两间相邻的房屋在同一晚上被小偷闯入，系统会自动报警。
 * 给定一个代表每个房屋存放金额的非负整数数组，计算你 不触动警报装置的情况下 ，一夜之内能够偷窃到的最高金额。
 * @Author weixin.liu
 * @Date 2026/5/7 20:48
 */
public class Rob {

    public static void main(String[] args) {
        Rob r = new Rob();
        check(r, null, 0);
        check(r, new int[]{}, 0);
        check(r, new int[]{5}, 5);
        check(r, new int[]{1, 2}, 2);
        check(r, new int[]{2, 1, 1, 2}, 4);
        check(r, new int[]{1, 2, 3, 1}, 4);
        check(r, new int[]{2, 7, 9, 3, 1}, 12);
        System.out.println("Rob main 测试全部通过");
    }

    private static void check(Rob r, int[] nums, int expected) {
        int got = r.rob(nums);
        if (got != expected) {
            throw new AssertionError(String.format("nums=%s 期望 %d 实际 %d",
                    nums == null ? "null" : Arrays.toString(nums), expected, got));
        }
        System.out.println("OK " + (nums == null ? "null" : Arrays.toString(nums)) + " => " + got);
    }

    public int rob(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int length = nums.length;
        if (length == 1) {
            return nums[0];
        }
        int[] dp = new int[length];
        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);
        for (int i = 2; i < length; i++) {
            dp[i] = Math.max(dp[i - 2] + nums[i], dp[i - 1]);
        }
        return dp[dp.length - 1];
    }

}
