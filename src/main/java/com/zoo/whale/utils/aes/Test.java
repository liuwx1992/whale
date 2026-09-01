package com.zoo.whale.utils.aes;


import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class Test {
    public static void main(String[] args) {
        //加密密钥
        String key = "qB2aI8fO3iC1dK0c";
        //接口调用凭证
        //String ticket = "3H7AHhVsSmLXCz79ckvKfsA2PKCNgfVEhaXryEM17FA";
        String ticket = "3H7AHhVsSmLXCz79ckvKfsA2PKCNgfVEhaXryEM17FA=";

        JSONObject person = new JSONObject();
        person.put("name", "张三");
        person.put("id", "430481202007211234");
        person.put("type", 3);
        //接口地址
        String apiUrl = "http://123.124.130.19/qysstest/qyss/fyss/queryLimitConsumption";
        int outTime = 10000;
        RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(outTime).setConnectTimeout(outTime).setConnectionRequestTimeout(outTime).build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
        HttpPost httpPost = new HttpPost(apiUrl);
        //添加header
        httpPost.addHeader("Content-type", "application/json; charset=utf-8");
        httpPost.addHeader("ticket", ticket);
        //添加body
        ByteArrayEntity entity = null;
        try {
            //请求的body信息，需要对参数进行aes加密
            String requestBody = TestAes.aesEncrypt(person.toJSONString(), key);
            entity = new ByteArrayEntity(requestBody.getBytes("UTF-8"));
            entity.setContentType("application/json");
        } catch (Exception e) {
            System.out.println("向服务器承保接口发起http请求,封装请求body时出现异常");
            throw new RuntimeException("向服务器承保接口发起http请求,封装请求body时出现异常", e);
        }
        httpPost.setEntity(entity);
        //执行post请求
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            String sss = EntityUtils.toString(response.getEntity());
            System.out.println(sss);
            //接收到返回结果需要进行aes解密
            System.out.println(TestAes.aesDecrypt(sss, key));

        } catch (ClientProtocolException e) {
            System.out.println("提交给服务器的请求，不符合HTTP协议");
            throw new RuntimeException("提交给服务器的请求，不符合HTTP协议", e);
        } catch (IOException e) {
            System.out.println("向服务器承保接口发起http请求,执行post请求异常");
            throw new RuntimeException("向服务器承保接口发起http请求,执行post请求异常", e);
        } catch (Exception e) {
            System.out.println("向服务器承保接口发起http请求,执行post请求异常");
            throw new RuntimeException("向服务器承保接口发起http请求,执行post请求异常", e);
        }
    }


}
