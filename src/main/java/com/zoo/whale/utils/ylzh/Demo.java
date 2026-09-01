package com.zoo.whale.utils.ylzh;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Demo {

    /*private static Logger LOGGER = LoggerFactory.getLogger(Demo.class);
    private static String url = "http://192.168.87.209:8088";
    private static String a_uri = "/auth/authCard";
    private static String masterodin_account = "00000000";
    private static String cusPrivateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC/SYVfUCwkf1/heX+LwGSlOS2hIf1fgXkoplY1S7e/2QFqNmljFShW4cCMkqD1Ei1boZr7u/M00cb6ihwQ3T3Yk5i938QzkCoYcxmSiSFSk1L+Fxu4DkbpIaUGtFOvjdjES56C4U0r4bfgFTwaczqsFZmQv9X8d2GxRlx70oNMoRnVQ0iL6lXhGzhmnPd1TR/FLW6t6GSy1FVAdQY5/yqqubqR9QY92IZ/TxwsncQ0bZtLK1Bdo3GjGkXMDixvyPL80BxhNANOc94D5OiGDXd9XZ3D2Pue2Ei8pV8g5OYsUpZTMlo00ug2TxPhrMOI846D6JWT6XFUEmUXOy4geJnZAgMBAAECggEAfkZLfBsRuNo48kBheWD9T7DKNWeZunVfBP2hRTeBymy3qWTX9yedZJVMpb+Z744bUTUCeZGeQE0HPJb1ZhCW5iSxHQ7dl2hg0ZkR1ZEe5UMJ82BP1RXfq8ChDyb1Y5uQ8Cw3LJEMf55W4t9BuPg+UCFGKa7p1Or+G6emH7tdH7TJVxwFZMrSmN6XI7/4gzVGp8tYmYJMTQQ9O3gb4nc92uIwQYItpBIQ2zz7AY4/F/Wgp++0WC4NxWAN/kTa6bFpWhMOnCHPVNCtYCwpimFv/u2UqbfDskB5o2zo154yelUTYUairiGh1N46kceyoqUcfzfqqsVj7L0NDkMXWCn2HQKBgQDl4p131Pi7t1Wn1B3slvr5a+q/wZ06dUnSD7zMFL6raxB9ut6jHyu5muQj4RbO5+SNy9uE7SQGuGFZ1vyre42sN/09mrlWB9inj3WoouAHvUftsXjVfFJhcTDKj9rcyC6YFqei0ksTu65C+ZlQZXFM2BcwKZcxrSJmOzCklO/VkwKBgQDVBGvPm/Qk8wMQKjcQGqwzNOABDlMYZdu7V10NJB11d3X/u+snj44aE19gYIm6xQ99dIQz2Evs9XIN0fAzOiL2ReIpkZ4VTdlZX6ljgZ6AgwnZhUr5dAhl9gMmbL+JkAaiGJ+wAGDn/0mm1B8LknixfolOcLewzsvpwCz9eCQ2YwKBgGNCAq2/9hnPIhvR2Y3S7tCNCRH8vi+EfvhFNUaE2oLXOoqfCgwypEWCnGjlglyaAbQ7xi/R2JFSQS5yRiygr14h2qUjzkxYHPLUXYAPu118G7eEvmCnSkaqgd+kV2KmyH+AoKcL18DQc4CVlt4AvvfoAbMY8iBP2h98BRe6+wcjAoGBAMoLTHr5cCPPv3sIn3uN4QEcyM8XmCdPDZBu99psA+kT6v8qbYvzdJQ1BcTzLA/Ts7KJ3o8SPEnAMHL9Sng8T0cJ188XyDpqwZosywyPuBgqsh/4drmOnhyfsDM0+KNw0WnUMzqL0V6bXG95SQz/26x22aNl9EUmkHDhD4tG9goDAoGBAMxFEw4rn06QKw8Ep0xIEArjI05tuPBlF2igNoTnge7wUXMTrtpdkDR25Z6s8U5eMdfGLE/qseHr0Kx9FrdsRHq7RPGLs3h/TXqNVkpFMlzeApZIySDmw06ktgPYoto6ggmHI8QAFTBRM0cNJFDe+EN48wzszuYgx/UkhXBZ+tkd";
    private static String upsPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv0mFX1AsJH9f4Xl/i8BkpTktoSH9X4F5KKZWNUu3v9kBajZpYxUoVuHAjJKg9RItW6Ga+7vzNNHG+oocEN092JOYvd/EM5AqGHMZkokhUpNS/hcbuA5G6SGlBrRTr43YxEueguFNK+G34BU8GnM6rBWZkL/V/HdhsUZce9KDTKEZ1UNIi+pV4Rs4Zpz3dU0fxS1urehkstRVQHUGOf8qqrm6kfUGPdiGf08cLJ3ENG2bSytQXaNxoxpFzA4sb8jy/NAcYTQDTnPeA+Tohg13fV2dw9j7nthIvKVfIOTmLFKWUzJaNNLoNk8T4azDiPOOg+iVk+lxVBJlFzsuIHiZ2QIDAQAB";
    private static ObjectMapper objectMapper = new ObjectMapper();*/


    private static Logger LOGGER = LoggerFactory.getLogger(Demo.class);
    private static String url = "https://warcraft-test.unionpaysmart.com";
    private static String a_uri = "/index/personal/v3";
    private static String masterodin_account = "T4010045";
    private static String cusPrivateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC2CMIFdQ2yvXSKLCToQZEiiW60HKaz5bC6TmYgYXSRonST84IdL5um4LIsifQm69dxX71X0i9gAFxELDqJbf6Wh5XIzqoF7YIQpq42NluhHQA3yQDc5A/80D6u4bxtroo1muCfBIyFh/IbjsMV42kiFl4zJjpjLby24cjVUmFeeTtpAimNQ9Xm8KQjNk+SxUR7QjmWGqzFvTcKn8SYHjSwsiPDN+/OILu8jNRx8J/6Aw4Y8pirYqYnFmOC4WPaKeeQygq9zn3SM97VHLTVFskpV8Xa2xRSYkNh9fhzo0BMLCtAREbOhkFsLMIVUCYGcttfAWTlm7CdhsnXrcVfM6xLAgMBAAECggEBAK6KUTcYWG0U+YRu9Wh5Tec2rZ4nhrQhvcW13HiZ2UK26V7fctf3SSDGfDD2idMz+ZystwlY8LH1RoLbgbk6bYz6LmWfuyj55ed/WrWCg3NFMmtK4Pfr+WRobGIU8i6c18q0WUJAA1kZ08wWo/n91lWn6uTSd9OXDEay0XWxiskghB9+hsnHn8vp4/KzsjjutIBAVHvE3h7nECrGclGFygkMJfhLJYlEttRZDj4LJflZEMBMD/BhocDamL5e1qOrHz/b3IbpHwlcjMrRqYOsy9axZZhlXbMmRwt4/fdS9vb9ZUz2/R9Cb3LZkq+5Xa08IrbI1XtqIKO+NRuLrV1gPrkCgYEA/BJve3+kYPgouEJWfzMiA1G0S8cy6LKXsin/c4e1cS7MC/3R6ZQs7wrHj4Foi529a2Dl2m2jIAIxEKvpzZ+rIR7zFtdRSKOt+BTmfonPYeVbuuagNXXbiXtxqmZQGIbIOBXILSfw5G1ph77dye0u8et6AlcKVsukSsgtejOZMk0CgYEAuN7tjR1NOQHr4d7Yz6yklsmtAG38Wg9/6iOYObqZpOm+9G2dFGoB0DwArkH5tnzPY+K6jy+Rkd//G+UmbKkP2e0ZZo2aHtWbk/aqcG5tYuiX/ZnBwtwBQZQonJymKwjQ7f6k8d437fKSyoMML0RbEGoXaaT88UJ0mW2IIuIetPcCgYBDWRGcJ4BdwC/w+xhtJ9ZAmMXZMUGy1nSdT4dG8M1TjjNKSOiaxdIzk+e5udOa6exAEkWXVI+u76b4U1ZGcBCi6sf8JE89Xy4OCFeFG4DjIQqfJxtK7VBYrTizVF62DtzTz04Kco90GJ+He0F6v/o4/Lc+hM7oXcRpjo5IBh06SQKBgBEq8ph2LiOrIAKbtTqj56zxEZXGoPakFdbic1J5F6eO5WLBgyLQLVbvG4kTK2Nc981o0rZVToljacdnYkgY8xXjxMYXREa0w3vcHjvOpW3X97VfP/UehSdX4OWaTnwUl4JicWLcpgj8y9DuloUVLfe6Qm5XGf41k6pj146VvRBFAoGBAMFDqnzs7Fs96EYqCEXxQmH7GvwahiZ0BSBI6ymK3hooApKAaCZ0/oJQRaFZAOC4xLblcLSWVzUPnyX+iN1oqrogAid9/pvnBKzA3aiXH6TebdeSKVdAH/q4y16rloUAH/9l58zGHTHinF0Dg81pE5I27SBGlESiQAaaM68XqM6n";
    private static String upsPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtgjCBXUNsr10iiwk6EGRIolutByms+Wwuk5mIGF0kaJ0k/OCHS+bpuCyLIn0JuvXcV+9V9IvYABcRCw6iW3+loeVyM6qBe2CEKauNjZboR0AN8kA3OQP/NA+ruG8ba6KNZrgnwSMhYfyG47DFeNpIhZeMyY6Yy28tuHI1VJhXnk7aQIpjUPV5vCkIzZPksVEe0I5lhqsxb03Cp/EmB40sLIjwzfvziC7vIzUcfCf+gMOGPKYq2KmJxZjguFj2innkMoKvc590jPe1Ry01RbJKVfF2tsUUmJDYfX4c6NATCwrQERGzoZBbCzCFVAmBnLbXwFk5ZuwnYbJ163FXzOsSwIDAQAB";
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        Object result = null;
        String path = url + a_uri;
        Map<String, Object> body = new HashMap<String, Object>();
        // a_uri请求参数如下
        body.put("name", "张三");
        body.put("orderId", "20160520001000");
        body.put("cid", "412727198812215416");
        body.put("type", 1);
        body.put("card", "6225396976879419");
        body.put("mobile", "13611917291");

        try {
            // rsa 加解密的工具类
            RSAUtil usaUtil = new RSAUtil();

            // 使用自己的私钥加密
            String encrypt = usaUtil.encryptByPrivateKey(cusPrivateKey, objectMapper.writer().writeValueAsString(body));

            // 生成sign：account+encrypt MD5方式生成签文
            StringBuilder buf = new StringBuilder(masterodin_account).append(encrypt);
            String sign = MD5Util.encrypt(buf.toString());

            // 封装map中参数：account，encrypt，sign
            ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
            objectNode.put("account", masterodin_account);
            objectNode.put("encrypt", encrypt);
            objectNode.put("sign", sign);

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
                                // 使用对方公钥解密
                                String decode = usaUtil.decryptByPublicKey(upsPublicKey, data.asText());
                                objectNode = (ObjectNode) node;
                                objectNode.replace("encrypt", objectMapper.readTree(decode));
                            } catch (Exception e) {
                                System.out.println("解密失败");
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
