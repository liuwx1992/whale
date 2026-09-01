package com.zoo.whale.leetcode;

/**
 * @Date: 2024年 11月 21日
 * @Time:17时:59分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class QuickSortFinal {

    // 快速排序入口方法
    public static void quickSort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;  // 边界条件：空数组或只有一个元素，直接返回
        }
        quickSort(arr, 0, arr.length - 1); // 调用递归排序方法
    }

    // 快速排序的递归方法
    private static void quickSort(int[] arr, int left, int right) {
        if (left >= right) {
            return;  // 递归终止条件，区间长度为0或1，不需要排序
        }

        // 执行分区操作，返回基准元素的正确位置
        int pivotIndex = partition(arr, left, right);

        // 对基准元素左侧和右侧的子数组进行递归排序
        quickSort(arr, left, pivotIndex - 1);
        quickSort(arr, pivotIndex + 1, right);
    }

    // 分区方法，使用双指针
    private static int partition(int[] arr, int left, int right) {
        int pivot = arr[right];  // 选取最后一个元素作为基准值
        int i = left - 1;         // i 初始化为 left - 1

        // 遍历整个数组，进行分区
        for (int j = left; j < right; j++) {
            if (arr[j] <= pivot) { // 找到小于等于基准值的元素
                i++; // 左指针右移
                swap(arr, i, j); // 交换到正确位置
            }
        }

        // 最后将基准值放到正确的位置
        swap(arr, i + 1, right);
        return i + 1; // 返回基准值的正确位置
    }

    // 交换数组中两个元素的辅助方法
    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // 打印数组，方便调试
    private static void printArray(int[] arr) {
        for (int num : arr) {
            System.out.print(num + " ");
        }
        System.out.println();
    }

    // 测试方法
    public static void main(String[] args) {
        int[] arr = {5, 3, 8, 6, 2, 7, 4, 1};
        System.out.println("Original array:");
        printArray(arr);

        quickSort(arr);  // 执行快速排序

        System.out.println("Sorted array:");
        printArray(arr);
    }
}

