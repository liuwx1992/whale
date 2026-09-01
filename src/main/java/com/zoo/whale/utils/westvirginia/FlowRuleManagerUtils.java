package com.zoo.whale.utils.westvirginia;

import com.zoo.whale.cache.CacheFactory;
import com.zoo.whale.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

/**
 * @Date: 2025年 11月 17日
 * @Time:13时:58分
 * @Author: weixin.liu
 * @Version:V1.0
 */
@Slf4j
public class FlowRuleManagerUtils {

    public static final String HX_LIMITER_REDIS_PREFIX = "hx_global_limit";

    /**判断产品是否达到限流阈值*/
    public static boolean isCanPass(String mealNameAndVersion){
        //2.先incr，并获取全局qps
        double globalQpsLimit = getGlobalQpsLimit(mealNameAndVersion);
        //redis判断全局是否超限
        if (globalQpsPass(mealNameAndVersion, globalQpsLimit)) {
            //全局未超限
            return true;
        }
        return false;
    }

    /**
     * 获取全局qps限流阈值，读取配置的全局限流配置
     * */
    private static double getGlobalQpsLimit(String mealNameAndVersion) {
        double globalQps = 10;
        try {
            String threshValue =  CacheFactory.globalLimitFlowProductMap.get(mealNameAndVersion);
            globalQps = StringUtils.isNotBlank(threshValue) ? Double.parseDouble(threshValue) : 10;
        } catch (Exception e) {
            log.warn("getGlobalQpsLimit Error:", e);
        }
        return globalQps;
    }

    /**
     * 获取全局限流是否触发
     * */
    public static boolean globalQpsPass(String key, double limitPerSecond) {
        try {
            //做环境区分
            String runtimeEnv = "uat";
            //避免null
            runtimeEnv = StringUtils.isBlank(runtimeEnv) ? "default" : runtimeEnv;
            //全局限流系数
            Double factor = CacheFactory.productQpsLimitFlowFactorMap.get(runtimeEnv);
            if (factor != null && factor == -1) {
                //配置-1,则不做限流
                return true;
            }
            if (factor != null && factor > 0) {
                limitPerSecond = limitPerSecond * factor;
            }
            String redisKey = HX_LIMITER_REDIS_PREFIX + "_" + runtimeEnv + "_" + key;
            return RedisClient.isAllowed(redisKey, limitPerSecond);
        } catch (Exception e) {
            log.warn("globalQpsPass error", e);
        }
        //redis执行异常，返回false，继续做单pod限流
        return false;
    }

}
