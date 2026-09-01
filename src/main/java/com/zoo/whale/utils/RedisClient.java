package com.zoo.whale.utils;

import com.brgroup.redis.client.BrRedisClient;
import io.lettuce.core.ScriptOutputType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Date: 2025年 06月 18日
 * @Time:16时:38分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class RedisClient {

    private static final Logger logger = LoggerFactory.getLogger(RedisClient.class);

    /**
     * redis客户端初始化
     */
    public static int init() {
        int flag = -1;
        try {
            RedisClusterUtils.getInstance().getRedisCluster1();
            RedisClusterUtils.getInstance().getRedisCluster2();
            RedisClusterUtils.getInstance().getZwWriteRedisCluster3();
            RedisClusterUtils.getInstance().getYzWriteRedisCluster4();
            logger.warn("缓存初始化完成！");
            flag = 0;
        } catch (Exception e) {
            logger.error("缓存初始化失败！error :", e);
        }
        return flag;
    }

    public static BrRedisClient<String, String> getRedisCluster1() {
        return RedisClusterUtils.getInstance().getRedisCluster1();
    }

    /**
     * 请求次数增加
     * */
    public static boolean incrRedisKey(String key) {
        String script = "if redis.call('exists', KEYS[1]) == 0 then" +
                "    redis.call('set', KEYS[1], 1)" +
                "    redis.call('expire', KEYS[1], tonumber(ARGV[1]))" +
                "    return 1 " +
                "else" +
                "    return redis.call('incr', KEYS[1]) " +
                "end";

        Object result = getRedisCluster1().eval(script,
                ScriptOutputType.INTEGER,
                new String[]{key},
                "1");
        if(result == null || StringUtils.isBlank(result.toString())){
            return false;
        }
        return Long.parseLong(result.toString()) >= 1;
    }

    /**
     * 是否到达全局限流qps
     * */
    public static boolean isAllowed(String redisKey, double limit) {
        // 设置1秒过期时间
        String script =
                "local current = redis.call('incr', KEYS[1]) " +
                        "if tonumber(current) == 1 then " +
                        "    redis.call('expire', KEYS[1], 1) " +
                        "end " +
                        "if tonumber(current) <= tonumber(ARGV[1]) then " +
                        "    return 1 " +
                        "else " +
                        "    return 0 " +
                        "end";

        Object result = getRedisCluster1().eval(
                script,
                ScriptOutputType.INTEGER,
                new String[]{redisKey},
                String.valueOf(limit)
        );

        logger.warn("result:{}",result);

        return "1".equals(result.toString());
    }


}
