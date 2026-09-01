package com.zoo.whale.leetcode;

/**
 * @Date: 2024年 11月 21日
 * @Time:16时:29分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class QuickSortSimple {
    public static void quickSort(int[] arr, int left, int right) {
        if (left >= right) {
            return; // 终止条件：子数组长度为 0 或 1
        }

        int pivot = partition(arr, left, right); // 分区操作
        quickSort(arr, left, pivot - 1);         // 对左半部分递归排序
        quickSort(arr, pivot + 1, right);        // 对右半部分递归排序
    }

    private static int partition(int[] arr, int left, int right) {
        int pivot = arr[right]; // 基准值：选择最后一个元素
        int i = left;           // 指向小于基准值的边界

        for (int j = left; j < right; j++) { // 遍历 left 到 right-1
            if (arr[j] < pivot) {           // 如果当前元素小于基准值
                swap(arr, i, j);            // 将该元素交换到左侧
                i++;                        // 移动边界
            }
        }

        // 最后，将基准值放到边界位置
        swap(arr, i, right);
        return i; // 返回基准值最终位置
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

        System.out.println("排序后: ");
        printArray(arr);
    }

    private static void printArray(int[] arr) {
        for (int num : arr) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
}
