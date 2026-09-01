package com.zoo.whale.leetcode;

import java.util.Random;

/**
 * @Date: 2024年 11月 21日
 * @Time:17时:06分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class QuickSortOptimized {
    private static final Random RANDOM = new Random(); // 随机数生成器

    public static void quickSort(int[] arr, int left, int right) {
        while (left < right) { // 使用尾递归优化，避免深度递归
            int pivotIndex = randomizedPartition(arr, left, right); // 随机分区
            if (pivotIndex - left < right - pivotIndex) {
                quickSort(arr, left, pivotIndex - 1); // 递归较小的部分
                left = pivotIndex + 1;               // 尾递归大部分，避免栈增长
            } else {
                quickSort(arr, pivotIndex + 1, right);
                right = pivotIndex - 1;
            }
        }
    }

    private static int randomizedPartition(int[] arr, int left, int right) {
        int pivotIndex = left + RANDOM.nextInt(right - left + 1); // 随机选择基准值
        swap(arr, pivotIndex, right); // 将随机基准值放到数组末尾
        return partition(arr, left, right);
    }

    private static int partition(int[] arr, int left, int right) {
        int pivot = arr[right]; // 基准值
        int i = left;           // 指向小于基准值的区域

        for (int j = left; j < right; j++) {
            if (arr[j] < pivot) { // 如果当前值小于基准值
                swap(arr, i, j);  // 交换到左边区域
                i++;
            }
        }

        // 将基准值放到正确位置
        swap(arr, i, right);
        return i; // 返回基准值的最终位置
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void main(String[] args) {
        int[] arr = {5, 3, 8, 6, 2, 7, 4, 1};
        System.out.println("排序前: ");
        printArray(arr);

        quickSort(arr, 0, arr.length - 1);

        System.out.println("\n排序后: ");
        printArray(arr);
    }

    private static void printArray(int[] arr) {
        for (int num : arr) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
}
