package com.zoo.whale.leetcode.easy;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName:FirstUniqCharSolution Description:给定一个字符串，找到它的第一个不重复的字符，并返回它的索引。如果不存在，则返回 -1。
 * @Author:weixin.liu
 * @Date:2021/8/6
 * @Time:20:38
 * @Version；1.0
 */
public class FirstUniqCharSolution {
    public static void main(String[] args) {

    }

    public static int firstUniqChar(String s) {
        if (s == null || "".equals(s)) {
            return 0;
        }
        char[] chars = s.toCharArray();
        Map<Character, Integer> map = new HashMap<>();
        for (char c : chars) {
            if (map.get(c) != null) {
                map.put(c, map.get(c) + 1);
            } else {
                map.put(c, 1);
            }
        }
        for (int i = 0; i < chars.length; i++) {
            Integer value = map.get(chars[i]);
            if (value == 1) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 要第一个和最后一个的索引一致，那肯定是只有这个是对的了
     */
    public static int firstUniqChar2(String s) {
        if (s == null || "".equals(s)) {
            return 0;
        }
        for (int i = 0; i < s.length(); i++) {
            if (s.indexOf(s.charAt(i)) == s.lastIndexOf(s.charAt(i))) {
                return i;
            }
        }
        return -1;
    }


}
