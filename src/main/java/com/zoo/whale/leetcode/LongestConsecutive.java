package com.zoo.whale.leetcode;

import java.util.HashSet;
import java.util.Set;

/**
 * @Date: 2026年 04月 07日
 * @Time:00时:05分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class LongestConsecutive {

    public static void main(String[] args) {
        int[] nums = new int[]{100, 4, 200, 1, 3, 2};
        int i = longestConsecutive(nums);
        System.out.println(i);
    }

    public static int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int num : nums) {
            set.add(num);
        }
        int maxLength = 0;
        for (int num : set) {
            if (!set.contains(num - 1)) {
                int tempNum = num;
                int length = 1;

                while (set.contains(tempNum + 1)) {
                    tempNum += 1;
                    length++;
                }
                maxLength = Math.max(length, maxLength);
            }
        }
        return maxLength;
    }

}
