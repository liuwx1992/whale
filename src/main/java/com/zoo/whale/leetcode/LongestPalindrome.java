package com.zoo.whale.leetcode;

import org.apache.commons.lang3.StringUtils;

/**
 * 给你一个字符串 s，找到 s 中最长的回文子串
 * 示例 1：
 *
 * 输入：s = "babad"
 * 输出："bab"ss
 * 解释："aba" 同样是符合题意的答案。
 * 示例 2：
 *
 * 输入：s = "cbbd"
 * 输出："bb"
 *
 *
 * 提示：
 *
 * 1 <= s.length <= 1000
 * s 仅由数字和英文字母组成
 * @Date: 2024年 11月 08日
 * @Time:20时:40分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class LongestPalindrome {

    /*public static String longestPalindrome(String s) {
        if(StringUtils.isBlank(s)){
            return "";
        }
        int length = s.length();
        if(length / 2 == 1){
            int start = length / 2;

        } else {
            int start = length / 2 - 1;


        }
        *//*for () {

        }*//*
    }


    public static void main(String[] args) {

    }*/

    public static String longestPalindrome(String s) {
        if (s == null || s.length() < 1) return "";

        int start = 0, end = 0;
        for (int i = 0; i < s.length(); i++) {
            int len1 = expandAroundCenter(s, i, i);     // 以单个字符为中心
            int len2 = expandAroundCenter(s, i, i + 1); // 以两个字符为中心
            int len = Math.max(len1, len2);
            if (len > end - start) {
                start = i - (len - 1) / 2;
                end = i + len / 2;
            }
        }
        String substring = s.substring(start, end + 1);
        return substring;
    }

    private static int expandAroundCenter(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return right - left - 1;
    }

    public static void main(String[] args) {
        String input = "babad";
        System.out.println("Longest Palindromic Substring of \"" + input + "\" is: " + longestPalindrome(input));
    }

}
