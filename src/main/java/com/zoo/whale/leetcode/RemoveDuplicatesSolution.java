package com.zoo.whale.leetcode;

import java.util.Arrays;

/**
 * @Date: 2026年 04月 12日
 * @Time:14时:06分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class RemoveDuplicatesSolution {

    public static void main(String[] args) {
        int[] array = new int[]{1, 1, 1, 2, 2, 3, 3, 4, 4, 5, 6};
//        int[] array = new int[]{1, 2, 3, 4, 5, 6};
//        int[] array = new int[]{1,1,1,1,1,1};
        System.out.println(removeDuplicates(array));
    }


    public static int removeDuplicates(int[] nums) {
        if(nums.length == 1){
            return 1;
        }
        int slow = 1;
        int fast = 1;
        while (fast < nums.length) {
            if (nums[fast-1] == nums[fast]) {
                fast++;
                continue;
            }nums[slow] = nums[fast];
            slow++;
            fast++;
        }
        return slow;
    }

}
