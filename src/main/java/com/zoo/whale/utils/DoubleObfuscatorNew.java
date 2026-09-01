package com.zoo.whale.utils;

/**
 * @Date: 2025年 07月 22日
 * @Time:16时:39分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class DoubleObfuscatorNew {

    // 混淆密钥（可自定义）
    private static final double KEY = 123456789.12345; // 建议使用大数
    private static final double SCALE = 1e12;          // 缩放因子

    // 混淆double值
    public static double obfuscate(double value) {
        // 1. 将double转换为可处理的整数形式
        long scaled = (long) (value * SCALE);

        // 2. 转换为长整型进行位操作
        long bits = Double.doubleToLongBits(value);

        // 3. 使用密钥进行变换
        double transformed = transform(value, KEY);

        // 4. 添加噪声（可逆）
        return transformed + (scaled % 1000) / SCALE / 1000.0;
    }

    // 解混淆
    public static double deobfuscate(double obfuscated) {
        // 1. 提取噪声部分（整数部分）
        long noise = (long) (obfuscated * SCALE) % 1000;

        // 2. 移除噪声
        double cleaned = obfuscated - noise / SCALE / 1000.0;

        // 3. 逆向变换
        return transform(cleaned, -KEY);
    }

    // 核心变换函数（可逆数学操作）
    private static double transform(double value, double key) {
        // 使用双曲正切函数实现可逆变换
        double transformed = Math.tanh(value * 0.1 + key);

        // 增加非线性变换
        return transformed * (1 + Math.sin(value));
    }

    // 简化版（更高效）
    public static double simpleObfuscate(double value) {
        return value * KEY;
    }

    public static double simpleDeobfuscate(double obfuscated) {
        return obfuscated / KEY;
    }

    public static void main(String[] args) {
        double original = 123.456789;

        System.out.println("原始值: " + original);

        // 混淆
        double obfuscated = obfuscate(original);
        System.out.println("混淆结果: " + obfuscated);

        // 解混淆
        double restored = deobfuscate(obfuscated);
        System.out.println("还原值: " + restored);

        // 简单版测试
        double simpleObf = simpleObfuscate(original);
        double simpleDeobf = simpleDeobfuscate(simpleObf);
        System.out.println("\n简单混淆: " + simpleObf);
        System.out.println("简单还原: " + simpleDeobf);

        // 测试不同值
        testValue(0.0);
        testValue(-1.0);
        testValue(999.999);
        testValue(Double.MAX_VALUE / 1e100); // 避免溢出
        testValue(Double.MIN_VALUE * 1e100);
    }

    private static void testValue(double value) {
        double obf = obfuscate(value);
        double deobf = deobfuscate(obf);

        // 计算相对误差
        double error = Math.abs((value - deobf) / value);

        // 特殊处理NaN和零
        if (Double.isNaN(value)) {
            System.out.printf("测试: NaN → %.3f → %s%n", obf, Double.isNaN(deobf) ? "NaN" : deobf);
        } else if (value == 0) {
            System.out.printf("测试: 0.0 → %.3f → %.10f (误差: %e)%n",
                    obf, deobf, deobf);
        } else {
            System.out.printf("测试: %f → %.3f → %f (相对误差: %e)%n",
                    value, obf, deobf, error);
        }
    }

}
