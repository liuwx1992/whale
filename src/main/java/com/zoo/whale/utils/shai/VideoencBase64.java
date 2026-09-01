package com.zoo.whale.utils.shai;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * @ClassName:VideoencBase64
 * @Description:
 * @Author:weixin.liu
 * @Date:2021/11/29
 * @Time:20:30
 * @Version；1.0
 */
public class VideoencBase64 {
    public static void main(String[] args) throws IOException {
        VideoencBase64 v = new VideoencBase64();
        v.test();
    }

    public void test() throws IOException {
        String sourceVideo = "D:/WIN_20211125_16_36_35_Pro.mp4";
        InputStream inputStream = FileUtils.openInputStream(new File(sourceVideo));
        //String ss = new String(Base64.getEncoder().encode(IOUtils.toByteArray(inputStream)),Charset="ISO-8859-1");
        String ss = new String(Base64.getEncoder().encode(IOUtils.toByteArray(inputStream)), Charset.defaultCharset());
        File txtFilePath = new File("D:/base64.txt");
        FileWriter fileWriter = new FileWriter(txtFilePath);
        fileWriter.write(ss);
        fileWriter.flush();
        fileWriter.close();

    }
}
