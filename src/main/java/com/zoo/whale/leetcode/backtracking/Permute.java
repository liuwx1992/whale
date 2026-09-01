package com.zoo.whale.leetcode.backtracking;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName Permute
 * @Description 给定一个不含重复数字的数组 nums ，返回其 所有可能的全排列 。你可以 按任意顺序 返回答案。
 * @Author weixin.liu
 * @Date 2026/5/3 23:15
 */
public class Permute {

    public static void main(String[] args) {

    }


    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        if (nums == null) {
            return ans;
        }
        List<Integer> numList = new ArrayList<>();
        backTracing(nums, ans, 0, numList);
        return ans;
    }

    private void backTracing(int[] nums, List<List<Integer>> ans, int index, List<Integer> numList) {
        if (numList.size() == nums.length) {
            ans.add(numList);
        }

    }

}
