package com.zoo.whale.leetcode.backtracking;

import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName LetterCombinationsSolution
 * @Description TODO
 * @Author weixin.liu
 * @Date 2026/5/3 22:15
 */
public class LetterCombinationsSolution {

    public static void main(String[] args) {
        String digits = "23";
        List<String> list = letterCombinations(digits);
        System.out.println(list);
    }


    public static List<String> letterCombinations(String digits) {
        List<String> ans = new ArrayList<>();
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
        backTracking(digits, phoneMap, 0, ans, new StringBuilder());
        return ans;
    }

    private static void backTracking(String digits, Map<Character, String> phoneMap, int index,
                                     List<String> ans, StringBuilder stringBuilder) {
        if (stringBuilder.length() == digits.length()) {
            ans.add(stringBuilder.toString());
            return;
        }
        char letterNum = digits.charAt(index);
        String letters = phoneMap.get(letterNum);
        for (char c : letters.toCharArray()) {
            stringBuilder.append(c);
            backTracking(digits, phoneMap, index + 1, ans, stringBuilder);
            stringBuilder.deleteCharAt(index);
        }
    }

}
