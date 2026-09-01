package com.zoo.whale.leetcode.backtracking;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2026年 02月 25日
 * @Time:21时:09分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class CombinationSum {

    public static void main(String[] args) {
        int[] candidates = new int[]{2, 3, 6, 7};
        int target = 7;
        List<List<Integer>> lists = combinationSum(candidates, target);
        System.out.println(lists);
    }


    public static List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> combinations = new ArrayList<>();
        List<Integer> sumList = new ArrayList<>();
        dfs(combinations, candidates, target, sumList, 0);
        return combinations;
    }

    private static void dfs(List<List<Integer>> combinations, int[] candidates,
                                          int target, List<Integer> combine, int index) {
        if (index == candidates.length) {
            return;
        }
        if(target == 0){
            combinations.add(new ArrayList<>(combine));
            return;
        }

        dfs(combinations, candidates, target, combine, index + 1);

        if(target - candidates[index] >= 0){
            combine.add(candidates[index]);
            dfs(combinations, candidates, target - candidates[index], combine, index);
            combine.remove(combine.size() - 1);
        }
    }

    private static void dfs2(List<List<Integer>> combinations, int[] candidates,
                            int target, List<Integer> combine, int index) {

    }

}
