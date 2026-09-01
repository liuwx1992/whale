package com.zoo.whale.utils.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.io.*;

/**
 * @Date: 2023年05月09日
 * @Time: 10时:37分
 * @Author: weixin.liu
 * @Version: 1.0
 */
@Slf4j
public class FileReaderUtil {

    public static String readFile(String path){
        File file = new File(path);
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
        return resultBuild.toString();
    }

    public static String getImageBase64Content(String filePath)
            throws Exception {
        File imgFile = new File(filePath);
        byte[] bytes = null;
        try {
            InputStream is = new FileInputStream(imgFile);
            long length = imgFile.length();
            bytes = new byte[(int) length];

            int offset = 0, numRead = 0;
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.encodeBase64String(bytes);
    }

}
