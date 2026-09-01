package com.zoo.whale.utils;


import org.apache.commons.lang3.StringUtils;

/**
 * @Date: 2025年 07月 22日
 * @Time:20时:46分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class AdvancedDoubleObfuscator {

    // 字符混淆映射表
    private static final char[] MAPPING = {
            '0', '5',
            '1', '6',
            '2', '7',
            '3', '8',
            '4', '9',
            '5', '0',
            '6', '1',
            '7', '2',
            '8', '3',
            '9', '4'
    };

    // 逆映射表
    private static final char[] INVERSE_MAPPING = new char[128];

    static {
        // 初始化逆映射表
        for (int i = 0; i < MAPPING.length; i += 2) {
            char orig = MAPPING[i];
            char mapped = MAPPING[i + 1];
            INVERSE_MAPPING[mapped] = orig;
        }

        // 特殊字符映射
        INVERSE_MAPPING['.'] = '.';
        INVERSE_MAPPING['-'] = '-';
    }

    /**
     * 混淆方法 - 创建自然的不一致效果
     *
     * @param input 原始double字符串
     * @return 混淆后的double字符串
     */
    public static String obfuscate(String input) {
        // 空、特殊值直接返回
        if (StringUtils.isBlank(input) || isSpecialValue(input)) {
            return input;
        }

        // 解析double确保有效性
        try {
            Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return input; // 无效格式原样返回
        }

        return applyCharacterMapping(input);
    }

    /**
     * 还原方法 - 100%还原原始字符串
     *
     * @param obfuscated 混淆后的字符串
     * @return 原始double字符串
     */
    public static String restore(String obfuscated) {
        // 空、特殊值直接返回
        if (StringUtils.isBlank(obfuscated) || isSpecialValue(obfuscated)){
            return obfuscated;
        }

        // 尝试解析double确保有效性
        try {
            Double.parseDouble(obfuscated);
        } catch (NumberFormatException e) {
            // 无效格式原样返回
            return obfuscated;
        }

        // 字符映射还原
        return reverseCharacterMapping(obfuscated);
    }

    /**
     * 应用字符映射混淆
     * */
    private static String applyCharacterMapping(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c >= '0' && c <= '9') {
                // 应用字符映射
                int index = (c - '0') * 2 + 1;
                result.append(MAPPING[index]);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * 反转字符映射
     * */
    private static String reverseCharacterMapping(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (c >= '0' && c <= '9') {
                result.append(INVERSE_MAPPING[c]);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * 检查特殊值
     */
    private static boolean isSpecialValue(String value) {
        return "NaN".equalsIgnoreCase(value) ||
                "Infinity".equalsIgnoreCase(value) ||
                "-Infinity".equalsIgnoreCase(value);
    }

    // 测试和验证
    public static void main(String[] args) {
        String[] testCases = {
                "123.456",
                "100.000",
                "-42.1950",
                "0.0000",
                "999999",
                "1234567890",
                "0.0",
                "-0.000"
        };

        System.out.println("Java 8 高级混淆测试 (原始 → 混淆后 → 还原):");
        System.out.println("==============================================");

        for (String original : testCases) {
            String obfuscated = obfuscate(original);
            String restored = restore(obfuscated);

            System.out.printf("原始: %-15s → 混淆: %-20s → 还原: %-15s | 匹配: %b%n",
                    original, obfuscated, restored,
                    original.equals(restored));

            // 测试数值是否相等
            double origValue = Double.parseDouble(original);
            double restoredValue = Double.parseDouble(restored);
            System.out.printf("数值验证: %f == %f → %b%n",
                    origValue, restoredValue, origValue == restoredValue);
        }

        // 多次测试查看不同混淆效果
        System.out.println("\n多次混淆效果展示:");
        String sample = "123.456";
        for (int i = 0; i < 5; i++) {
            System.out.printf("混淆 %d: %s%n", i + 1, obfuscate(sample));
        }

        // 特殊值测试
        testSpecialValue("NaN");
        testSpecialValue("Infinity");
        testSpecialValue("-Infinity");
    }

    private static void testSpecialValue(String value) {
        String obfuscated = obfuscate(value);
        String restored = restore(obfuscated);

        System.out.printf("\n特殊值测试: %-12s → 混淆: %-12s → 还原: %-12s | 匹配: %b%n",
                value, obfuscated, restored, value.equals(restored));
    }
}
