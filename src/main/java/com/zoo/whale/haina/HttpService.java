package com.zoo.whale.haina;

import com.zoo.whale.example.caffeine.CaffeineCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.zoo.whale.example.caffeine.CaffeineCache.getHost;

/**
 * @Date: 2024年 02月 20日
 * @Time:20时:15分
 * @Author: weixin.liu
 * @Version:V1.0
 */
@Slf4j
public class HttpService {

    public String sendThirdRequest(String url, String param) {
        String result;
        CloseableHttpResponse response = null;
        try {
            String isProxy = CaffeineCache.getCaffeineCache().getProxy(url);

            log.info("请求url:{},是否请求代理：{}", url, isProxy);

            String host = getHost(url);

            Map<String, Object> clientMap = HttpConnectionManager4.getHttpClient(isProxy, host);

            // 构建HttpMethod
            HttpUriRequest method = getMethod(url, param);

            //请求三方
            response = requestThird(clientMap, method, isProxy);

            //根据状态码判断结果解析处理
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 调用getEntity()方法获取到一个HttpEntity实例
                HttpEntity entity = response.getEntity();
                //结果解析处理
                result = resolveResult(response, entity);
            } else {
                result = resolveExceptionResult(response);
            }
        } catch (Exception ex) {
            log.warn("sendThirdRequest error:", ex);
            return "error result";
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error("关闭请求链接出错", e);
            }
        }
        return result;
    }

    private String resolveResult(CloseableHttpResponse response, HttpEntity entity) {
        String result = "{}";
        try {
            result = EntityUtils.toString(entity, "utf-8");
        } catch (Exception e) {
            result = "deal error result";
        }
        return result;
    }

    public CloseableHttpResponse requestThird(Map<String, Object> clientMap, HttpUriRequest method, String isProxy) throws IOException {
        CloseableHttpClient httpClient = (CloseableHttpClient) clientMap.get("client");
        if (StringUtils.equals("true", isProxy)) {
            HttpContext context = (HttpContext) clientMap.get("context");
            //执行三方查询
            return httpClient.execute(method, context);
        } else {
            return httpClient.execute(method);
        }
    }

    public static HttpRequestBase getMethod(String url, String jsonData) throws UnsupportedEncodingException {
        // Post方式
        HttpPost method = new HttpPost(url);
        // build param
        List<NameValuePair> data = new ArrayList<>();
        data.add(new BasicNameValuePair("jsonData", jsonData));
        method.setEntity(new UrlEncodedFormEntity(data, StandardCharsets.UTF_8.name()));
        return method;
    }

    /**
     * 异常返回结果处理
     */
    private String resolveExceptionResult(CloseableHttpResponse response) {
        //非200结果处理
        return "";
    }

}
