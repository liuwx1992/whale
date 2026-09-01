package com.zoo.whale.leetcode;

import java.util.Arrays;

/**
 * @ClassName SearchInsert
 * @Description 搜索插入位置 给定一个排序数组和一个目标值，在数组中找到目标值，并返回其索引。如果目标值不存在于数组中，返回它将会被按顺序插入的位置。
 * @Author weixin.liu
 * @Date 2026/5/29 15:40
 */
public class SearchInsert {

    public static void main(String[] args) {
        int[] nums = {1, 3, 5, 6};
        runCase(nums, 5);
        runCase(nums, 2);
        runCase(nums, 7);
        runCase(nums, 0);
    }

    private static void runCase(int[] nums, int target) {
        System.out.println("nums=" + Arrays.toString(nums)
                + ", target=" + target
                + ", result=" + searchInsert(nums, target));
    }

    public static int searchInsert(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        int start = 0;
        int end = nums.length - 1;
        while (start <= end) {
            int middle = start + (end - start) / 2;
            if (target == nums[middle]) {
                return middle;
            } else if (target > nums[middle]) {
                start = middle + 1;
            } else {
                end = middle - 1;
            }
        }
        return start;
    }


}
