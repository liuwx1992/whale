package com.zoo.whale.example.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Date: 2024年 02月 20日
 * @Time:19时:52分
 * @Author: weixin.liu
 * @Version:V1.0
 */
@Slf4j
public class CaffeineCache {

    private CaffeineCache() {
    }
    private static class CaffeineCacheHolder {
        private static final CaffeineCache instance = new CaffeineCache();
    }
    public static CaffeineCache getCaffeineCache() {
        return CaffeineCacheHolder.instance;
    }

    /**
     * 本地缓存
     * 保存后有效期1s
     * */
    private LoadingCache<String, String> cache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .maximumSize(10000)
            .build(key -> getValueFromRedis(key));

    /**
     * query from redis->mysql
     * if get value,save to caffeine
     * */
    private String getValueFromRedis(String url) {
        String cachedResult = "";
        if(StringUtils.isEmpty(url)){
            return cachedResult;
        }
        try {
            //从redis获取标识
            String domainFailure = "dac_domain_failure_nottl";
            String host = getHost(url);
            /*JedisCluster jedisCluster = JedisClusterUtil.getInstance().getJedisCluster();
            String status = jedisCluster.hget(domainFailure, host);*/
        } catch (Exception e){
            log.warn("getValueFromNext exception:",e);
        }
        //return 结果返回，并设置到caffeine
        return cachedResult;
    }

    /**
     * query caffeine->redis
     * @param url
     */
    public String getProxy(String url) {
        //goProxy 是否走代理0-本地，1-走代理
        String goProxy = "0";
        try {
            if (StringUtils.isEmpty(url)) {
                return goProxy;
            }
            String host = getHost(url);
            //获取本域名是否走代理
            goProxy = cache.get(host);
        } catch (Exception e) {
            log.warn("url:{},getGoProxy error:",url,e);
        }
        return goProxy;
    }

    private static final Pattern PATTERN = Pattern.compile("(?:https?://)((\\w+\\.){2,3}\\w+|[a-zA-z0-9]+)(?::[0-9]+)?");
    /**
     * 获取域名
     */
    public static String getHost(String url) {
        Matcher matcher = PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

}
