package com.zoo.whale.jdk;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2023年 06月 30日
 * @Time:14时:16分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class StringDemo {

    public static void main(String[] args) {
        List<String> codeList = new ArrayList<>();
        codeList.add("bd");
        codeList.add("bdjw");
        codeList.add("bds");

        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (String codeName : codeList) {
            builder.append(codeName).append(",");
        }
        builder.replace(builder.length()-1,builder.length(),")");
        System.out.println(builder.toString());
    }

}
