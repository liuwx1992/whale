package com.zoo.whale.leetcode.sort;

/**
 * @Date: 2026年 02月 28日
 * @Time:14时:13分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class CountingSort {

    public static void main(String[] args) {
//        int[] array = new int[]{1,1,1,2,2,3,3,4,4,5,6};
        int[] array = new int[]{8, 1, 2, 2, 3};
//        int[] array = new int[]{1,2,2,3,3,4,8};
//        int[] array = new int[]{1, 2, 2, 1, 1};

//        int[] array = new int[]{5,0,10,0,10,6};
//        int[] array = new int[]{1, 7, 7, 7, 7};
//        int[] array = new int[]{8, 1, 2, 2, 2, 2, 2, 3};
        int[] smallNumbers = countingSortNaive(array);
        System.out.println();
        for (int num : smallNumbers) {
            System.out.print(num + " ");
        }
    }



    public static int[] countingSortNaive(int[] array) {
        int[] result = new int[array.length];
        int max = 0;
        for (int num : array) {
            max = Math.max(max, num);
        }
        int[] count = new int[max + 1];
        for (int num : array) {
            count[num]++;
        }
        // 3. 遍历 counter ，将各元素填入原数组 nums
        int i = 0;
        for (int num = 0; num < max + 1; num++) {
            for (int j = 0; j < count[num]; j++, i++) {
                result[i] = num;
            }
        }
        return result;
    }


}
