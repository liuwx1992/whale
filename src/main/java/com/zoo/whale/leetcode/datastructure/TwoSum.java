package com.zoo.whale.leetcode.datastructure;

import java.util.HashMap;
import java.util.Map;

/**
 * @Date: 2022年09月14日
 * @Time: 16时:31分
 * @Author: weixin.liu
 * @Version: 1.0
 * 给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target  的那 两个 整数，并返回它们的数组下标。
 *
 * 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。
 *
 * 你可以按任意顺序返回答案。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode.cn/problems/two-sum
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class TwoSum {

    public static void main(String[] args) {
        //int[] nums = new int[]{2,7,11,15};
        int[] nums = new int[]{3,3,4,15};
        int target = 6;
        int[] ints = twoSum(nums, target);
        for (int num:ints){
            System.out.println(num);
        }
    }

    public static int[] twoSum(int[] nums, int target) {
        int[] ret = new int[2];
        Map<Integer,Integer> map = new HashMap<>();
        for (int i = 0;i < nums.length;i++){
            int another = target - nums[i];
            if(map.get(another) != null){
                ret[1] = i;
                ret[0] = map.get(another);
            }
            map.put(nums[i], i);
        }
        return ret;
    }
}
