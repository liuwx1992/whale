package com.zoo.whale.leetcode;

/**
 * @Date: 2024年 11月 23日
 * @Time:23时:23分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class MyQuickSort02 {

    public static void main(String[] args) {
        int[] arr = {3, 6, 8, 10, 1, 2, 1};
        quickSort(arr, 0, arr.length - 1);
        for (int num : arr) {
            System.out.print(num + " ");
        }
    }

    public static void quickSort(int[] arr, int left, int right) {
        if(left >= right){
            return;
        }
        int pivotIndex = partition(arr, left ,right);

        quickSort(arr, left ,pivotIndex - 1);
        quickSort(arr, pivotIndex + 1, right);
    }

    private static int partition(int[] arr, int left, int right) {
        int pivotValue = arr[right];

        int i = left;
        int j = right - 1;

        while (i <= j) {
            if(arr[i] > pivotValue && arr[j] < pivotValue){
                swap(arr,i,j);
                i++;
                j--;
            }
            if(arr[i] <= pivotValue){
                i++;
            }
            if(arr[j] >= pivotValue){
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
