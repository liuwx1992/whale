package com.zoo.whale.utils.ylzh2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Demo {

    private static Logger LOGGER = LoggerFactory.getLogger(Demo.class);
    private static String url = "https://warcraft-test.unionpaysmart.com/";
    private static String a_uri = "/index/personal/v3";
    private static String masterodin_account = "T4010045";
    private static String cusPrivateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC2CMIFdQ2yvXSKLCToQZEiiW60HKaz5bC6TmYgYXSRonST84IdL5um4LIsifQm69dxX71X0i9gAFxELDqJbf6Wh5XIzqoF7YIQpq42NluhHQA3yQDc5A/80D6u4bxtroo1muCfBIyFh/IbjsMV42kiFl4zJjpjLby24cjVUmFeeTtpAimNQ9Xm8KQjNk+SxUR7QjmWGqzFvTcKn8SYHjSwsiPDN+/OILu8jNRx8J/6Aw4Y8pirYqYnFmOC4WPaKeeQygq9zn3SM97VHLTVFskpV8Xa2xRSYkNh9fhzo0BMLCtAREbOhkFsLMIVUCYGcttfAWTlm7CdhsnXrcVfM6xLAgMBAAECggEBAK6KUTcYWG0U+YRu9Wh5Tec2rZ4nhrQhvcW13HiZ2UK26V7fctf3SSDGfDD2idMz+ZystwlY8LH1RoLbgbk6bYz6LmWfuyj55ed/WrWCg3NFMmtK4Pfr+WRobGIU8i6c18q0WUJAA1kZ08wWo/n91lWn6uTSd9OXDEay0XWxiskghB9+hsnHn8vp4/KzsjjutIBAVHvE3h7nECrGclGFygkMJfhLJYlEttRZDj4LJflZEMBMD/BhocDamL5e1qOrHz/b3IbpHwlcjMrRqYOsy9axZZhlXbMmRwt4/fdS9vb9ZUz2/R9Cb3LZkq+5Xa08IrbI1XtqIKO+NRuLrV1gPrkCgYEA/BJve3+kYPgouEJWfzMiA1G0S8cy6LKXsin/c4e1cS7MC/3R6ZQs7wrHj4Foi529a2Dl2m2jIAIxEKvpzZ+rIR7zFtdRSKOt+BTmfonPYeVbuuagNXXbiXtxqmZQGIbIOBXILSfw5G1ph77dye0u8et6AlcKVsukSsgtejOZMk0CgYEAuN7tjR1NOQHr4d7Yz6yklsmtAG38Wg9/6iOYObqZpOm+9G2dFGoB0DwArkH5tnzPY+K6jy+Rkd//G+UmbKkP2e0ZZo2aHtWbk/aqcG5tYuiX/ZnBwtwBQZQonJymKwjQ7f6k8d437fKSyoMML0RbEGoXaaT88UJ0mW2IIuIetPcCgYBDWRGcJ4BdwC/w+xhtJ9ZAmMXZMUGy1nSdT4dG8M1TjjNKSOiaxdIzk+e5udOa6exAEkWXVI+u76b4U1ZGcBCi6sf8JE89Xy4OCFeFG4DjIQqfJxtK7VBYrTizVF62DtzTz04Kco90GJ+He0F6v/o4/Lc+hM7oXcRpjo5IBh06SQKBgBEq8ph2LiOrIAKbtTqj56zxEZXGoPakFdbic1J5F6eO5WLBgyLQLVbvG4kTK2Nc981o0rZVToljacdnYkgY8xXjxMYXREa0w3vcHjvOpW3X97VfP/UehSdX4OWaTnwUl4JicWLcpgj8y9DuloUVLfe6Qm5XGf41k6pj146VvRBFAoGBAMFDqnzs7Fs96EYqCEXxQmH7GvwahiZ0BSBI6ymK3hooApKAaCZ0/oJQRaFZAOC4xLblcLSWVzUPnyX+iN1oqrogAid9/pvnBKzA3aiXH6TebdeSKVdAH/q4y16rloUAH/9l58zGHTHinF0Dg81pE5I27SBGlESiQAaaM68XqM6n";
    private static String upsPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvE+KiUV9Np01zJZfqiUHXval9+N7XQoqWWYHxxsbAyEmHEMMleoGBtShoyZTlVb4i5KXtPjG/Pj5n7msiFvLAo9/FJKAfWEwJdSyGw2j5MJrmYyEiRbbt6+Nw5OgRO+jaTcFG/K7onyvtFuCq3A9GOadW2G6PPljM87gJEtnekJRCDulwYV6r45OUfLdgtjR0hD2nBMNwKCyFfhkK5armI8kB4a0nTUi2guuudJvMRskj2Tb83mx/NKT5XIf79VhY28Y05znHd/tfdwl7aTA7uSHv+mD/VRDDvDVaDf9iFa0NlldcYIJ0hugkO09+1iJ1BSJXNosU27LqJzWiBUxvQIDAQAB";


    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        Set<String> cardSet = new HashSet<>();
        cardSet.add("A44E6DA0EB39D62221A4C6911EC50C724110C12E29E72EF1BF56D3350DDE3693");
        Object result = null;
        String path = url + a_uri;
        Map<String, Object> body = new HashMap<String, Object>();
        // a_uri请求参数如下
        body.put("cid", "482793CD172FD9B28A10F55AB2625469");
        body.put("cards", cardSet);
        body.put("orderId", "20160520001000");
        body.put("encryptType", "02");
        body.put("scName", "XX银行");
        body.put("scUsageScenarios", "XX分期");
        body.put("scUsePurpose", "贷前风险管理");
        body.put("protocolVerNm", "2020XXXX10");
        body.put("serialNm", "XXXXXXXX");

        try {
            // rsa 加解密的工具类
            RSAUtil usaUtil = new RSAUtil();

            String bodyStr = objectMapper.writer().writeValueAsString(body);

            System.out.println("body===" + bodyStr);

            // 使用自己的私钥加密
            String encrypt = usaUtil.encryptByPrivateKey(cusPrivateKey, bodyStr);

            // 生成sign：account+encrypt MD5方式生成签文
            StringBuilder buf = new StringBuilder(masterodin_account).append(encrypt);
            String sign = MD5Util.encrypt(buf.toString());

            // 封装map中参数：account，encrypt，sign
            ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
            objectNode.put("account", masterodin_account);
            objectNode.put("encrypt", encrypt);
            //objectNode.put("encrypt", "ZQri24qPDAOMqpJeu357LUXiiD3q1I8t/MBaIYk2YKByfFvyArv1IlbjSbw39kFmZIJOgJCSTDxKHK2+z06jwhdF6tWsmHWzWM39d8bxre3FA2vkmwquwF1G3LxxBohvPPdX06r/wJFg5gkQpIkoFtT1mk84YTAUeoXZw9ro/ze+6v6Cf6b1scE3qIi4omcfXZoKaHW12WO21sazYK3pdnyIwWvjAF6155gmx8AcvqxUB1Tmic3NyC3qnA72Y+/iEHVPwtO1Dc+dA7IG77SfGyrE8BEJM9V9RusAZSvQuynmZCTmOBCvLGDul/WrrvGEFaFzG6Rd1Mpl8KeDtf+ICA=");
            objectNode.put("sign", sign);
            //objectNode.put("sign", "4C68A842C036AAFD4F7384C272C252D");

            // http请求接口
            String params = objectNode.toString();
            LOGGER.info("封装map中参数: {}", params);
            String response = HttpsUtil.doPost(path, params);
            LOGGER.info("请求返回数据：{}", response);

            if (StringUtils.isNotBlank(response)) {
                JsonNode node = objectMapper.readTree(response);
                if (node.has("sign")) {
                    // 返回数据验签
                    String responseSign = node.get("sign").asText();
                    String responseSignData = node.get("account").asText() + node.get("encrypt").asText();
                    boolean verify = MD5Util.encrypt(responseSignData).equalsIgnoreCase(responseSign);
                    LOGGER.info("验签结果{}", verify ? "成功" : "失败");
                    if (verify) {
                        // 验签成功
                        JsonNode data = node.get("encrypt");
                        if (null != data && !data.isNull()) {
                            try {
                                System.out.println("===================" + data.asText());
                                // 使用对方公钥解密
                                String decode = usaUtil.decryptByPublicKey(upsPublicKey, data.asText());
                                objectNode = (ObjectNode) node;
                                objectNode.replace("encrypt", objectMapper.readTree(decode));
                            } catch (Exception e) {

                            } finally {
                                result = node;
                            }
                        }
                    }
                }
            }
            if (null == result) {
                result = response;
            }
        } catch (Exception e) {
            LOGGER.info(e.getMessage(), e);
            result = e.getMessage();
        }

        LOGGER.info("请求返回：{}", result);
    }


}
