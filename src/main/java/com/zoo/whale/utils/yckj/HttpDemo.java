package com.zoo.whale.utils.yckj;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * @Date: 2022年08月08日
 * @Time: 11时:05分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class HttpDemo {
    private static final String API_KEY = "1gz4u8t1zfavrwqei5m7";
    private static final String SHARED_SECRET = "z$0Hv$IXrK3S8N+jD@WjbBWlNX32-xxcSKM7s9k9";
    private static final String BASE_URL = "https://api-client.hlxy.com";

    public static void main(String[] args) throws Exception {
        //FYLT_BatchDataRun("D:/测试数据/ymzn_alriskcontrolv4.txt");
        //httpGetDemo();
        //httpPostDemo();
        getApiToken();
    }

    private static String getApiToken() throws Exception {
        // 1. 获取SSL的client实例
        CloseableHttpClient httpClient = createSSLClientDefault();
        // 2. 创建httpPost实例
        URI url = new URI(BASE_URL + "/api/v2/sc/token");
        HttpPost post = new HttpPost(url);
        try {
            // 3. 设置请求头
            post.addHeader("Accept-Charset", "utf-8");
            post.addHeader("Content-Type", "application/json");

            // 4. 将参数设置到entity对象中
            Map<String, Object> body = new HashMap<>();
            body.put("sharedSecret", SHARED_SECRET);
            body.put("apiKey", API_KEY);
            StringEntity se = new StringEntity(new JSONObject(body).toString(), "UTF-8");
            se.setContentType("text/json");
            se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));

            // 5. 将entity对象设置到httpPost对象中
            post.setEntity(se);

            // 6. 发送请求
            CloseableHttpResponse response = httpClient.execute(post);

            // 7. 在这里处理返回值
            Map data = new Gson().fromJson(EntityUtils.toString(response.getEntity()), Map.class);
            System.out.println("response status:" + response.getStatusLine().getStatusCode());
            System.out.println("response data:" + data);
            return (String) ((Map) data.get("data")).get("sc-api-token");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static void httpGetDemo() throws UnsupportedEncodingException, URISyntaxException {
        // 1. 获取SSL的client实例
        CloseableHttpClient httpClient = createSSLClientDefault();

        // 2. 创建httpGet实例
        URI url = new URI(BASE_URL + "/api/dbc/hg/cus/list?orderNo="
                + URLEncoder.encode("TEST_330879541493100", "UTF-8"));
        HttpGet get = new HttpGet(url);

        try {
            // 3. 获取token
            String token = getApiToken();

            // 4. 设置请求头
            get.addHeader("Content-Type", "application/json");
            get.addHeader("Accept-Charset", "utf-8");
            get.addHeader("sc-api-token", token);

            // 5. 发送请求
            CloseableHttpResponse response = httpClient.execute(get);

            // 6. 在这里处理返回值
            System.out.println("response status:" + response.getStatusLine().getStatusCode());
            System.out.println("response data:" + EntityUtils.toString(response.getEntity()));
            System.out.println("response header:");
            Header[] allHeaders = response.getAllHeaders();
            for (int i = 0; i < allHeaders.length; i++) {
                System.out.println(allHeaders[i].getName() + ":" + allHeaders[i].getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void httpPostDemo() throws URISyntaxException {
        // 1. 获取SSL的client实例
        CloseableHttpClient httpClient = createSSLClientDefault();

        // 2. 创建httpPost实例
        URI url = new URI(BASE_URL + "/api/dbc/hg/cus/list");
        HttpPost post = new HttpPost(url);

        try {
            // 3. 获取token
            String token = getApiToken();

            // 4. 设置请求头
            post.addHeader("Accept-Charset", "utf-8");
            post.addHeader("Content-Type", "application/json");
            post.addHeader("sc-api-token", token);

            // 5. 将参数设置到entity对象中
            Map<String, Object> body = new HashMap<>();
            body.put("idCard", "500223198801014444");
            body.put("name", "张三");
            StringEntity se = new StringEntity(new JSONObject(body).toString(), "UTF-8");
            se.setContentType("text/json");
            se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));

            // 6. 将entity对象设置到httpPost对象中
            post.setEntity(se);

            // 7. 发送请求
            CloseableHttpResponse response = httpClient.execute(post);

            // 8. 在这里处理返回值
            System.out.println("response status:" + response.getStatusLine().getStatusCode());
            System.out.println("response data:" + EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void FYLT_BatchDataRun(String filePath) throws Exception {
        BufferedOutputStream buffer = new BufferedOutputStream(new FileOutputStream(new File("D:/测试数据/ymzn_alriskcontrolv4.txt"), true));
        try {
            String encoding = "UTF-8";// 乱码就换成UTF-8
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String s = null;
                System.out.println("12313232");
                String[] a;
                FileWriter fileWriter = new FileWriter("D:/测试结果/yc.txt");
                while ((s = bufferedReader.readLine()) != null) {
                    a = s.split("\\,");
                    // 1. 获取SSL的client实例
                    CloseableHttpClient httpClient = createSSLClientDefault();

                    // 2. 创建httpGet实例
                    URI url = new URI(BASE_URL + "/api/v2/hg/splQuery/detail?orderNo="
                            + URLEncoder.encode("TEST_330879541493100", "UTF-8"));
                    HttpGet get = new HttpGet(url);

                    try {
                        // 3. 获取token
                        String token = getApiToken();

                        // 4. 设置请求头
                        get.addHeader("Content-Type", "application/json");
                        get.addHeader("Accept-Charset", "utf-8");
                        get.addHeader("sc-api-token", token);

                        // 5. 发送请求
                        long start = System.currentTimeMillis();
                        CloseableHttpResponse response = httpClient.execute(get);
                        long end = System.currentTimeMillis();

                        // 6. 在这里处理返回值
                        System.out.println("response status:" + response.getStatusLine().getStatusCode());
                        System.out.println("response data:" + EntityUtils.toString(response.getEntity()));
                        System.out.println("response header:");
                        System.out.println("耗时:" + (end - start) + "ms");
                        //						fileWriter.write("response data:" + EntityUtils.toString(response.getEntity())+"耗时:"+(end-start)+"ms");
                        fileWriter.write("\r\n");
                        fileWriter.flush();
                        Header[] allHeaders = response.getAllHeaders();
                        for (int i = 0; i < allHeaders.length; i++) {
                            System.out.println(allHeaders[i].getName() + ":" + allHeaders[i].getValue());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {

                            httpClient.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
                fileWriter.close();
                read.close();

            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        } finally {
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 添加证书信任
     */
    public static CloseableHttpClient createSSLClientDefault() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                //信任所有
                public boolean isTrusted(X509Certificate[] chain, String authType) {
                    return true;
                }
            }).build();

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
            return HttpClients.custom()
                    .setUserAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:102.0) Gecko/20100101 Firefox/102.0")
                    .setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }
}
