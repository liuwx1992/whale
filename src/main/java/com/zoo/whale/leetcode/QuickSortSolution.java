package com.zoo.whale.leetcode;

/**
 * @Date: 2024年 11月 21日
 * @Time:16时:23分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class QuickSortSolution {
    public static void quickSort(int[] arr, int left, int right) {
        if (left >= right){
            return; // 递归终止条件
        }

        int pivot = arr[right];    // 选取最后一个元素作为基准值
        int i = left;
        int j = right - 1; // 定义双指针

        // 分区过程
        while (i <= j) {
            int iValue = arr[i];
            int jValue = arr[j];
            if (iValue > pivot && jValue < pivot) {
                // 左大右小的元素交换
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
                i++;
                j--;
            }
            if (iValue <= pivot){
                i++; // 左指针右移
            }
            if (jValue >= pivot){
                j--; // 右指针左移
            }
        }

        // 将基准值放到正确位置
        arr[right] = arr[i];
        arr[i] = pivot;

        // 递归处理左右两侧
        quickSort(arr, left, i - 1);
        quickSort(arr, i + 1, right);
    }

    public static void main(String[] args) {
        int[] arr = {5, 3, 8, 6, 2, 7, 4, 1};
        System.out.println("排序前: ");
        for (int num : arr) System.out.print(num + " ");

        quickSort(arr, 0, arr.length - 1);

        System.out.println("\n排序后: ");
        for (int num : arr) System.out.print(num + " ");
    }
}
