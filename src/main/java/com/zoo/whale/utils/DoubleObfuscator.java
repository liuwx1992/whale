package com.zoo.whale.utils;

/**
 * @Date: 2025年 07月 22日
 * @Time:16时:30分
 * @Author: weixin.liu
 * @Version:V1.0
 */
import java.nio.ByteBuffer;
import java.util.Base64;

public class DoubleObfuscator {

    // 混淆密钥（可自定义）
    private static final long KEY = 0xCAFEBABEDEADBEEFL;
    private static final int SHIFT = 17; // 移位量

    // 混淆double值
    public static String obfuscate(double value) {
        // 1. 将double转换为64位long
        long bits = Double.doubleToLongBits(value);

        // 2. 使用密钥进行异或混淆
        bits ^= KEY;

        // 3. 循环左移操作
        bits = Long.rotateLeft(bits, SHIFT);

        // 4. 转换为Base64编码的短字符串
        byte[] bytes = longToBytes(bits);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // 解混淆
    public static double deobfuscate(String obfuscated) {
        // 1. 从Base64解码
        byte[] bytes = Base64.getUrlDecoder().decode(obfuscated);

        // 2. 转换为long
        long bits = bytesToLong(bytes);

        // 3. 循环右移（还原）
        bits = Long.rotateRight(bits, SHIFT);

        // 4. 使用密钥进行异或还原
        bits ^= KEY;

        // 5. 转换回double
        return Double.longBitsToDouble(bits);
    }

    // long转换为8字节数组
    private static byte[] longToBytes(long value) {
        return ByteBuffer.allocate(8).putLong(value).array();
    }

    // 8字节数组转换为long
    private static long bytesToLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }

    public static void main(String[] args) {
        double original = 123.456789;

        System.out.println("原始值: " + original);

        // 混淆
        String obfuscated = obfuscate(original);
        System.out.println("混淆结果: " + obfuscated + " (长度: " + obfuscated.length() + "字符)");

        // 解混淆
        double restored = deobfuscate(obfuscated);
        System.out.println("还原值: " + restored);

        // 测试不同值
        testValue(0.0);
        testValue(-1.0);
        testValue(999.999);
        testValue(Double.MAX_VALUE);
        testValue(Double.MIN_VALUE);
        testValue(Double.NaN);
    }

    private static void testValue(double value) {
        String obf = obfuscate(value);
        double deobf = deobfuscate(obf);

        // 特殊处理NaN
        if (Double.isNaN(value)) {
            System.out.printf("测试: NaN → %s → %s%n", obf, Double.isNaN(deobf) ? "NaN" : deobf);
        } else {
            System.out.printf("测试: %f → %s → %f%n", value, obf, deobf);
        }
    }
}
