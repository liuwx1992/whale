package com.zoo.whale.egress;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Date: 2022年10月27日
 * @Time: 20时:54分
 * @Author: weixin.liu
 * @Version: 1.0
 */
@Slf4j
@Service
public class EgressService {



    public String getData(String code,String jsonData){
        String result = "{}";
        try {
            //获取HttpClient
            CloseableHttpClient httpClient = HttpConnectionManager4.getHttpClient();

            // 构建HttpMethod
            HttpUriRequest method = HttpConnectionManager4.getMethod(code,jsonData);

            //请求三方
            CloseableHttpResponse response = httpClient.execute(method);

            //根据状态码判断结果解析处理
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 调用getEntity()方法获取到一个HttpEntity实例
                HttpEntity entity = response.getEntity();
                //结果解析处理
                result = EntityUtils.toString(entity, "utf-8");
                log.info("返回结果为: 【{}】", result);
            } else {
                String headers = Arrays.toString(response.getAllHeaders());
                log.warn("headers:",headers);
                result = "{\"DacRequestError\":\"HTTP request failure, Error code is " + response.getStatusLine().getStatusCode() + "!\"}";
                return result;
            }
        }catch (Exception e){
            log.warn("getData Exception:",e);
        }
        return result;
    }



}
