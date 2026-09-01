package com.zoo.whale.leetcode;

import java.util.Arrays;

/**
 * @Date: 2026年 04月 12日
 * @Time:15时:20分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class SubarraySum {

    public static void main(String[] args) {
//        int[] array = new int[]{1,1,1};
//        int[] array = new int[]{1, 2, 3};
        int[] array = new int[]{1, 2, 1, 2, 1};
        int k = 3;
        System.out.println(subarraySum(array, k));
    }

    public static int subarraySum(int[] nums, int k) {
        Arrays.sort(nums);
        int sortNum = 0;
        for (int i = 0; i < nums.length; i++) {
            int sum = 0;
            for (int j = i + 1; j < nums.length; j++) {
                sum = nums[i] + nums[j];
                if (sum == k) {
                    sortNum++;
                }
            }
        }
        return sortNum;
    }
}
