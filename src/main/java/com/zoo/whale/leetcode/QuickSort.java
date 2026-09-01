package com.zoo.whale.leetcode;

/**
 * @Date: 2024年 11月 21日
 * @Time:09时:55分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class QuickSort {
    public static void quickSort(int[] arr, int left, int right) {
        if (left >= right) {
            return; // 终止条件
        }

        // 分区操作
        int pivotIndex = partition(arr, left, right);

        // 递归排序左侧
        quickSort(arr, left, pivotIndex - 1);

        // 递归排序右侧
        quickSort(arr, pivotIndex + 1, right);
    }

    private static int partition(int[] arr, int left, int right) {
        int pivot = arr[right]; // 选择最右侧元素为基准值
        int i = left;           // 初始化较小元素的边界

        // 遍历数组，将小于 pivot 的元素移到左侧
        for (int j = left; j < right; j++) {
            if (arr[j] < pivot) {
                swap(arr, i, j);
                i++;
            }
        }

        // 将 pivot 放到正确位置
        swap(arr, i, right);

        // 返回 pivot 的最终位置
        return i;
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void main(String[] args) {
        int[] arr = {3, 6, 8, 10, 1, 2, 1};
        quickSort(arr, 0, arr.length - 1);
        for (int num : arr) {
            System.out.print(num + " ");
        }
    }
}

