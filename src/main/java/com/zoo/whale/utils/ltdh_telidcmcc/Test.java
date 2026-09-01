package com.zoo.whale.utils.ltdh_telidcmcc;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @ClassName:Test
 * @Description:
 * @Author:weixin.liu
 * @Date:2021/7/12
 * @Time:15:00
 * @Version；1.0
 */
public class Test {

    public static void main(String[] args) throws Exception {
        //String strUrl = "https://testapi.goodsdatas.com:8880/credit/YD2ElementsD";  //url
        String strUrl = "https://api.goodsdatas.com:8443/credit/YD2ElementsD";  //url
        String strKey = "b7cd1aff0cc55b2f78f35454a623f9a1";    //apiKey
        String strRettype = "json";
        String strparam = "";
        DesEncrypter desEncrypter = new DesEncrypter("b7cd1aff0cc55b2f78f35454a623f9a1");
        String str = "idCard=21010219800706102x&mobile=13840786095";
        strparam = desEncrypter.encrypt(str);
        String url = strUrl + "/" + strKey;
        Map<String, String> bodyMap = new HashMap<String, String>();
        bodyMap.put("rettype", strRettype);
        bodyMap.put("encryptParam", strparam);
        try {
            String res = readByPost(url, bodyMap);
            System.out.println(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readByPost(String inUrl, Map<String, String> bodyMap) throws IOException {
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        PostMethod postMethod = new PostMethod();
        if (inUrl.startsWith("https")) {
            Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);
            Protocol.registerProtocol("https", myhttps);
        }
        try {

            postMethod.setURI(new URI(inUrl, false));
            postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler(0, false));
            Iterator<Entry<String, String>> iterator = bodyMap.entrySet().iterator();
            StringBuffer logRequestParam = new StringBuffer();
            while (iterator.hasNext()) {
                Entry<String, String> entry = (Entry<String, String>) iterator.next();
                String parameterName = (String) entry.getKey();
                String parameterValue = (String) entry.getValue();
                postMethod.setParameter(parameterName, parameterValue);
                logRequestParam.append(parameterName + "=" + parameterValue + "&");
            }
            logRequestParam.deleteCharAt(logRequestParam.length() - 1);
            System.out.println("请求参数：——》》" + logRequestParam);
            int statusCode = httpClient.executeMethod(postMethod);
            System.out.println("statusCode============" + statusCode);
            byte[] responseByte = postMethod.getResponseBody();
            String responseMsg = new String(responseByte, 0, responseByte.length, "utf-8");
            //System.out.println("postToWSO2，返回参数：——》》" + responseMsg);
            return responseMsg;
        } catch (URIException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
