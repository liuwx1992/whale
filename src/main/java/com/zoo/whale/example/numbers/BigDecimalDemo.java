package com.zoo.whale.example.numbers;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @Date: 2022年10月26日
 * @Time: 14时:44分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class BigDecimalDemo {

    public static void main(String[] args) {
        BigDecimal cumulativePercent = new BigDecimal("3.14");
        cumulativePercent = cumulativePercent.setScale(4, RoundingMode.HALF_UP).stripTrailingZeros();
        System.out.println(cumulativePercent);
    }
    
}
