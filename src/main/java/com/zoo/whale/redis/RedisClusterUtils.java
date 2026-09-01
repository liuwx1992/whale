package com.zoo.whale.redis;

import com.brgroup.redis.BrRedisClients;
import com.brgroup.redis.client.BrRedisClient;

public class RedisClusterUtils {
	private static final RedisClusterUtils INSTANCE = new RedisClusterUtils();

	public static RedisClusterUtils getInstance() {
		return INSTANCE;
	}

	public BrRedisClient<String, String> getRedisCluster1() {
		BrRedisClient<String, String> apiRedisClient;
		try {
			apiRedisClient = BrRedisClients.getRedisClient("redis1");
		}catch (Exception e){
			throw new RuntimeException(e);
		}
		return apiRedisClient;
	}

	public BrRedisClient<String, String> getRedisCluster2() {
		BrRedisClient<String, String> apiRedisClient;
		try {
			apiRedisClient = BrRedisClients.getRedisClient("redis2");
		}catch (Exception e){
			throw new RuntimeException(e);
		}
		return apiRedisClient;
	}

	public BrRedisClient<String, String> getZwWriteRedisCluster3() {
		BrRedisClient<String, String> apiRedisClient;
		try {
			apiRedisClient = BrRedisClients.getRedisClient("redis3");
		}catch (Exception e){
			throw new RuntimeException(e);
		}
		return apiRedisClient;
	}

	public BrRedisClient<String, String> getYzWriteRedisCluster4() {
		BrRedisClient<String, String> apiRedisClient;
		try {
			apiRedisClient = BrRedisClients.getRedisClient("redis4");
		}catch (Exception e){
			throw new RuntimeException(e);
		}
		return apiRedisClient;
	}

	/**
	 * 获取计数集群redis客户端，和redis1集群为同一集群
	 * 由于做限流，暂时修改了超时时间和重试次数
	 * */
	public BrRedisClient<String, String> getRedisCluster1Limier() {
		BrRedisClient<String, String> apiRedisClient;
		try {
			apiRedisClient = BrRedisClients.getRedisClient("redis1_limiter");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return apiRedisClient;
	}

}
