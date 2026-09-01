package com.zoo.whale.business.json;

import com.zoo.whale.utils.common.FileReaderUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * @Date: 2023年05月08日
 * @Time: 20时:43分
 * @Author: weixin.liu
 * @Version: 1.0
 */
@Slf4j
public class JsonParseDemo {
    public static void main(String[] args) {

        //String path = "H:\\jindie_ocr_5.txt";
        String path = "H:\\vfq_tax_merge.txt";
        String s = FileReaderUtil.readFile(path);

        long start = System.currentTimeMillis();
        String s1 = limitRequest(s);
        System.out.println(s1);
        System.out.println(System.currentTimeMillis() - start);

        String s2 = limitResponse(s);
        System.out.println(s2);

    }


    public static String limitRequest(String requestJson) {
        try {
            int length = requestJson.getBytes(StandardCharsets.UTF_8).length;
            System.out.println(length);
            BigDecimal lengthDe = new BigDecimal(length);
            BigDecimal sizeM = lengthDe.divide(new BigDecimal(1024 * 1024));
            log.warn("sizeM{}",sizeM);

            if (requestJson.length() <= 1000) {
                return requestJson;
            }
            JSONObject jsonObject = JSONObject.fromObject(requestJson);
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = jsonObject.getString(key);
                if (value.length() > 1000) {
                    jsonObject.put(key, "参数过大,不显示!");
                }
            }
            return jsonObject.toString();
        } catch (Exception e) {
            log.warn("limitRequest error:", e);
        }
        return requestJson;
    }


    private static String limitResponse(String response) {
        int length = response.getBytes(StandardCharsets.UTF_8).length;;
        if(length <= 2 * 1024 * 1024){
            return response;
        }
        return "{\"key\":\"unsave\"}";
    }

}
