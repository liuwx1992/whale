package com.zoo.whale.example.test;

import com.zoo.whale.example.enums.CpuCategoryEnum;

/**
 * @Date: 2023年04月06日
 * @Time: 11时:10分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class EnumTest {

    public static void main(String[] args) {
        String name = "Ryzen";
        String detail = CpuCategoryEnum.getDetail(name);
        System.out.println(detail);
    }

}
