package com.zoo.whale.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zoo.whale.utils.ice.JsonProcessUtil;

import java.util.Iterator;
import java.util.Map;

/**
 * @Date: 2025年 07月 23日
 * @Time:20时:46分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class JsonUtils {

    private static final ObjectMapper mapperInstance = JsonProcessUtil.getMapperInstance();


    public static void main(String[] args) throws JsonProcessingException {
        String ss = "{\"$1\":\"754\",\"$2\":\"452\"}";
        ObjectNode json = (ObjectNode)mapperInstance.readTree(ss);
        Iterator<Map.Entry<String, JsonNode>> fields = json.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> next = fields.next();
            System.out.println(next);
        }
    }

}
