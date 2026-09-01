package com.zoo.whale.leetcode.backtracking;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2026年 02月 25日
 * @Time:16时:49分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class GenerateParenthesis {

    public static void main(String[] args) {
        List<String> list = generateParenthesis(3);
        System.out.println(list);
    }

    public static List<String> generateParenthesis(int n) {
        List<String> combinations = new ArrayList<String>();
        generateAll(new char[2 * n], 0, combinations);
        return combinations;
    }

    private static void generateAll(char[] current, int pos, List<String> combinations) {
        if(current.length == pos){
            if(valid(current)){
                combinations.add(new String(current));
            }
        } else {
            current[pos] = '(';
            generateAll(current, pos + 1, combinations);
            current[pos] = ')';
            generateAll(current, pos + 1, combinations);
        }
    }

    public static boolean valid(char[] current) {
        int balance = 0;
        for (char c: current) {
            if (c == '(') {
                ++balance;
            } else {
                --balance;
            }
            if (balance < 0) {
                return false;
            }
        }
        return balance == 0;
    }

}
