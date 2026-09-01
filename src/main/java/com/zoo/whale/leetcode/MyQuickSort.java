package com.zoo.whale.leetcode;

/**
 * @Date: 2024年 11月 21日
 * @Time:18时:06分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class MyQuickSort {

    public static void main(String[] args) {
        int[] arr = {3, 6, 8, 10, 1, 2, 1};
        quickSort(arr, 0, arr.length - 1);
        for (int num : arr) {
            System.out.print(num + " ");
        }
    }


    public static void quickSort(int[] arr, int left, int right) {
        if (left >= right) {
            return;
        }
        int pivotIndex = partition(arr, left, right);
        quickSort(arr, left, pivotIndex - 1);
        quickSort(arr, pivotIndex + 1, right);
    }

    private static int partition(int[] arr, int left, int right) {
        int pivot = arr[right];
        int i = left;
        int j = right - 1;
        while (i <= j) {
            if (arr[i] > pivot && arr[j] < pivot) {
                swap(arr, i, j);
                i++;
                j--;
            }
            if (arr[i] <= pivot) {
                i++;
            }
            if (arr[j] >= pivot) {
                j--;
            }
        }
        swap(arr, i, right);
        return i;
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

}
