package com.zoo.whale.utils.zsxy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

/**
 * @Date: 2023年01月04日
 * @Time: 11时:49分
 * @Author: weixin.liu
 * @Version: 1.0
 */
@Slf4j
public class DataUtils {

    public static void main(String[] args) {
        File file = new File("G:\\param\\result2.txt");
        StringBuilder resultBuild = new StringBuilder();
        try {
            // 构造一个BufferedReader类来读取文件
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            // 使用readLine方法，一次读一行
            while ((s = br.readLine()) != null) {
                if (!s.isEmpty()) {
                    resultBuild.append(s);
                }
            }
            br.close();
        } catch (Exception e) {
            log.error("speed error", e);
        }
        long start = System.currentTimeMillis();

        String result = resultBuild.toString();

        JSONObject resultJson = JSONObject.parseObject(result);
        JSONObject resultInfo = resultJson.getJSONObject("resultInfo");
        /*if(resultInfo == null){
            return result;
        }*/

        Integer statCode = resultJson.getInteger("statcode");
        /*if(statCode != null && statCode != 1600){
            return result;
        }*/
        Integer sessionId = resultJson.getInteger("sessionId");
        String state = resultInfo.getString("state");
        JSONObject stateJson = JSONObject.parseObject(state);

        Integer relationTotal = stateJson.getInteger("relation_total");
        Integer winTotal = stateJson.getInteger("win_total");
        Integer noticeTotal = stateJson.getInteger("notice_total");

        if((relationTotal != null && relationTotal > 0) || (winTotal != null && winTotal > 0) || (noticeTotal != null && noticeTotal > 0)){
            stateJson.put("flag","1");
        } else {
            stateJson.put("flag","0");
        }

        JSONArray winList = stateJson.getJSONArray("win_list");
        JSONArray noticeList = stateJson.getJSONArray("notice_list");
        for(int i = 0;i < winList.size();i++) {
            JSONObject winObject = winList.getJSONObject(i);
            putLowerCaseKey(winObject,"amount_list");
            putLowerCaseKey(winObject,"project_list");
            putLowerCaseKey(winObject,"sections_list");

        }
        for(int i = 0;i < noticeList.size();i++) {
            JSONObject noticeObject = noticeList.getJSONObject(i);
            putLowerCaseKey(noticeObject,"amount_list");
            putLowerCaseKey(noticeObject,"project_list");
            putLowerCaseKey(noticeObject,"sections_list");
        }
        resultInfo.put("state",stateJson);
        resultJson.remove("queryInfo");
        resultJson.put("resultInfo",resultInfo);

    }


    private static void putLowerCaseKey(JSONObject json, String key) {
        JSONArray keyList = json.getJSONArray(key);
        JSONArray keyListNew = new JSONArray();
        for (int j = 0; j < keyList.size(); j++) {
            JSONObject keyJsonNew = new JSONObject();
            JSONObject keyJsonObject = keyList.getJSONObject(j);
            for (Map.Entry<String, Object> entry : keyJsonObject.entrySet()) {
                keyJsonNew.put(entry.getKey().toLowerCase(), entry.getValue());
            }
            keyListNew.add(keyJsonNew);
        }
        json.put(key, keyListNew);
    }

}
