package com.zoo.whale.redis;

import com.brgroup.redis.client.BrRedisClient;
import io.lettuce.core.ScriptOutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
            RedisClusterUtils.getInstance().getRedisCluster1Limier();
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
     * 限流计数集群redis客户端
     */
    public static BrRedisClient<String, String> getRedisCluster1Limier() {
        return RedisClusterUtils.getInstance().getRedisCluster1Limier();
    }

    /**
     * 是否到达全局限流qps
     */
    public static boolean isAllowed(String redisKey, double limit) {
        // 设置1秒过期时间
        String script =
                "local current = redis.call('incr', KEYS[1]) " +
                        "if tonumber(current) == 1 then " +
                        "    redis.call('expire', KEYS[1], 1) " +
                        "end " +
                        "if tonumber(current) <= tonumber(ARGV[1]) then " +
                        "    return {1, current} " +
                        "else " +
                        "    return  {0, current} " +
                        "end";

        Object result = getRedisCluster1Limier().eval(
                script,
                ScriptOutputType.MULTI,
                new String[]{redisKey},
                String.valueOf(limit)
        );

        List<Object> list = (List<Object>) result;
        int allowed = Integer.parseInt(list.get(0).toString());
        long current = Long.parseLong(list.get(1).toString());

        logger.warn("当前限流阈值{}", current);

        return "1".equals(String.valueOf(allowed));
    }
}
