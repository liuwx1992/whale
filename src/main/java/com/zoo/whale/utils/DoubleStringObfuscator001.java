package com.zoo.whale.utils;

/**
 * @Date: 2025年 07月 22日
 * @Time:17时:43分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class DoubleStringObfuscator001 {

    // 字符偏移混淆
    public static String obfuscate(double value) {
        String str = Double.toString(value);
        char[] chars = str.toCharArray();

        // 简单字符偏移混淆
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                // 数字字符循环偏移 (0-9)
                chars[i] = (char)('0' + (chars[i] - '0' + 5) % 10);
            }
        }
        return new String(chars);
    }

    // 解混淆
    public static double deobfuscate(String obfStr) {
        char[] chars = obfStr.toCharArray();

        // 逆向字符偏移
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                // 逆向偏移
                chars[i] = (char)('0' + (chars[i] - '0' + 5) % 10);
            }
        }
        return Double.parseDouble(new String(chars));
    }

    public static void main(String[] args) {
        double value = 123.456;
        String obf = obfuscate(value);
        double deobf = deobfuscate(obf);

        System.out.println("原始值: " + value);
        System.out.println("混淆字符串: " + obf); // 678.901
        System.out.println("还原值: " + deobf);
    }

}
