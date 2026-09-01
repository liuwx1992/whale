package com.zoo.whale.leetcode.stack;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2026年 03月 01日
 * @Time:21时:34分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class BuildArray {

    public static void main(String[] args) {
        int[] array = new int[]{1, 5};
        List<String> disappearedNumbers = buildArray2(array, 6);
        System.out.println(disappearedNumbers);
    }

    public static List<String> buildArray(int[] target, int n) {
        List<String> result = new ArrayList<>();

        int max = target[target.length - 1];
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < target.length; i++) {
            list.add(target[i]);
        }
        for (int i = 1; i <= n; i++) {
            if (i > max) {
                break;
            }
            if (list.contains(i)) {
                result.add("Push");
            } else {
                result.add("Push");
                result.add("Pop");
            }
        }
        return result;
    }

    public static List<String> buildArray2(int[] target, int n) {
        List<String> result = new ArrayList<>();
        int curr = 1;

        for (int num : target) {
            while (curr < num) {
                //说明不存在
                result.add("Push");
                result.add("Pop");
                curr++;
            }
            result.add("Push");
            curr++;
        }

        return result;
    }


}
