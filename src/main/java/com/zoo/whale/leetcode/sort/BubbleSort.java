package com.zoo.whale.leetcode.sort;

import java.util.Arrays;

/**
 * @Date: 2025年 10月 18日
 * @Time:23时:07分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class BubbleSort {

    public static void bubbleSort(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            boolean swap = false;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] > arr[j]) {
                    swap(arr, i, j);
                    swap = true;
                }
            }
            if (!swap) {
                break;
            }
        }
    }

    public static void bubbleSort02(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    // 交换 arr[j] 和 arr[j+1]
                    swap(arr,j,j+1);
                    swapped = true;
                }
            }
            // 如果没有发生交换，说明已经有序
            if (!swapped) break;
        }
    }

    public static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void main(String[] args) {
        int[] arr = {64, 34, 25, 12, 22, 11, 90};
        bubbleSort02(arr);
        System.out.println("排序后的数组: " + Arrays.toString(arr));
    }


}
