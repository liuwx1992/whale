package com.zoo.whale.leetcode.backtracking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Date: 2026年 02月 25日
 * @Time:09时:57分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class LetterCombinations {

    public static void main(String[] args) {
        String digits = "23";
        List<String> list = letterCombinations(digits);
        System.out.println(list);
    }

    private static List<String> letterCombinations(String digits) {
        List<String> combinations = new ArrayList<>();
        if (digits == null || digits.length() == 0) {
            return combinations;
        }
        Map<Character, String> phoneMap = new HashMap<Character, String>() {{
            put('2', "abc");
            put('3', "def");
            put('4', "ghi");
            put('5', "jkl");
            put('6', "mno");
            put('7', "pqrs");
            put('8', "tuv");
            put('9', "wxyz");
        }};

        backTracking(combinations, phoneMap, digits, 0, new StringBuilder());
        return combinations;
    }

    private static void backTracking(List<String> combinations, Map<Character, String> phoneMap,
                                     String digits, int index, StringBuilder combination) {
        int digitsLength = digits.length();
        if (combination.length() == digitsLength) {
            //合法放入集合
            combinations.add(combination.toString());
            return;
        }
        char c = digits.charAt(index);
        String letters = phoneMap.get(c);
        for (int i = 0; i < letters.length(); i++) {
            //和后面的逐个拼接
            combination.append(letters.charAt(i));
            //递归来拼接后面的
            backTracking(combinations, phoneMap, digits, index + 1, combination);
            //去掉拼接过的index所在节点的字母
            combination.deleteCharAt(index);
        }
    }


}
