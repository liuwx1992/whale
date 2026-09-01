package com.zoo.whale.leetcode.easy;

import java.util.Arrays;

/**
 * @ClassName:SortedSquaresSolution
 * @Description:
 * @Author:weixin.liu
 * @Date:2021/10/12
 * @Time:16:56
 * @Version；1.0
 */
public class SortedSquaresSolution {

    public static void main(String[] args) {
        int[] nums = new int[]{-4, -1, 0, 3, 10};
        int[] newNums = sortedSquares(nums);
        for (int i = 0; i < nums.length; i++) {
            System.out.print(nums[i] + " ");
        }
        System.out.println();
        for (int i = 0; i < newNums.length; i++) {
            System.out.print(newNums[i] + " ");
        }
    }

    public static int[] sortedSquares(int[] nums) {
        int[] newNums = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            newNums[i] = nums[i] * nums[i];
        }
        Arrays.sort(newNums);
        return newNums;
    }

}
