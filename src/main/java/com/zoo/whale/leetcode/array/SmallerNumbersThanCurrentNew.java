package com.zoo.whale.leetcode.array;

import org.apache.commons.math3.analysis.function.Max;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Date: 2026年 02月 28日
 * @Time:10时:06分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class SmallerNumbersThanCurrentNew {

    public static void main(String[] args) {
//        int[] array = new int[]{1,1,1,2,2,3,3,4,4,5,6};
        int[] array = new int[]{8,1,2,2,3};
//        int[] array = new int[]{1,2,2,3,3,4,8};
//        int[] array = new int[]{1, 2, 2, 1, 1};

//        int[] array = new int[]{5,0,10,0,10,6};
//        int[] array = new int[]{1, 7, 7, 7, 7};
//        int[] array = new int[]{8, 1, 2, 2, 2, 2, 2, 3};
        int[] smallNumbers = smallerNumbersThanCurrent(array);
        System.out.println();
        for (int num : smallNumbers) {
            System.out.print(num + " ");
        }
    }

    public static int[] smallerNumbersThanCurrent2(int[] nums) {
        int length = nums.length;

        int[] smallNumbers = new int[length];

        int[] array = nums.clone();

        Arrays.sort(array);

        Map<Integer, Integer> map = new HashMap<>();

        for (int idx = 0; idx < array.length; idx++) {
            int num = array[idx];
            map.putIfAbsent(num, idx);
        }
        for (int i = 0; i < nums.length; i++) {
            Integer val = map.get(nums[i]);
            smallNumbers[i] = val;
        }
        return smallNumbers;
    }

    public static int[] smallerNumbersThanCurrent(int[] nums) {
        int length = nums.length;

        int[] smallNumbers = new int[length];

        int[] count = new int[101];

        for (int num : nums) {
            count[num]++;
        }
        for (int i = 1; i < 101; i++) {
            count[i] += count[i - 1];
        }
        for (int i = 0; i < nums.length; i++) {
            int idx = nums[i];
            if (idx == 0) {
                smallNumbers[i] = 0;
            } else {
                smallNumbers[i] = count[idx - 1];
            }
        }
        return smallNumbers;
    }

}
