package com.zoo.whale.business.tx;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;


public class TX_lkf {

    private final static String AppId = "bairongyunchuang";
    private final static String secret = "qOgDBiWbOh7scTIZ";
    private final static String servertype = "1";

    public static void main(String args[]) throws Exception {
        //FYLT_BatchDataRun("");

        String url = "https://api.lkfz.qq.com/query/victimphonenum";
        String phone = "e4f74012e81d44394dec960a1c259176acd851c58ea77c041b3c08dbb754c2a4";
//	    String s = MD5Utils.genMd5("17792146020");
//	    System.out.print(s);
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(10000L);
        int seq = secureRandom.nextInt(1000);
        int queryId = secureRandom.nextInt(1000);
        int sendTime = (int) System.currentTimeMillis();
        String sign = createSignId(AppId, secret, sendTime, seq);// createLinkString(params,appSecret);
        //设置通用的请求属性
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("AppId", AppId);
        header.put("Seq", seq + "");
        header.put("Timestamp", sendTime + "");
        header.put("QueryId", queryId + "");
        header.put("SignId", sign);
        header.put("ServerType", 1 + "");
        header.put("Referer", "125223222");
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.137 Safari/537.36 LBBROWSER");

        //设置入参内容
        JSONObject paramJSON = new JSONObject();
        Map<String, String> params = new HashMap<String, String>();
        params.put("phone", phone);
        paramJSON.put("Data", params);
        System.out.print(seq + "   " + sendTime + "    " + sign + "   " + queryId);

        // json方式发送post请求
        long start = System.currentTimeMillis();
        String result = HttpClientUtil.doPost(url, paramJSON.toJSONString(), "utf-8", header);
        long end = System.currentTimeMillis();


        System.out.println(result);
        System.out.println("耗时 " + (end - start) + "ms");
    }

    /**
     * 生成数字签名
     * md5(md5(appid + 约定密码 + 十进制unixTime串) + seq)；腾讯侧提供约定密码
     *
     * @param
     * @return String
     */

    public static String createSignId(String appid, String secret, int unixTime, int seq) {
        String data = appid + secret + unixTime;
        System.out.println("data:" + data);
        String calSign = DigestUtils.md5Hex(DigestUtils.md5Hex(data) + seq);
        return calSign;
    }

    public static void FYLT_BatchDataRun(String filePath) throws Exception {
        BufferedOutputStream buffer = new BufferedOutputStream(new FileOutputStream(new File("D:/测试数据/40.txt"), true));
        String url = "https://api.dazhenyun.cn/gw1/api/v1/openplat/dz/portraitCreditBehaviorTSLV2";// https://app.fahaicc.com/fhfk/query.jsp
        try {
            String encoding = "UTF-8";// 乱码就换成UTF-8
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String s = null;
                System.out.println("12313232");
                String[] a;
                FileWriter fileWriter = new FileWriter("");
                while ((s = bufferedReader.readLine()) != null) {
                    a = s.split(",");
                    String phone = a[0];
//					String name = "0e84a38aa86aa0a6de9599c2706f616f";
                    JSONObject paramJSON = new JSONObject();
//					paramJSON.put("idcard", idcard);
//					paramJSON.put("name", name);
                    paramJSON.put("phone", phone);

                    Map<String, String> params = new HashMap<String, String>();
//					params.put("idcard", idcard);
//					params.put("name", name);
                    params.put("phone", phone);


                    long start = System.currentTimeMillis();
                    //String result = HttpClientUtil.doPost(url, paramJSON.toJSONString(), "utf-8");
                    long end = System.currentTimeMillis();
                    //String result1 = result + "耗时" + (end - start) + "ms";

                    //System.out.println(a[0]+result1);
                    //fileWriter.write(a[0]+result1);
                    fileWriter.write("\r\n");
//					System.out.print(JSONObject.parseObject(result) + "耗时 " + (end - start) + "ms");
                    fileWriter.flush();
                }
                fileWriter.close();
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
