package com.zoo.whale.example.json;

import com.alibaba.fastjson.JSONObject;

/**
 * @Date: 2022年12月07日
 * @Time: 20时:27分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class JsonDemo {
    public static void main(String[] args) {
        String s = "{\"a\":\"a\",\"b\":\"\",\"c\":null}";
        JSONObject jsonObject = JSONObject.parseObject(s);
        System.out.println(jsonObject.size());
        System.out.println(jsonObject.toJSONString());
        net.sf.json.JSONObject jsonObject1 = net.sf.json.JSONObject.fromObject(s);
        System.out.println(jsonObject1.size());
        System.out.println(jsonObject1.toString());
    }
}
