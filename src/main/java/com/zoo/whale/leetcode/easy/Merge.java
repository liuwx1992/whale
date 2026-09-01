package com.zoo.whale.leetcode.easy;

import cn.hutool.core.lang.func.VoidFunc;

import java.util.Arrays;

/**
 * @Date: 2026年 04月 04日
 * @Time:13时:00分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class Merge {

    public static void main(String[] args) {
        int[] nums1 = new int[]{1,2,3,0,0,0};
        int m = 3, n = 3;
        int[] nums2 = new int[]{2,5,6};
        merge(nums1,n,nums2,m);
        Arrays.stream(nums1).forEach(System.out::print);
    }


    public static void merge(int[] nums1, int m, int[] nums2, int n) {
        int[] sorted = new int[m + n];
        int i = 0;
        int j = 0;
        int tempIndex = 0;
        while (i < m || j < n) {
            if(i == m){
                sorted[tempIndex] = nums2[j++];
            } else if (j == n) {
                sorted[tempIndex] = nums1[i++];
            } else if (nums1[i] <= nums2[j]) {
                sorted[tempIndex] = nums1[i++];
            } else {
                sorted[tempIndex] = nums2[j++];
            }
            tempIndex++;
        }
        for (int index = 0; index != m + n; ++index) {
            nums1[index] = sorted[index];
        }
    }
}
