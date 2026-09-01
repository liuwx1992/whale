package com.zoo.whale.leetcode.datastructure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Date: 2022年09月13日
 * @Time: 20时:25分
 * @Author: weixin.liu
 * @Version: 1.0
 *
 * 存在重复元素
 * 给你一个整数数组 nums 。如果任一值在数组中出现 至少两次 ，返回 true ；如果数组中每个元素互不相同，返回 false 。
 * 提示：
 *
 * 1 <= nums.length <= 105
 * -109 <= nums[i] <= 109`
 */
public class ContainsDuplicate {

    public static void main(String[] args) {
        int[] nums = new int[]{1,2,3,1};
        System.out.println(containsDuplicate(nums));
    }

    public static boolean containsDuplicate(int[] nums) {
        if(nums == null || nums.length == 0){
            return false;
        }
        Set<Integer> set = new HashSet<>();
        for (int num:nums){
            if(!set.add(num)){
                return true;
            }
        }
        return false;
    }

}
