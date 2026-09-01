package com.zoo.whale.utils;


/**
 * @Date: 2024年 12月 18日
 * @Time:15时:42分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class TraditionalChineseUtils {
    /**
     * 判断字符串中是否包含繁体汉字
     *
     * @param input 输入的字符串
     * @return true：包含繁体汉字，false：不包含繁体汉字
     */
    public static boolean containsTraditionalChinese(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        for (char c : input.toCharArray()) {
            // 将当前字符转换为简体
            /*String simplified = ZhConverterUtil.toSimple(String.valueOf(c));

            // 如果转换前后的字符不同，说明是繁体字
            if (!simplified.equals(String.valueOf(c))) {
                return true;
            }*/
        }
        return false;
    }

    public static void main(String[] args) {
        long l = System.currentTimeMillis();
        System.out.println(l);

        // 测试示例
        String text1 = "测试字符串"; // 全是简体字
        String text2 = "這是測試字符串"; // 包含繁体字
        String text3 = "Hello World!"; // 无汉字

        //System.out.println("text1 是否包含繁体字: " + containsTraditionalChinese(text1));
        System.out.println("text2 是否包含繁体字: " + containsTraditionalChinese(text2));
        //System.out.println("text3 是否包含繁体字: " + containsTraditionalChinese(text3));
        System.out.println(System.currentTimeMillis());
        System.out.println(System.currentTimeMillis() - l);
    }
}

