package com.zoo.whale.example.bus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2022年12月06日
 * @Time: 15时:01分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class CalAvgDemo {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        //'87717', '93899', '92548', '69654', '1143', '17677', '98644', '75408', '1229', '17152
        list.add(87717);
        list.add(93899);
        list.add(92548);
        list.add(69654);
        list.add(1143);
        list.add(17677);
        list.add(98644);
        list.add(75408);
        list.add(1229);
        list.add(17152);
        String meanValue = getMeanValue(list);
        String meanValue2 = getDecimalMeanValue(list);
        System.out.println(meanValue);
        System.out.println(meanValue2);
        System.out.println((float)555071/10);
    }

    private static String getDecimalMeanValue(List<Integer> numbers) {
        if (numbers != null && numbers.size() > 0) {
            int size = numbers.size();
            int sum = 0;
            for (Integer num : numbers) {
                sum += num;
            }
            System.out.println("sum:"+sum);
            BigDecimal a = new BigDecimal(sum);
            BigDecimal c = a.divide(new BigDecimal(numbers.size()),4, RoundingMode.HALF_UP);
            return c.toString();
        }
        return null;
    }

    private static String getMeanValue(List<Integer> numbers) {
        if (numbers != null && numbers.size() > 0) {
            int size = numbers.size();
            int sum = 0;
            for (Integer num : numbers) {
                sum += num;
            }
            return avgFormat(sum, size, 3);
        }
        return null;
    }

    protected static String avgFormat(int dividend, int divisor, int offset){
        if(divisor != 0){
            float temp = (float)dividend/divisor;
            //double temp = (double)dividend/divisor;
            System.out.println("temp:"+temp);
            String formatStr = "0.";
            for(int i=1;i<=offset;i++){
                formatStr += "0";
            }
            DecimalFormat df = new DecimalFormat(formatStr);
            double re = (double) temp;
            System.out.println(re);
            return df.format(temp);
        }
        return null;
    }
}
