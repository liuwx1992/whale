package com.zoo.whale.leetcode.array;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2026年 02月 28日
 * @Time:20时:43分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class FindDisappearedNumbers {

    public static void main(String[] args) {
        int[] array = new int[]{4, 3, 2, 7, 8, 2, 3, 1};
        List<Integer> disappearedNumbers = findDisappearedNumbers2(array);
        System.out.println(disappearedNumbers);
    }


    public static List<Integer> findDisappearedNumbers(int[] nums) {
        List<Integer> list = new ArrayList<>();
        int[] count = new int[nums.length + 1];
        for (int num : nums) {
            count[num]++;
        }
        for (int idx = 1; idx <= nums.length; idx++) {
            int countNum = count[idx];
            if (countNum == 0) {
                list.add(idx);
            }
        }
        return list;
    }

    public static List<Integer> findDisappearedNumbers2(int[] nums) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            int idx = Math.abs(nums[i]) - 1;
            if (nums[idx] > 0) {
                nums[idx] = -nums[idx];
            }
        }
        for (int i = 0; i < nums.length; i++) {
            int num = nums[i];
            if (num > 0) {
                list.add(i + 1);
            }
        }
        return list;
    }
}
