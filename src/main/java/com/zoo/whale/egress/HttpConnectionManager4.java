package com.zoo.whale.egress;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class HttpConnectionManager4 {
	private static final Logger log = LoggerFactory.getLogger(HttpConnectionManager4.class);
	// 最大连接数
	private static final int MAX_TOTAL = 100;
	// 每个路由最大连接数MAXPERROUTE
	private static final int MAX_PER_ROUTE = 100;
	private final static Object SYNC_LOCK = new Object();
	private final static ConcurrentMap<String, Object> CLIENT_MAP = new ConcurrentHashMap<>();
    // 创建连接池
	private static PoolingHttpClientConnectionManager cm;
	private static CloseableHttpClient httpClient;

	private static final String HN_URL = "http://haina.brapp.com/haina/v1/getData";

	/**
	 * 连接池管理器
	 * 每个code维护一个连接池
	 * */
	private static PoolingHttpClientConnectionManager getPool() {
		try {
			HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(createIgnoreVerifySSL(), hostnameVerifier);
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
								.register("http", PlainConnectionSocketFactory.INSTANCE)
								.register("https", sslsf).build();

			cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			cm.setMaxTotal(MAX_TOTAL);
			cm.setDefaultMaxPerRoute(MAX_PER_ROUTE);

			log.debug("haina接口:PoolingNHttpClientConnectionManager请求池初始化……");
		} catch (Exception e) {
			log.error("getPool exception",e);
		}
		return cm;
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
	 * */
	public static CloseableHttpClient getHttpClient() {
		if (CLIENT_MAP.get("client") == null) {
			synchronized (SYNC_LOCK) {
				if (CLIENT_MAP.get("client") == null) {
					// connectTimeOut：指与服务端建立连接的超时时间，单位毫秒;
					// setConnectionRequestTimeout指从连接池获取到连接的超时时间,单位ms;
					// setSocketTimeout指的读超时时间（等待数据超时时间）超时时间，单位ms
					RequestConfig.Builder builder = RequestConfig.custom().setConnectTimeout(2000).setConnectionRequestTimeout(100).setSocketTimeout(20000);
					//setSoTimeout接收数据的等待超时时间，单位ms
					SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(20000).build();

					// 请求重试处理
					HttpRequestRetryHandler httpRequestRetryHandler = (IOException exception, int executionCount, HttpContext context) -> {
						// 如果已经重试了3次，就放弃
						if (executionCount >= 3) {
							return false;
						}
						// 如果服务器丢掉了连接，那么就重试
						if (exception instanceof org.apache.http.NoHttpResponseException) {
							return true;
						}
						// 不要重试SSL握手异常
						if (exception instanceof SSLHandshakeException) {
							return false;
						}

						// 连接超时异常重试,ConnectTimeoutException
						if (exception instanceof ConnectTimeoutException) {
							return true;
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
					// 声明重定向策略对象
					LaxRedirectStrategy redirectStrategy = new LaxRedirectStrategy();

					RequestConfig requestConfig = builder.build();

					PoolingHttpClientConnectionManager connManager = getPool();

					HttpClientBuilder clientBuilder = HttpClients.custom().setConnectionManager(connManager).setDefaultRequestConfig(requestConfig);

					clientBuilder.setDefaultSocketConfig(socketConfig);
					//30s的允许空闲期
					clientBuilder.evictExpiredConnections().evictIdleConnections(86400, TimeUnit.SECONDS);
					clientBuilder.setRedirectStrategy(redirectStrategy).setRetryHandler(httpRequestRetryHandler);

					httpClient = clientBuilder.build();

					CLIENT_MAP.put("client", httpClient);
				}
			}
		}
		return (CloseableHttpClient) CLIENT_MAP.get("client");
	}


	public static HttpRequestBase getMethod(String code, String jsonData) throws UnsupportedEncodingException {
		// Post方式
		HttpPost method = new HttpPost(HN_URL);
		// build param
		List<NameValuePair> data = new ArrayList<>();
		data.add(new BasicNameValuePair("code", code));
		data.add(new BasicNameValuePair("jsonData", jsonData));
		method.setEntity(new UrlEncodedFormEntity(data, StandardCharsets.UTF_8.name()));
		return method;
	}

}
