package com.zoo.whale.example.thread;

import com.alibaba.fastjson.JSONObject;

/**
 * @Date: 2023年03月12日
 * @Time: 21时:41分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class ConfigureTask implements Runnable{


    private String ss;

    public ConfigureTask(String ss){
        this.ss = ss;
    }

    @Override
    public void run() {
        JSONObject json = JSONObject.parseObject(ss);
        System.out.println("json长度:"+json.size());
    }
}
