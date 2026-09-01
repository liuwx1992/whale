package com.zoo.whale.leetcode.array;

import java.util.*;

/**
 * @Date: 2026年 02月 28日
 * @Time:16时:27分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class FindErrorNums {

    public static void main(String[] args) {
//        int[] array = new int[]{1, 2, 2, 4};
//        int[] array = new int[]{1, 1};
//        int[] array = new int[]{1,2,2,3,3,4,8};
        int[] array = new int[]{1, 1};

//        int[] array = new int[]{5,0,10,0,10,6};
//        int[] array = new int[]{1, 7, 7, 7, 7};
//        int[] array = new int[]{8, 1, 2, 2, 2, 2, 2, 3};
        int[] smallNumbers = findErrorNums(array);
        System.out.println();
        for (int num : smallNumbers) {
            System.out.print(num + " ");
        }
    }

    public static int[] findErrorNums(int[] nums) {
        int[] result = new int[2];

        int[] count = new int[nums.length + 1];

        for (int num : nums) {
            count[num]++;
        }
        for (int idx = 1; idx <= nums.length; idx++) {
            int countNum = count[idx];
            if (countNum == 2) {
                result[0] = idx;
            }
            if (countNum == 0) {
                result[1] = idx;
            }
        }
        return result;
    }

}
