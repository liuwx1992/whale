package com.zoo.whale.leetcode;

import java.util.Arrays;

/**
 * @Date: 2026年 04月 05日
 * @Time:12时:10分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class MoveZeroes {

    public static void main(String[] args) {
        int[] nums = new int[]{4,2,4,0,0,3,0,5,1,0};
        moveZeroes(nums);
        Arrays.stream(nums).forEach(n -> System.out.print(n));
    }


    public static void moveZeroes(int[] nums) {
        int length = nums.length;
        if(length == 0){
            return;
        }
        int start = 0;
        int end = 1;
        while (end < length && start < length) {
            if (nums[start] == 0 && nums[end] != 0) {
                int temp = nums[start];
                nums[start] = nums[end];
                nums[end] = temp;
                start++;
                end++;
            } else if (nums[start] == 0 && nums[end] == 0){
                end++;
            } else {
                start++;
                end++;
            }
        }
    }
}
