package com.zoo.whale.haina;

import com.zoo.whale.utils.PropertiesUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.ChallengeState;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class HttpConnectionManager4New {
    private static final Logger log = LoggerFactory.getLogger(HttpConnectionManager4New.class);
    // 最大连接数
    private static final int MAX_TOTAL = 150;

    // 每个路由最大连接数MAXPERROUTE
    private static final int MAX_PER_ROUTE = 150;
    private final static Object SYNC_LOCK = new Object();
    private final static ConcurrentMap<String, PoolingHttpClientConnectionManager> MAP = new ConcurrentHashMap<>();
    private final static ConcurrentMap<String, Map<String, Object>> CLIENT_AND_CONTEXT_MAP = new ConcurrentHashMap<>();
    // 创建连接池
    private static PoolingHttpClientConnectionManager cm;
    private static CloseableHttpClient httpClient;
    private static CredentialsProvider credentialsProvider;

    /**
     * 连接池管理器
     * 每个code维护一个连接池
     */
    private static PoolingHttpClientConnectionManager getPool(String code) {
        if (MAP.get(code) == null) {
            synchronized (SYNC_LOCK) {
                if (MAP.get(code) == null) {
                    try {
                        HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
                        SSLConnectionSocketFactory sslsf;
                        if (code.contains("zjyd_")) {
                            sslsf = new SSLConnectionSocketFactory(
                                    createIgnoreVerifySSL(),
                                    new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"},
                                    new String[]{"TLS_RSA_WITH_AES_128_CBC_SHA256"},
                                    hostnameVerifier
                            );
                        } else if (StringUtils.equals("bd", code) || StringUtils.equals("bdjw", code) ||
                                StringUtils.equals("xz_badinfov2", code) || StringUtils.startsWith(code, "fh_") ||
                                StringUtils.startsWith(code, "sm_") || StringUtils.startsWith(code, "zx03_") ||
                                StringUtils.startsWith(code, "d01_") || StringUtils.startsWith(code, "p01_") ||
                                StringUtils.startsWith(code, "txsk_") || StringUtils.startsWith(code, "zsxy_")) {
                            sslsf = new SSLConnectionSocketFactory(createSSLConnectTlsV12(), hostnameVerifier);
                        } else {
                            sslsf = new SSLConnectionSocketFactory(createIgnoreVerifySSL(), hostnameVerifier);
                        }

                        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                                .register("http", PlainConnectionSocketFactory.INSTANCE)
                                .register("https", sslsf).build();

                        cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
                        cm.setMaxTotal(MAX_TOTAL);
                        cm.setDefaultMaxPerRoute(MAX_PER_ROUTE);
                        MAP.put(code, cm);
                        log.debug("{} 接口:PoolingNHttpClientConnectionManager请求池初始化……", code);
                    } catch (Exception e) {
                        log.error("getPool exception,code:{}", code, e);
                    }
                }
            }
        }

        return MAP.get(code);
    }

    /**
     * 绕过验证
     */
    public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = getX509TrustManager();

        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }

    /**
     * fh接口 TLSv1.2
     */
    public static SSLContext createSSLConnectTlsV12() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("TLSv1.2");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = getX509TrustManager();

        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }

    private static X509TrustManager getX509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
    }

    /**
     * 获取HttpClient
     */
    public static Map<String, Object> getHttpClient(String isProxy, String hostStr) {
        StringBuilder stringBuilder = new StringBuilder();
        // 需要保证一个三方源的连接池在使用代理时和不使用代理是要不同的连接池
        stringBuilder.append(hostStr).append("_").append(isProxy);
        String mapKey = stringBuilder.toString();

        if (CLIENT_AND_CONTEXT_MAP.get(mapKey) == null) {
            synchronized (SYNC_LOCK) {
                if (CLIENT_AND_CONTEXT_MAP.get(mapKey) == null) {
                    RequestConfig requestConfig;
                    RequestConfig.Builder builder = RequestConfig.custom().setConnectTimeout(2000).setConnectionRequestTimeout(100).setSocketTimeout(20000);
                    //setSoTimeout接收数据的等待超时时间，单位ms
                    SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(20000).build();
                    // 请求重试处理
                    HttpRequestRetryHandler httpRequestRetryHandler = (IOException exception, int executionCount, HttpContext context) -> {
                        ///根据自身情况定义不同场景的重试策略
                        return false;
                    };
                    // 声明重定向策略对象
                    LaxRedirectStrategy redirectStrategy = new LaxRedirectStrategy();
                    //代理认证
                    credentialsProvider = new BasicCredentialsProvider();
                    int proxyPort = PropertiesUtil.getIntegerValue("proxy_port");
                    String proxyHost = PropertiesUtil.getStringValue("proxy_host");
                    String proxyUsername = PropertiesUtil.getStringValue("proxy_username");
                    String proxyPassword = PropertiesUtil.getStringValue("proxy_password");
                    credentialsProvider.setCredentials(new AuthScope(proxyHost, proxyPort),
                            new UsernamePasswordCredentials(proxyUsername, proxyPassword));
                    HttpContext context = HttpClientContext.create();
                    if (StringUtils.equals("true", isProxy)) {
                        AuthScheme basicAuthScheme = new BasicScheme(ChallengeState.PROXY);
                        //抢先认证
                        HttpHost host = new HttpHost(proxyHost, proxyPort);
                        AuthCache authCache = new BasicAuthCache();
                        authCache.put(host, basicAuthScheme);
                        context.setAttribute(ClientContext.AUTH_CACHE, authCache);
                        requestConfig = builder.setProxy(host).build();
                    } else {
                        requestConfig = builder.build();
                    }
                    PoolingHttpClientConnectionManager connManager = getPool(hostStr);
                    if (StringUtils.equals("true", isProxy)) {
                        //设置整个过程的读超时时间,只有代理的时候增加该配置，其他情况由于会覆盖连接超时时间，请勿使用
                        connManager.setDefaultSocketConfig(socketConfig);
                    }
                    HttpClientBuilder clientBuilder = HttpClients.custom().setConnectionManager(connManager).setDefaultRequestConfig(requestConfig);

                    clientBuilder.setDefaultSocketConfig(socketConfig);
                    //30s的允许空闲期
                    clientBuilder.evictExpiredConnections().evictIdleConnections(30, TimeUnit.SECONDS);
                    clientBuilder.setRedirectStrategy(redirectStrategy).setRetryHandler(httpRequestRetryHandler);
                    if (StringUtils.equals("true", isProxy)) {
                        httpClient = clientBuilder.setDefaultCredentialsProvider(credentialsProvider).build();
                    } else {
                        httpClient = clientBuilder.build();
                    }
                    Map<String, Object> clientContextMap = new HashMap<>();
                    clientContextMap.put("client", httpClient);
                    clientContextMap.put("context", context);
                    CLIENT_AND_CONTEXT_MAP.put(mapKey, clientContextMap);
                }
            }
        }
        return CLIENT_AND_CONTEXT_MAP.get(mapKey);
    }

    private static String getTimeType(String host) {
        //获取接口快慢类型
        return "";
    }

}
