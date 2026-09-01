package com.zoo.whale.leetcode.array;

/**
 * @Date: 2026年 02月 27日
 * @Time:20时:36分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class GetConcatenation {

    public static void main(String[] args) {

    }

    public int[] getConcatenation(int[] nums) {
        int length = nums.length;
        int[] array = new int[length * 2];
        for (int i = 0; i < nums.length; i++) {
            array[i] = nums[i];
            array[i + length] = nums[i];
        }
        return array;
    }
}
