package com.zoo.whale.example.future;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * @Date: 2022年09月22日
 * @Time: 15时:42分
 * @Author: weixin.liu
 * @Version: 1.0
 */
@Slf4j
public class WriteTask implements Callable<String> {


    private String req;
    private String response;


    public WriteTask(String req, String response){
        this.req = req;
        this.response = response;
    }

    @Override
    public String call() throws Exception {
       return writeDacCacheToHbase(req,response);
    }

    private String writeDacCacheToHbase(String req,String response) {
        try {
            Thread.sleep(10);
            writeFileToLocation(req,"J:\\pictures\\","test.txt");
            return "89757";
        } catch (Exception e) {
            log.error("海纳大缓存写数据出现异常，请紧急处理！{},code:{},请求数据:{}", e.getMessage(),req,response);
        }
        return "";
    }

    /** 日志记录
     * @author sys
     * @param content 要写入的类容
     * @param path 目标路径 c:/log/
     * @param filename 文件名 log.txt
     */
    public static void writeFileToLocation(String content,String path,String filename) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            File f = new File(path+"//"+filename);
            if(!f.exists())
                f.createNewFile();
            FileWriter fw = new FileWriter(f.getAbsoluteFile(),true);//追加
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content+"");
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        for (int i = 0;i < 10;i++){
            writeFileToLocation(i+"","J:\\pictures\\","test01.txt");
        }
    }

}
