package com.zoo.whale.leetcode;

/**
 * @Date: 2025年 05月 27日
 * @Time:17时:17分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class QuickSort001 {

    public static void main(String[] args) {
        int[] arr = {3, 6, 8, 10, 1, 2, 1};
        quickSort(arr, 0, arr.length - 1);
        for (int num : arr) {
            System.out.print(num + " ");
        }
    }


    private static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            // 分区并获取基准位置
            int pivotIndex = partition(arr, low, high);
            // 递归排序左半部分
            quickSort(arr, low, pivotIndex - 1);
            // 递归排序右半部分
            quickSort(arr, pivotIndex + 1, high);
        }
    }

    private static int partition(int[] arr, int low, int high) {
        // 选择中间元素作为基准（避免最坏情况）
        int mid = low + (high - low) / 2;
        int pivot = arr[mid];
        // 将基准交换到末尾
        swap(arr, mid, high);

        int i = low; // 指向小于基准的边界
        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                swap(arr, i, j);
                i++;
            }
        }
        // 将基准放回正确位置
        swap(arr, i, high);
        return i;
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }


}
