package com.zoo.whale.leetcode.hash;

import java.util.*;

/**
 * @Date: 2026年 03月 07日
 * @Time:16时:50分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class GroupAnagrams {

    public static void main(String[] args) {
//       String[] strs = new String[]{"eat", "tea", "tan", "ate", "nat", "bat"};
        String[] strs = new String[]{"ddddddddddg", "dgggggggggg"};
        List<List<String>> result = groupAnagrams(strs);
        System.out.println(result);
    }


    public static List<List<String>> groupAnagrams(String[] strs) {
        List<List<String>> result = new ArrayList<>();
        if (strs == null || strs.length == 0) {
            return result;
        }
        HashMap<String, List<String>> map2 = new HashMap<>();
        for (int i = 0; i < strs.length; i++) {
            String s1 = strs[i];

            char[] charArray = s1.toCharArray();

            Arrays.sort(charArray);

            String key = new String(charArray);

            if (map2.get(key) == null) {
                List<String> list = new ArrayList<>();
                list.add(s1);
                map2.put(key, list);
            } else {
                map2.get(key).add(s1);
            }
        }
        for (Map.Entry<String, List<String>> entry : map2.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }

}
