package com.zoo.whale.leetcode.easy;


import org.apache.commons.collections.CollectionUtils;

import java.util.*;

/**
 * @ClassName:IntersectSolution
 * @Description:
 * @Author:weixin.liu
 * @Date:2021/8/3
 * @Time:18:27
 * @Version；1.0
 */
public class IntersectSolution {
    public static void main(String[] args) {

    }

    public static void add(int num, String s) {
        num = 100;
        s = "ss哎";
    }

    /**
     *
     */
    public static int[] intersect(int[] nums1, int[] nums2) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int num : nums1) {
            if (map.get(num) != null) {
                map.put(num, map.get(num) + 1);
            } else {
                map.put(num, 1);
            }
        }
        List<Integer> list = new ArrayList<>();
        for (int num : nums2) {
            if (map.get(num) != null && map.get(num) > 0) {
                list.add(num);
                map.put(num, map.get(num) - 1);
            }
        }
        int[] ret = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ret[i] = list.get(i);
        }
        return ret;
    }

    public int[] intersect2(int[] nums1, int[] nums2) {
        return null;
    }

}
