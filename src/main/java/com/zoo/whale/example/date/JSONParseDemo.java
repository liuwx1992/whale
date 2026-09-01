package com.zoo.whale.example.date;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * @Date: 2023年06月01日
 * @Time: 14时:49分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class JSONParseDemo {

    public static void main(String[] args) {
        String dataValue = "{\"key\":5}";
        JSONObject jsonData = JSONObject.parseObject(dataValue);
        String hbaseDataValue = jsonData.getString("key");
        System.out.println(hbaseDataValue);


        DescriptiveStatistics stats = new DescriptiveStatistics();

    }


}
