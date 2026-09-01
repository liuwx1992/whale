package com.zoo.whale.example.date;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeSet;

/**
 * @Date: 2023年04月17日
 * @Time: 14时:31分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class DateDemo {



    public static void main(String[] args) throws ParseException {
        /*String pattern = "yyyyMMdd";

        TreeSet<Long> timeSet = new TreeSet<>();

        Date parse = getSdf(pattern).parse("20230417");
        Date parse2 = getSdf(pattern).parse("20210417");
        Date parse3 = getSdf(pattern).parse("20220417");
        //System.out.println(parse.getTime());
        timeSet.add(parse.getTime());
        timeSet.add(parse2.getTime());
        timeSet.add(parse3.getTime());

        System.out.println(timeSet.first());
        System.out.println(timeSet.last());*/

        /*String ss = "source#date#apicode#cid#strategyId#日志加密关联人id";
        String[] split = ss.split("#");
        System.out.println(split.length);*/

        String value = "4.44515";
        BigDecimal avgDecimal = new BigDecimal(value);
        String result = avgDecimal.setScale(0, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
        System.out.println(result);
    }

    private static SimpleDateFormat getSdf(final String pattern) {
        return new SimpleDateFormat(pattern);
    }


}
