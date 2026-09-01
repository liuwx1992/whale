package com.zoo.whale.utils.ylzh2;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpsUtil {
    private static Logger LOGGER = LoggerFactory.getLogger(HttpsUtil.class);

    private static final int timeout = 120 * 1000;
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();

    private static X509TrustManager x509mgr = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] xcs, String string) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] xcs, String string) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };

    private static CloseableHttpClient getClient() {
        SSLConnectionSocketFactory sslCSF = null;
        try {
            // 信任所有
            SSLContext sslContext = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
            sslContext.init(null, new TrustManager[]{x509mgr}, null);
            sslCSF = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return HttpClients.custom().setSSLSocketFactory(sslCSF).build();
    }

    /**
     * @param url  请求url
     * @param json 请求参数 json报文
     */
    public static String doPost(String url, String json) {
        String res = null;
        URI uri = generateURL(url);
        HttpPost post = new HttpPost(uri);
        HttpEntity entity = null;
        try {
            post.setConfig(requestConfig);
            entity = new StringEntity(json);
            post.setEntity(entity);
            res = execute(post);
        } catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
            return res;
        }
        LOGGER.info("request: " + url + " response :" + (res.length() > 1000 ? "data过大，省略输出" : res));
        return res;
    }

    /**
     * @param url 请求接口url
     * @return URI对象
     * @Description: 生成请求的URI对象
     */
    private static URI generateURL(String url) {
        URI uri = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return uri;
    }

    /**
     * @param request 请求信息
     * @Description: 执行http请求并关闭资源（HttpPost 或 HttpGet）返回请求结果
     */
    public static String execute(HttpUriRequest request) {
        String responseStr = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = getClient().execute(request);
            responseStr = EntityUtils.toString(httpResponse.getEntity(), DEFAULT_CHARSET);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return responseStr;
    }

}
