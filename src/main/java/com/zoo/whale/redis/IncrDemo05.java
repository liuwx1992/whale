package com.zoo.whale.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * @Date: 2023年 12月 22日
 * @Time:14时:56分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class IncrDemo05 implements Runnable{
    public static void main(String[] args) throws InterruptedException {
        RedisClient client = RedisClient.create("redis://10.100.123.157:7362");

        StatefulRedisConnection<String, String> connection = client.connect();

        RedisCommands<String, String> commands = connection.sync();

        commands.set("key", "0");

        while (true) {
            Long value = commands.incr("key");
            if (value <= 2) {
                //fetch data
                Thread.currentThread().sleep(3000);

                System.out.println(Thread.currentThread().getName() + "取数完成...");
                //置零
                commands.set("key", "0");
                //结束任务
                break;
            } else {
                Thread.sleep(5000);
            }
        }
        System.out.println(Thread.currentThread().getName() + "取数任务结束...");
    }

    private void fetchData() throws Exception{
        RedisClient client = RedisClient.create("redis://10.100.123.157:7362");

        StatefulRedisConnection<String, String> connection = client.connect();

        RedisCommands<String, String> commands = connection.sync();

        commands.set("key", "0");

        while (true) {
            Long value = commands.incr("key");
            if (value <= 2) {
                //fetch data
                Thread.currentThread().sleep(3000);

                System.out.println(Thread.currentThread().getName() + "取数完成...");
                //置零
                commands.set("key", "0");
                //结束任务
                break;
            } else {
                Thread.sleep(5000);
            }
        }
        //System.out.println(Thread.currentThread().getName() + "取数任务结束...");
    }

    @Override
    public void run() {
        try {
            fetchData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
