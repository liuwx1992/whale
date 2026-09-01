package com.zoo.whale.leetcode.easy;

import java.util.Arrays;

/**
 * @Date: 2026年 04月 04日
 * @Time:11时:49分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class RemoveElement {

    public static void main(String[] args) {
        int[] nums = new int[]{2,3,3,2};
        int val = 3;
        int[] expectedNums = new int[]{2, 2}; // 长度正确的预期答案。
        // 它以不等于 val 的值排序。

        int k = removeElement(nums, val); // 调用你的实现

        assert k == expectedNums.length;
        Arrays.sort(nums, 0, k); // 排序 nums 的前 k 个元素
        for (int i = 0; i < k; i++) {
            assert nums[i] == expectedNums[i];
        }
    }


    /*public static int removeElement(int[] nums, int val) {
        int start = 0;
        int end = nums.length - 1;
        int size = nums.length;
        while (start <= end) {
            int startNum = nums[start];
            int endNum = nums[end];
            if (startNum == val && endNum != val) {
                nums[start] = endNum;
                start++;
                end--;
                size--;
            } else if (startNum != val) {
                start++;
            } else {
                end--;
                size--;
            }
        }
        return size;
    }*/

    public static int removeElement(int[] nums, int val) {
        int start = 0;
        int end = nums.length - 1;
        while (start <= end) {
            int startNum = nums[start];
            int endNum = nums[end];
            if (startNum == val) {
                nums[start] = endNum;
                end--;
            } else {
                start++;
            }
        }
        return start;
    }

}
