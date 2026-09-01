package com.zoo.whale.atguigu;

import static org.bouncycastle.math.raw.Nat.inc;

/**
 * @Date: 2025年 05月 12日
 * @Time:20时:26分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class IncTest {

    public static void main(String[] args) {
        System.out.println(inc());
    }

    public static int inc() {
        int x;
        try {
            x = 1;
            return x;
        } catch (Exception e) {
            x = 2;
            return x;
        } finally {
            x = 3;
        }
    }
}
