package com.zoo.whale.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Date: 2025年 11月 17日
 * @Time:14时:02分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class CacheFactory {

    public static final Map<String, String> globalLimitFlowProductMap = new ConcurrentHashMap<>();
    public static final Map<String, Double> productQpsLimitFlowFactorMap = new ConcurrentHashMap<>();


    static {
        globalLimitFlowProductMap.put("MidLoanRiskChar#V.0#6962", "20");
        productQpsLimitFlowFactorMap.put("MidLoanRiskChar#V.0#6962", 1.0);
    }


}
