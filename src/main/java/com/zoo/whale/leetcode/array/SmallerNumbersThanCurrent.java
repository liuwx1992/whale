package com.zoo.whale.leetcode.array;

/**
 * @Date: 2026年 02月 27日
 * @Time:20时:42分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class SmallerNumbersThanCurrent {

    public static void main(String[] args) {
//        int[] array = new int[]{1,1,1,2,2,3,3,4,4,5,6};
        int[] array = new int[]{8,1,2,2,3};
        int[] smallNumbers = smallerNumbersThanCurrent(array);
        for (int num : smallNumbers) {
            System.out.print(num + " ");
        }
    }

    public static int[] smallerNumbersThanCurrent(int[] nums) {
        int[] smallNumbers = new int[nums.length];

        for (int i = 0; i < nums.length; i++) {
            int start = 0;
            int end = nums.length - 1;
            int num = nums[i];
            int newNum = 0;
            while (start <= end) {
                if(start == end){
                    if (num > nums[start]) {
                        newNum++;
                    }
                } else {
                    if (num > nums[start]) {
                        newNum++;
                    }
                    if (num > nums[end]) {
                        newNum++;
                    }
                }
                start++;
                end--;
            }
            smallNumbers[i] = newNum;
        }
        return smallNumbers;
    }

}
