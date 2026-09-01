package com.zoo.whale.utils;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Date: 2023年05月06日
 * @Time: 11时:45分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class HttpClientUtils {

    public static ClientHttpRequestFactory getClientHttpRequestFactory() {
        // 长连接保持30秒
        PoolingHttpClientConnectionManager pollingConnectionManager = new PoolingHttpClientConnectionManager(60,
                TimeUnit.SECONDS);
        // 总连接数
        pollingConnectionManager.setMaxTotal(PropertiesUtil.getIntegerValue("PConnection_MaxTotal"));
        // 同路由的并发数
        pollingConnectionManager.setDefaultMaxPerRoute(PropertiesUtil.getIntegerValue("PConnection_MaxTotal"));

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setConnectionManager(pollingConnectionManager);
        // 重试次数，默认是3次，没有开启
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(2, true));
        // 保持长连接配置，需要在头添加Keep-Alive
        httpClientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());

        // RequestConfig.Builder builder = RequestConfig.custom();
        // builder.setConnectionRequestTimeout(200);
        // builder.setConnectTimeout(5000);
        // builder.setSocketTimeout(5000);
        //
        // RequestConfig requestConfig = builder.build();
        // httpClientBuilder.setDefaultRequestConfig(requestConfig);

        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.16 Safari/537.36"));
        headers.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
        headers.add(new BasicHeader("Accept-Language", "zh-CN"));
        headers.add(new BasicHeader("Connection", PropertiesUtil.getStringValue("PConnection_Keep-Alive")));
        headers.add(new BasicHeader("Keep-Alive", "120"));

        httpClientBuilder.setDefaultHeaders(headers);

        HttpClient httpClient = httpClientBuilder.build();
        // httpClient连接配置，底层是配置RequestConfig
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                httpClient);
        // 连接超时
        clientHttpRequestFactory.setConnectTimeout(PropertiesUtil.getIntegerValue("PConnectTimeout"));
        // 数据读取超时时间，即SocketTimeout
        clientHttpRequestFactory.setReadTimeout(PropertiesUtil.getIntegerValue("PReadTimeout"));
        // 连接不够用的等待时间，不宜过长，必须设置，比如连接不够用时，时间过长将是disabledInterface.properties灾难性的
        clientHttpRequestFactory.setConnectionRequestTimeout(5000);
        // 缓冲请求数据，默认值是true。通过POST或者PUT大量发送数据时，建议将此属性更改为false，以免耗尽内存。
        clientHttpRequestFactory.setBufferRequestBody(PropertiesUtil.getBooleanValue("PBufferRequestBody"));
        //DiscoveryClient discoveryClient = ApplicationContextService.getBean(DiscoveryClient.class);

        return clientHttpRequestFactory;
    }

}
