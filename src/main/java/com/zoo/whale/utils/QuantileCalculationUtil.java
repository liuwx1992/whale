package com.zoo.whale.utils;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.List;

/**
 * 分位数计算工具类
 * @Date: 2023年06月01日
 * @Time: 18时:06分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class QuantileCalculationUtil {

    public static void main(String[] args) {
        quantileCal();
    }


    public static void quantileCal(){

        DescriptiveStatistics stats = new DescriptiveStatistics();

        List<Integer> list = new ArrayList<>();

        for (int i = 1;i <= 100;i++){
            stats.addValue(i);
        }

        Double dou = Double.parseDouble(QuantileCommonValueEnum.P50.getValue());

        double median = stats.getPercentile(dou);
        double NinePer = stats.getPercentile(95);
        double p99 = stats.getPercentile(99);

        System.out.println(p99);
        System.out.println(NinePer);
        System.out.println(median);

    }


}
