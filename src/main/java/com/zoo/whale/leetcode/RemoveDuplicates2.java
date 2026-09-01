package com.zoo.whale.leetcode;

import java.util.Arrays;

/**
 * @Date: 2026年 04月 12日
 * @Time:14时:03分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class RemoveDuplicates2 {

    public static void main(String[] args) {
//                int[] array = new int[]{1, 1, 1, 2, 2, 3, 3, 3, 4, 4, 5, 6};
                int[] array = new int[]{0,0,1,1,1,1,2,3,3};
//        int[] array = new int[]{1, 2, 3, 4, 5, 6};
//        int[] array = new int[]{1,1,1,1,1,1};
        System.out.println(removeDuplicates(array));
        Arrays.stream(array).forEach(n -> System.out.print(n));
    }

    public static int removeDuplicates(int[] nums) {
        if (nums.length <= 2) {
            return 2;
        }
        int slow = 2;
        int fast = 2;
        while (fast < nums.length) {
            if (nums[slow-2] == nums[fast]) {
                fast++;
                continue;
            }
            nums[slow] = nums[fast];
            slow++;
            fast++;
        }
        return slow;
    }

}
