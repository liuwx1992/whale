package com.zoo.whale.leetcode;

/**
 * @Date: 2026年 04月 05日
 * @Time:00时:45分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class RemoveDuplicates {

    public static void main(String[] args) {
//        int[] array = new int[]{1, 1, 1, 2, 2, 3, 3, 4, 4, 5, 6};
        int[] array = new int[]{1, 2, 3, 4, 5, 6};
//        int[] array = new int[]{1,1,1,1,1,1};
        System.out.println(removeDuplicates(array));
    }


    /*public static int removeDuplicates(int[] nums) {
        int length = nums.length;
        int fast = 1, slow = 1;
        while (fast < length) {
            if (nums[fast - 1] != nums[fast]) {
                nums[slow] = nums[fast];
                slow++;
            }
            fast++;
        }
        return slow;
    }*/

    public static int removeDuplicates(int[] nums) {
        int length = nums.length;
        int fast = 1, slow = 1;
        while (fast < length) {
            if (nums[fast - 1] == nums[fast]) {
                fast++;
                continue;
            }
            //遇到不一致的就将新的值赋给slow
            nums[slow] = nums[fast];
            slow++;
            fast++;
        }
        return slow;
    }

}
