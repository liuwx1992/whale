package com.zoo.whale.leetcode.middle;

/**
 * 当且仅当每个相邻位数上的数字 x 和 y 满足 x <= y 时，我们称这个整数是单调递增的。
 *
 * 给定一个整数 n ，返回 小于或等于 n 的最大数字，且数字呈 单调递增 。
 *
 * 提示:
 *
 * 0 <= n <= 109
 * @Date: 2023年05月15日
 * @Time: 19时:32分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class MonotoneIncreasingDigits {

    public static void main(String[] args) {
        int a = 332;

        int i = monotoneIncreasingDigits03(a);
        System.out.println(i);


    }


    public static int monotoneIncreasingDigits03(int n) {
        char[] chars = Integer.toString(n).toCharArray();
        int i = 1;
        while (i < chars.length && chars[i-1] <= chars[i]){
            i++;
        }
        if(i < chars.length){
            while (i > 0 && chars[i-1] > chars[i]) {
                chars[i-1] -= 1;
                i -= 1;
            }
            for (i += 1;i < chars.length;++i){
                chars[i] = '9';
            }
        }
        return Integer.parseInt(new String(chars));
    }





    public static int monotoneIncreasingDigits02(int n) {
        char[] chars = Integer.toString(n).toCharArray();
        int i = 1;
        while (i < chars.length && chars[i - 1] <= chars[i]){
            i++;
        }
        if(i < chars.length){
            while (i > 0 && chars[i - 1] > chars[i]){
                i--;
            }
            chars[i - 1] -= 1;
            for (i += 1;i < chars.length;i++) {
                chars[i] = '9';
            }
        }
        return Integer.parseInt(new String(chars));
    }


    public static int monotoneIncreasingDigits(int n) {
        char[] chars = Integer.toString(n).toCharArray();
        int i = 1;
        while (i  <= chars.length && chars[i - 1] <= chars[i]){
            i++;
        }
        if(i < chars.length){
            while (i > 0 && chars[i - 1] > chars[i]){
                chars[i-1] -= 1;
                i -= 1;
            }
            for (i += 1;i < chars.length;++i){
                chars[i] = '9';
            }
        }
        return Integer.parseInt(new String(chars));
    }




    public static int monotoneIncreasingDigits01(int n) {
        char[] strN = Integer.toString(n).toCharArray();
        int i = 1;
        while (i < strN.length && strN[i - 1] <= strN[i]) {
            i += 1;
        }
        if (i < strN.length) {
            while (i > 0 && strN[i - 1] > strN[i]) {
                strN[i - 1] -= 1;
                i -= 1;
            }
            for (i += 1; i < strN.length; ++i) {
                strN[i] = '9';
            }
        }
        return Integer.parseInt(new String(strN));
    }

}
