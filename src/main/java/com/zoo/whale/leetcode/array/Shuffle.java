package com.zoo.whale.leetcode.array;

/**
 * @Date: 2026年 02月 28日
 * @Time:14时:43分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class Shuffle {

    public static void main(String[] args) {
//        int[] array = new int[]{1,1,1,2,2,3,3,4,4,5,6};
        int[] array = new int[]{2, 5, 1, 3, 4, 7};
//        int[] array = new int[]{1,2,2,3,3,4,8};
//        int[] array = new int[]{1, 2, 2, 1, 1};

//        int[] array = new int[]{5,0,10,0,10,6};
//        int[] array = new int[]{1, 7, 7, 7, 7};
//        int[] array = new int[]{8, 1, 2, 2, 2, 2, 2, 3};
        int[] smallNumbers = shuffle(array, 3);
        System.out.println();
        for (int num : smallNumbers) {
            System.out.print(num + " ");
        }
    }


    public static int[] shuffle(int[] nums, int n) {
        int[] result = new int[2 * n];
        for (int i = 0; i < nums.length; i++) {
            if (i < n) {
                result[2 * i] = nums[i];
            } else {
                result[2 * (i - n) + 1] = nums[i];
            }
        }
        return result;
    }

    public static int[] shuffle2(int[] nums, int n) {
        int[] result = new int[2 * n];
        int[] numX = new int[n];
        int[] numY = new int[n];
        for (int i = 0; i < nums.length; i++) {
            if (i < n) {
                result[2 * i] = nums[i];
            } else {
                result[2 * (i - n) + 1] = nums[i];
            }
        }
        /*for (int i = 0; i < numX.length; i++) {
            result[2 * i] = numX[i];
        }
        for (int i = 0; i < numX.length; i++) {
            result[2 * i + 1] = numY[i];
        }*/
        return result;
    }

}
