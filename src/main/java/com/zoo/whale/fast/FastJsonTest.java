package com.zoo.whale.fast;

import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName FastJsonTest
 * @Description TODO
 * @Author weixin.liu
 * @Date 2026/5/14 18:10
 */
public class FastJsonTest {

    public static void main(String[] args) {
        JSONObject valueJson;
        try {
            String valueStr = "{\"score\":\"66\"}";
            valueJson = JSONObject.parseObject(valueStr);
        } catch (Exception e) {
            return;
        }
        if (valueJson.isEmpty()) {
            return;
        }
        System.out.println(valueJson);
    }

}
