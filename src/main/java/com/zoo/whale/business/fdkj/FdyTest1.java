package com.zoo.whale.business.fdkj;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FdyTest1 {

    private final static String appid = "W0xuXxhK_12";
    private final static String appkey = "00c1a8ae11c9cfd43772b3c7a2fb55ed262322de";
    private final static String version = "1.0";
    //private static String noise = UUID.randomUUID().toString();
    private static String noise = "hbxunifipt8fjhug";

    public static void main(String args[]) throws Exception {
        //FYLT_BatchDataRun("K:\\fdkj需求文档\\test_all.txt");
		query();
    }

    public static void query() throws UnsupportedEncodingException {
        for (int i = 0; i < 1; i++) {
            String url = "https://pj-pre.fendanyun.com/rsx/api/checkInputNonTax";
            //接口入参data
            String invoiceCode = "36060119";
            String invoiceNo = "0000000001";
            String invoiceCheckCode = "810586";
            String invoiceAmt = "281.06";
            String invoiceDate = "2019-12-18";


            /*String invoiceCode = "62060119";
            String invoiceNo = "0004181047";
            String invoiceCheckCode = "52f14d";
            String invoiceAmt = "72.12";
            String invoiceDate = "2021-01-21";*/




            //参数data
            Map<String, String> param = new HashMap<String, String>();
            param.put("invoiceCode", invoiceCode);
            param.put("invoiceNo", invoiceNo);
            param.put("invoiceCheckCode", invoiceCheckCode);
            param.put("invoiceAmt", invoiceAmt);
            param.put("invoiceDate", invoiceDate);
            //map转json
            String data = "";
            String jString = JSON.toJSONString(param);
            //json进行base64编码
            try {
                byte byteXl[] = JSON.toJSONString(param).getBytes("UTF-8");
                data = Base64.getEncoder().encodeToString(byteXl);
            } catch (Exception e) {
                e.printStackTrace();// TODO: handle exception
            }

            //上传参数json
            JSONObject paramJSON = new JSONObject();
            paramJSON.put("appid", appid);
            paramJSON.put("data", data);
            paramJSON.put("noise", noise);
            paramJSON.put("version", version);

            String sign = CreatSign.createLinkString(param, appid, noise, appkey, version);
            paramJSON.put("sign", sign);

            //输出上传参数
            System.out.print(paramJSON);
            //获取返回参数
            long start = System.currentTimeMillis();
            String result = HttpClientUtil.doPost(url, paramJSON.toJSONString(), "utf-8");
            long end = System.currentTimeMillis();
            System.out.println("1");
            //返回参数转json
            JSONObject jsonObject = JSON.parseObject(result);
            System.out.println(jsonObject);
            //获取返回json中特定字段
            //解析返回json中的data字段
            String data_return = (String) jsonObject.get("data");
            String noise_return = (String) jsonObject.get("noise");
            String sign_return = (String) jsonObject.get("sign");
            String data_dec = new String(Base64.getDecoder().decode(data_return), "UTF-8");//Base64解密
            JSONObject jsonObject2 = JSON.parseObject(data_dec);//data内容转json
            String data_dec_message = (String) jsonObject2.get("message");//获取返回data  json中的字段
            String data_dec_result = (String) jsonObject2.get("result");
            System.out.println("data_dec_result:"+data_dec_result);
            String message_dec = "";
            try {
                message_dec = new String(Base64.getDecoder().decode(data_dec_message), "UTF-8");//Base64解密
                System.out.println("message_dec:"+message_dec);
            } catch (Exception e) {
                System.out.println(data_dec_message);// TODO: handle exception
                message_dec = "\"" + data_dec_message + "\"";
            }
            System.out.println("{\"data\":\"" + "{\"result\":\"" + data_dec_result + "\",\"message\":" + message_dec + "},\"noise\":\"" + noise_return + ",\"sign\":\"" + sign_return + "\"}");


            System.out.println("耗时 " + (end - start) + "ms");
        }
    }

    public static void FYLT_BatchDataRun(String filePath) throws Exception {
        BufferedOutputStream buffer = new BufferedOutputStream(new FileOutputStream(new File("D:/测试数据/40.txt"), true));
        String url = "https://pj-pre.fendanyun.com/rsx/api/checkInputNonTax";
        try {
            String encoding = "UTF-8";// 乱码就换成UTF-8
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String s = null;
                System.out.println("12313232");
                String[] a;
                //FileWriter fileWriter = new FileWriter("E:/日常其他工作/接口测试/20221111医疗票据接入测试/result/1114.txt");
                while ((s = bufferedReader.readLine()) != null) {
                    a = s.split(",");
                    //				System.out.println("name"+a[0]);
                    String invoiceCode = a[0];
                    String invoiceNo = a[1];
                    String invoiceCheckCode = a[2];
                    String invoiceAmt = a[3];
                    String invoiceDate = a[4];

                    Map<String, String> param = new HashMap<String, String>();
                    param.put("invoiceCode", invoiceCode);
                    param.put("invoiceNo", invoiceNo);
                    param.put("invoiceCheckCode", invoiceCheckCode);
                    param.put("invoiceAmt", invoiceAmt);
                    param.put("invoiceDate", invoiceDate);

                    //map转json
                    String data = "";
                    String jString = JSON.toJSONString(param);
                    try {
                        byte byteXl[] = JSON.toJSONString(param).getBytes("UTF-8");
                        data = Base64.getEncoder().encodeToString(byteXl);
                    } catch (Exception e) {
                        e.printStackTrace();// TODO: handle exception
                    }

                    JSONObject paramJSON = new JSONObject();
                    paramJSON.put("appid", appid);
                    paramJSON.put("data", data);
                    paramJSON.put("noise", noise);
                    paramJSON.put("version", version);

                    String sign = CreatSign.createLinkString(param, appid, noise, appkey, version);
                    paramJSON.put("sign", sign);

                    //输出上传参数
                    System.out.print(paramJSON);
                    //获取返回参数
                    long start = System.currentTimeMillis();
                    String result = HttpClientUtil.doPost(url, paramJSON.toJSONString(), "utf-8");
                    long end = System.currentTimeMillis();
                    System.out.println("1");
                    //返回参数转json
                    JSONObject jsonObject = JSON.parseObject(result);
                    System.out.println(jsonObject);
                    //获取返回json中特定字段
                    //解析返回json中的data字段
                    String data_return = (String) jsonObject.get("data");
                    String noise_return = (String) jsonObject.get("noise");
                    String sign_return = (String) jsonObject.get("sign");
                    String data_dec = new String(Base64.getDecoder().decode(data_return), "UTF-8");//Base64解密
                    JSONObject jsonObject2 = JSON.parseObject(data_dec);//data内容转json
                    String data_dec_message = (String) jsonObject2.get("message");//获取返回data  json中的字段
                    String data_dec_result = (String) jsonObject2.get("result");
                    System.out.println(data_dec_result);
                    String message_dec = "";
                    try {
                        message_dec = new String(Base64.getDecoder().decode(data_dec_message), "UTF-8");//Base64解密
                        System.out.println(message_dec);
                    } catch (Exception e) {
                        System.out.println(data_dec_message);// TODO: handle exception
                        message_dec = "\"" + data_dec_message + "\"";
                    }
                    System.out.println("{\"data\":\"" + "{\"result\":\"" + data_dec_result + "\",\"message\":" + message_dec + "},\"noise\":\"" + noise_return + ",\"sign\":\"" + sign_return + "\"}");


                    System.out.println("耗时 " + (end - start) + "ms");
                    System.out.println("-------------------------------------------------------------");
                    //fileWriter.write("\r\n");
                    //					System.out.print(JSONObject.parseObject(result) + "耗时 " + (end - start) + "ms");
                    //fileWriter.flush();


                }
                //fileWriter.close();
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
}
