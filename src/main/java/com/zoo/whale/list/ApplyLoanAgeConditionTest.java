package com.zoo.whale.list;

import java.math.BigDecimal;

/**
 * @Date: 2023年 06月 20日
 * @Time:10时:14分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class ApplyLoanAgeConditionTest {

    public static void main(String[] args) {
        //System.out.println(ApplyLoanAgeCondition.CONTAINS_MAP.get("it").contains("0"));


        BigDecimal total = new BigDecimal("1017");

        int va0_1 = 795 + 19 + 16 + 14 + 20;
        BigDecimal De0_1 = new BigDecimal(va0_1);

        int va1_2 = 36 + 32;
        BigDecimal De1_2 = new BigDecimal(va1_2);

        int va2_3 = 47;
        BigDecimal De2_3 = new BigDecimal(va2_3);

        int va2_5_3 = 38;
        BigDecimal De2_3_ = new BigDecimal(va2_5_3);

        BigDecimal p1 = De0_1.divide(total,4,BigDecimal.ROUND_HALF_UP);
        BigDecimal p2 = De1_2.divide(total,4,BigDecimal.ROUND_HALF_UP);
        BigDecimal p3 = De2_3.divide(total,4,BigDecimal.ROUND_HALF_UP);
        BigDecimal p4 = De2_3_.divide(total,4,BigDecimal.ROUND_HALF_UP);

        System.out.println(va0_1);
        System.out.println(va1_2);
        System.out.println(va2_3);
        System.out.println(va0_1 + va1_2 + va2_3);


        System.out.println(p1 + "%");
        System.out.println(p2 + "%");
        System.out.println(p3+ "%");
        System.out.println(p4+ "%");



    }

}
