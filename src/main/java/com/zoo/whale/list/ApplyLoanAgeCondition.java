package com.zoo.whale.list;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Date: 2023年 06月 19日
 * @Time:20时:42分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class ApplyLoanAgeCondition {

    private String keyIn;

    List<String> contains;

    private String keyNotIn;

    List<String> notContains;


    public static final Map<String,List<String>> CONTAINS_MAP = new HashMap<>();

    static {
        CONTAINS_MAP.put("it", Arrays.asList("0"));
    }





    Map<String,List<String>> notContainsMap = new HashMap<>();


}
