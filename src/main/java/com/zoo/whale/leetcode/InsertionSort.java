package com.zoo.whale.leetcode;

/**
 * @Date: 2024年 11月 26日
 * @Time:20时:42分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class InsertionSort {

    public static void main(String[] args) {
        int[] arr = {12, 11, 13, 5, 6};
        insertionSort(arr);
        System.out.println("Sorted array:");
        for (int num : arr) {
            System.out.print(num + " ");
        }
    }

    // 插入排序
    public static void insertionSort(int[] arr) {
        int n = arr.length;
        int i = 1;
        int key = arr[i];  // 当前要插入的元素
        for (i = 0;i < n;i++) {
            int j = i -1;
            while (j >=0 && arr[i] < arr[j]) {
                arr[j+1] = arr[j];
                j--;
            }
            arr[j] = key;
        }
    }

}
