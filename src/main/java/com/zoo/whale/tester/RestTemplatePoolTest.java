package com.zoo.whale.tester;

import com.zoo.whale.config.DacResultBean;
import com.zoo.whale.utils.ice.SwiftNumberManager;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * @Date: 2023年05月05日
 * @Time: 18时:05分
 * @Author: weixin.liu
 * @Version: 1.0
 */
@Slf4j
public class RestTemplatePoolTest {

    @Autowired
    private RestTemplate restTemplate = new RestTemplate(httpRequestFactory());


    private static String apiCode = "xxxxxxx";

    private static String url = "http://haina.brapp.com/haina/v1/getData";


    @Test
    public void test() {
        queryHaiNaRet();
    }


    public void queryHaiNaRet() {
        String code = "ymzn_badinfoscore";
        String jsonData = getJsonData();
        send(code, jsonData);
    }

    public String getJsonData() {
        JSONObject json = new JSONObject();
        //client_type 替换为自己业务线对应的client_type，不清楚联系开发
        json.put("client_type", "xxxxx");
        json.put("swift_number", apiCode + "_".concat(SwiftNumberManager.getSwiftNumberManager().getSwiftNumber()));

        json.put("name", "张三");
        json.put("idCard", "320625190001011010");

        return json.toString();
    }

    public String send(String code, String jsonData) {
        try {
            //  封装参数，不要替换为Map与HashMap，否则参数无法传递
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

            // 入参
            params.add("jsonData", jsonData);
            params.add("code", code);

            HttpHeaders headers = new HttpHeaders();
            //  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

            // http 响应状态码
            log.info("海纳响应状态码:{}", responseEntity.getStatusCode());

            String resStr = responseEntity.getBody();

            // 响应结果
            log.info("the result is :" + resStr);

            JSONObject jsonObject = JSONObject.fromObject(resStr);

            //1、转成Bean方式
            DacResultBean dacResultBean = (DacResultBean)JSONObject.toBean(jsonObject, DacResultBean.class);
            String code1 = dacResultBean.getCode();
            String data1 = dacResultBean.getData();
            String message1 = dacResultBean.getMessage();
            String status1 = dacResultBean.getStatus();

            //2 直接从json取值
            String retCode = jsonObject.getString("code");
            String status = jsonObject.getString("status");
            String message = jsonObject.getString("message");
            String data = jsonObject.getString("data");

            return resStr;
        } catch (Exception e) {
            log.warn("调用海纳http服务异常: ", e);
        } finally {

        }
        return "";
    }

    public ClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    public HttpClient httpClient() {
        Registry registry = RegistryBuilder.create().register("http", PlainConnectionSocketFactory.getSocketFactory()).build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        //设置整个连接池最大连接数
        connectionManager.setMaxTotal(200);
        //路由是对maxTotal的细分
        connectionManager.setDefaultMaxPerRoute(200);

        //超时配置
        RequestConfig requestConfig = RequestConfig.custom()
                //读取数据的超时时间(可根据自己服务接口设定)
                .setSocketTimeout(5000)
                //连接服务器的超时时间(可根据自己服务接口设定)
                .setConnectTimeout(3000)
                //从连接池中获取连接的超时时间
                .setConnectionRequestTimeout(500)
                .build();

        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = (IOException exception, int executionCount, HttpContext context) -> {
            // 如果已经重试了3次，就放弃
            if (executionCount >= 3) {
                return false;
            }
            // 如果服务器丢掉了连接，可以重试
            if (exception instanceof org.apache.http.NoHttpResponseException) {
                return true;
            }

            // 连接超时异常，可以重试
            if (exception instanceof ConnectTimeoutException) {
                return true;
            }

            // 不要重试SSL握手异常
            if (exception instanceof SSLHandshakeException) {
                return false;
            }

            // 超时
            if (exception instanceof InterruptedIOException) {
                return false;
            }
            // 目标服务器不可达
            if (exception instanceof UnknownHostException) {
                return false;
            }
            // SSL握手异常
            if (exception instanceof SSLException) {
                return false;
            }

            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            // 如果请求是幂等的，就再次尝试
            if (!(request instanceof HttpEntityEnclosingRequest)) {
                return true;
            }
            return false;
        };

        CloseableHttpClient httpClient = HttpClientBuilder.create().
                setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                //允许空闲期
                .evictExpiredConnections().evictIdleConnections(30, TimeUnit.SECONDS)
                //重试策略
                .setRetryHandler(httpRequestRetryHandler)
                .build();
        return httpClient;
    }

}
