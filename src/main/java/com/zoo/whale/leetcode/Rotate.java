package com.zoo.whale.leetcode;

import java.util.Arrays;

/**
 * @Date: 2026年 04月 04日
 * @Time:17时:28分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class Rotate {

    public static void main(String[] args) {
        int[] nums = new int[]{1, 2, 3, 4, 5, 6, 7};
        int k = 3;
        Rotate rotate = new Rotate();

        rotate.rotate(nums, k);
        Arrays.stream(nums).forEach(n -> System.out.print(n));
    }


    /*public static void rotate(int[] nums, int k) {
        int[] rotate = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            int tempIndex = i + k;
            int newIndex = tempIndex % nums.length;
            rotate[newIndex] = nums[i];
        }
        for (int i = 0; i < nums.length; i++) {
            nums[i] = rotate[i];
        }
    }*/


    public void rotate(int[] nums, int k) {
        k = k % nums.length;
        reverse(nums, 0, nums.length - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, nums.length - 1);
    }

    public void reverse(int[] nums, int start, int end) {
        while (start < end) {
            int temp = nums[start];
            nums[start] = nums[end];
            nums[end] = temp;
            start++;
            end--;
        }
    }

}
