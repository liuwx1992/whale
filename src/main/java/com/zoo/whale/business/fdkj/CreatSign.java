package com.zoo.whale.business.fdkj;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;

public class CreatSign {
	
	public static Map<String, String> paraFilter(Map<String, String> sArray) {
        Map<String, String> result = new HashMap<String, String>();
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
        	String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
                    || key.equalsIgnoreCase("sign_type") || key.equalsIgnoreCase("t")) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }
	public static String createLinkString(Map<String, String> params, String appid, String noise, String appkey, String version) {
		
        params = paraFilter(params);
        List<String> keys = new ArrayList<String>(params.keySet());
        String data = "";
		String jString = JSON.toJSONString(params);
		try {
			byte byteXl[]  = JSON.toJSONString(params).getBytes("UTF-8");
			data = Base64.getEncoder().encodeToString(byteXl);
		}catch (Exception e) {
			e.printStackTrace();// TODO: handle exception
		}
        data = "appid=" + appid + "&data=" + data + "&noise=" + noise +"&key=" + appkey + "&version=" + version;
        System.out.println("data:"+data);
        String calSign = DigestUtils.md5Hex(data).toUpperCase();
        return calSign;
    }
	
}
