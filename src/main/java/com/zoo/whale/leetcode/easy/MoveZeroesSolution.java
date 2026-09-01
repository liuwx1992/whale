package com.zoo.whale.leetcode.easy;

/**
 * @ClassName:MoveZeroesSolution
 * @Description:
 * @Author:weixin.liu
 * @Date:2021/8/5
 * @Time:18:21
 * @Version；1.0
 */
public class MoveZeroesSolution {
    public static void main(String[] args) {
        //int[] nums = new int[]{0,1,0,3,12};
        int[] nums = new int[]{0, 0, 1};
        moveZeroes2(nums);
        for (int num : nums) {
            System.out.print(num + ",");
        }
    }

    public static void moveZeroes3(int[] nums) {
        int[] tempArray = new int[nums.length];
        int start = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                tempArray[start++] = nums[i];
            }
        }
        nums = tempArray;
    }

    public static void moveZeroes2(int[] nums) {
        for (int start = 0, end = nums.length - 1; start <= end; start++) {
            int temp = nums[start];
            if (temp == 0) {
                moveForwardOneIndex(nums, start + 1, end);
                nums[end--] = temp;
                start--;
            }
        }
    }

    public static void moveForwardOneIndex(int[] nums, int start, int end) {
        for (int i = start; i <= end; i++) {
            nums[i - 1] = nums[i];
        }
    }

    public static void moveZeroes(int[] nums) {
        int temp = 0;
        for (int start = 0, end = nums.length - 1; start <= end; ) {
            if (nums[start] == 0 && nums[end] != 0) {
                temp = nums[start];
                nums[start] = nums[end];
                nums[end] = temp;
                start++;
                end--;
            }

            if (nums[start] == 0 && nums[end] != 0) {
                temp = nums[start];
                nums[start] = nums[end];
                nums[end] = temp;
                start++;
                end--;
            } else if (nums[start] == 0 && nums[end] == 0) {
                end--;
            } else if (nums[start] != 0 && nums[end] != 0) {
                start++;
            } else if (nums[start] != 0 && nums[end] == 0) {
                start++;
            }
        }
    }

}
