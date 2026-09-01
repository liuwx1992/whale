package com.zoo.whale.leetcode.array;

/**
 * @Date: 2026年 02月 28日
 * @Time:15时:04分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class FindMaxConsecutiveOnes {

    public static void main(String[] args) {
        int[] array = new int[]{1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0};
        int length = findMaxConsecutiveOnes(array);
        System.out.println(length);
    }

    public static int findMaxConsecutiveOnes(int[] nums) {
        int maxCount = 0, count = 0;
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            if (nums[i] == 1) {
                count++;
            } else {
                maxCount = Math.max(maxCount, count);
                count = 0;
            }
        }
        maxCount = Math.max(maxCount, count);
        return maxCount;
    }

    public static int findMaxConsecutiveOnes2(int[] nums) {
        int max = 0;
        int maxTemp = 0;
        boolean start = true;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == 1) {
                if (start) {
                    maxTemp++;
                } else {
                    maxTemp = 1;
                }
                start = true;
                max = Math.max(max, maxTemp);
            } else {
                start = false;
            }
        }
        return max;
    }

}
