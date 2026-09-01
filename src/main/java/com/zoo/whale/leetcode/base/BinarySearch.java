package com.zoo.whale.leetcode.base;

/**
 * @ClassName:BinarySearch
 * @Description:二分查找
 * @Author:weixin.liu
 * @Date:2021/9/6
 * @Time:15:50
 * @Version；1.0
 */
public class BinarySearch {

    public static void main(String[] args) {
        int[] array = new int[]{-1, 0, 3, 5, 9, 12};
        int search = search(array, 9);
        int search2 = useRecurSearch(array, 9);
        System.out.println(search);
        System.out.println(search2);
    }

    public static int search(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        while (left <= right) {
            int middle = (left + right) / 2;
            if (nums[middle] == target) {
                return middle;
            } else if (nums[middle] < target) {
                left = middle + 1;
            } else if (nums[middle] > target) {
                right = middle - 1;
            }
        }
        return -1;
    }

    public static int useRecurSearch(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        if (target < nums[left] || target > nums[right]) {
            return -1;
        }
        int ret = recurSearch(nums, target, left, right);
        return ret;
    }


    //递归
    public static int recurSearch(int[] nums, int target, int left, int right) {
        if (left > right) {
            return -1;
        }
        int middle = (left + right) / 2;
        if (nums[middle] == target) {
            return middle;
        } else if (nums[middle] < target) {
            return recurSearch(nums, target, middle, right);
        } else if (nums[middle] > target) {
            return recurSearch(nums, target, left, middle);
        }
        return -1;
    }

}
