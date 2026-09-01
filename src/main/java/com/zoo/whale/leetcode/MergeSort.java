package com.zoo.whale.leetcode;

/**
 * @Date: 2024年 11月 24日
 * @Time:00时:14分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class MergeSort {

    /*public static void main(String[] args) {
        int[] arr = {6, 3, 8, 5, 2, 7, 4, 1};
        mergeSort(arr, 0, arr.length - 1);
        for (int num : arr) {
            System.out.print(num + " ");
        }
    }

    // 主归并排序方法
    public static void mergeSort(int[] arr, int left, int right) {
        if (left >= right) {
            return; // 递归终止条件
        }
        int mid = (left + right)/2;
        //int mid = left + (right - left) / 2; // 中间点
        mergeSort(arr, left, mid);          // 递归排序左半部分
        mergeSort(arr, mid + 1, right);     // 递归排序右半部分
        merge(arr, left, mid, right);       // 合并左右两部分
    }

    // 合并两个有序子数组
    private static void merge(int[] arr, int left, int mid, int right) {
        int[] temp = new int[right - left + 1]; // 辅助数组
        int i = left;     // 左半部分起始索引
        int j = mid + 1;  // 右半部分起始索引
        int k = 0;        // 临时数组的索引

        // 合并左右部分
        while (i <= mid && j <= right) {
            if (arr[i] <= arr[j]) {
                temp[k++] = arr[i++];
            } else {
                temp[k++] = arr[j++];
            }
        }

        // 如果左半部分有剩余
        while (i <= mid) {
            temp[k++] = arr[i++];
        }

        // 如果右半部分有剩余
        while (j <= right) {
            temp[k++] = arr[j++];
        }

        // 将排序后的数组复制回原数组
        for (int p = 0; p < temp.length; p++) {
            arr[left + p] = temp[p];
        }
    }*/


    public static void main(String[] args) {
        int[] arr = {6, 3, 8, 5, 2, 7, 4, 1};
        mergeSort(arr, 0, arr.length - 1);
        for (int num : arr) {
            System.out.print(num + " ");
        }
    }

    // 主归并排序方法
    public static void mergeSort(int[] arr, int left, int right) {
        if (left >= right) {
            return;
        }
        int middle = left + (right - left) / 2;
        mergeSort(arr, left, middle);
        mergeSort(arr, middle + 1, right);
        merge(arr, left, middle, right);
    }

    // 合并两个有序子数组
    private static void merge(int[] arr, int left, int mid, int right) {
        if (left >= right) {
            return;
        }
        int i = left;
        int k = 0;
        int j = mid + 1;  // 右半部分起始索引
        int[] temp = new int[right - left + 1];

        while (i <= mid && j <= right) {
            if (arr[i] <= arr[j]) {
                temp[k++] = arr[i++];
            } else {
                temp[k++] = arr[j++];
            }
        }

        while (i <= mid) {
            temp[k++] = arr[i++];
        }

        while (j <= right) {
            temp[k++] = arr[j++];
        }

        // 将排序后的数组复制回原数组
        for (int p = 0; p < temp.length; p++) {
            arr[left + p] = temp[p];
        }
    }


}
