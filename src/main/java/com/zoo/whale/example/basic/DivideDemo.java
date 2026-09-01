package com.zoo.whale.example.basic;

import java.text.DecimalFormat;

/**
 * @Date: 2023年03月28日
 * @Time: 17时:23分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class DivideDemo {

    public static void main(String[] args) {
        String s = avgFormat2(10, 4);
        System.out.println(s);
    }

    protected static String avgFormat(int dividend, int divisor, int offset) {
        if (divisor != 0) {
            float temp = (float) dividend / divisor;
            StringBuilder formatStr = new StringBuilder("0.");
            for (int i = 1; i <= offset; i++) {
                formatStr.append("0");
            }
            DecimalFormat df = new DecimalFormat(formatStr.toString());
            return df.format(temp);
        }
        return null;
    }

    protected static String avgFormat2(int dividend, int divisor) {
        if (divisor != 0) {
            float temp = (float) dividend / divisor;

            int round = Math.round(temp);

            return String.valueOf(round);
        }
        return null;
    }

}
