package com.zoo.whale.al.test;/*
package com.zoo.whale.al.test;

import com.zoo.whale.utils.date.DateUtils;
import com.zoo.whale.utils.grpc.EncodeMappingResultBean;
import com.zoo.whale.utils.grpc.EncodeMappingServiceRetryUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

*/
/**
 * @Date: 2023年 06月 20日
 * @Time:18时:09分
 * @Author: weixin.liu
 * @Version:V1.0
 *//*

public class PostloanRepayCluesTest {

    private static RestTemplate template = new RestTemplate();
    private static String url = "http://127.0.0.1:18105/query";

    public static String send(String json) {
        try {
            //System.out.println("the json " + json);
            HttpHeaders headers = new HttpHeaders();
            //  请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            */
/*headers.add("brh-log-level","debug");
            headers.add("brh-log-service","all");*//*

            //  封装参数，千万不要替换为Map与HashMap，否则参数无法传递
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("paramStr", json);
            */
/*params.add("apiCode", "7411119");
            params.add("jsonData", json);*//*

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
            ResponseEntity<String> responseEntity = template.postForEntity(url, requestEntity, String.class);

            //System.out.println(responseEntity.getStatusCode());
            return responseEntity.getBody();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) throws Exception {
        //getInfo();
        readExcel();
    }

    private static void query() throws Exception {
        String s = "";
        String[] split = s.split("\n");
        for (String ss : split) {
            String[] subArray = ss.split("\t");
            id = decideId(subArray[0]);
            cell = decideCell(subArray[1]);
            user_date = subArray[2];
        }
        String result = "";
        for (int i = 0;i < split.length;i++) {
            String ss = split[i];
            String[] subArray = ss.split("\t");
            id = decideId(subArray[0]);
            cell = decideCell(subArray[1]);
            user_date = subArray[2].replace("/","-");


            String param = PostloanRepayClues();
            String response = send(param);
            //System.out.println(response);

            JSONObject jsonObject = JSONObject.fromObject(response);
            String code = jsonObject.getString("code");

            if(code.equals("100002") && subArray.length == 4){
                System.out.println("*********************"+i+"不一致了");
                continue;
            }

            if(code.equals("100002")){
                continue;
            }

            result = subArray[3];

            JSONObject postloanRepayClues = jsonObject.getJSONObject("PostloanRepayClues");
            String level = postloanRepayClues.getString("level");

            if(!level.equals(result)){
                System.out.println("*********************"+i+"不一致了");
            }
        }
    }



    //读取excel
    public static Workbook readExcel(String filePath) {
        Workbook wb = null;
        if (filePath == null) {
            return null;
        }
        String extString = filePath.substring(filePath.indexOf('.'));
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            if (".xls".equals(extString)) {
                return wb = new HSSFWorkbook(is);
            } else if (".xlsx".equals(extString)) {
                return wb = new XSSFWorkbook(is);
            } else {
                return wb = null;
            }

        } catch (FileNotFoundException e) {
            //LOGGER.error(e.toString());
        } catch (IOException e) {
            //LOGGER.error(e.toString());
        } catch (NoClassDefFoundError e) {
            //LOGGER.error(e.toString());
        }
        return wb;
    }

    public static void readExcel() {
        try {
            // 打开 Excel 文件
            FileInputStream inputStream = new FileInputStream(new File("K:\\画像API\\Al-需求文档\\测试样例-towx3.xlsx"));

            // 创建一个 Workbook 对象来表示整个 Excel 文件
            Workbook workbook = WorkbookFactory.create(inputStream);

            // 获取第一个工作表
            Sheet sheet = workbook.getSheetAt(0);

            String result = "";

            String num = "";

            Set<String> emptySet = new HashSet<>();

            Set<String> retSet = new HashSet<>();

            // 循环遍历每一行
            for (Row row : sheet) {
                // 循环遍历每一列
                //Cell cellValue =  row.getCell(i);

                // 输出单元格的内容
                //System.out.print(cellValue.getStringCellValue() + "\t");
                row.getCell(0).setCellType(CellType.STRING);
                num = row.getCell(0).getStringCellValue();

                row.getCell(1).setCellType(CellType.STRING);
                id = decideId(row.getCell(1).getStringCellValue());

                row.getCell(2).setCellType(CellType.STRING);
                cell = decideCell(row.getCell(2).getStringCellValue());


                Date date = row.getCell(3).getDateCellValue();
                user_date = DateUtils.formatForDate2(date);

                if(row.getCell(4) == null){
                    result = "";
                } else {
                    row.getCell(4).setCellType(CellType.STRING);
                    result = row.getCell(4).getStringCellValue();
                }


                if(StringUtils.isNotEmpty(id) && id.length() > 11 && StringUtils.isNotEmpty(cell) && StringUtils.isNotEmpty(user_date)){
                    String param = PostloanRepayClues();
                    String response = send(param);
                    //System.out.println(response);

                    JSONObject jsonObject = JSONObject.fromObject(response);
                    String code = jsonObject.getString("code");

                    if(code.equals("100002") && StringUtils.isNotEmpty(result)){
                        System.out.println("*********************"+num+"不一致了");
                        emptySet.add(num);
                        continue;
                    }

                    if(code.equals("100002")){
                        continue;
                    }

                    JSONObject postloanRepayClues = jsonObject.getJSONObject("PostloanRepayClues");
                    String level = postloanRepayClues.getString("level");

                    if(!level.equals(result)){
                        System.out.println("*********************"+num+"不一致了");
                        retSet.add(num);
                    }
                }
                //System.out.println();
            }
            System.out.println("emptySet"+emptySet.size());
            for (String ss : emptySet) {
                System.out.print(ss+",");
            }
            System.out.println("retSet"+retSet.size());
            for (String ss : retSet) {
                System.out.print(ss+",");
            }

            // 关闭流和 Workbook 对象
            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String decideId(String id) throws Exception {
        // 新接口方法
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("swift_number", "7411228_20230705154803_1719");
        jsonParam.put("appName", "zh_service");
        jsonParam.put("appSecretKey", "d0a84e149d12a8d1b88e85f7b215b87c");
        jsonParam.put("alogrithm", "md5");
        jsonParam.put("key", id);
        jsonParam.put("type", "id");
        EncodeMappingResultBean bean = EncodeMappingServiceRetryUtil.queryRetryOnceIfTimeOut(jsonParam);
        return bean.getData();
    }

    private static String decideCell(String cell) throws Exception {
        // 新接口方法
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("swift_number", "7411228_20230705154803_1719");
        jsonParam.put("appName", "zh_service");
        jsonParam.put("appSecretKey", "d0a84e149d12a8d1b88e85f7b215b87c");
        jsonParam.put("alogrithm", "md5");
        jsonParam.put("key", cell);
        jsonParam.put("type", "cell");
        EncodeMappingResultBean bean = EncodeMappingServiceRetryUtil.queryRetryOnceIfTimeOut(jsonParam);
        return bean.getData();
    }

    private static String cell = "";
    private static String id = "";
    private static String user_date = "";

    public static String PostloanRepayClues(){
        String paramStr = "{\"requestParam\":{\"apiCode\":\"7411228\",\"swiftNumber\":\"7411228_20230705154803_1719\"," +
                "\"jsonData\":\"{\\\"cell\\\":[\\\""+cell+"\\\"],\\\"user_date\\\":\\\""+user_date+"\\\",\\\"isMd5\\\":\\\"Y\\\"," +
                "\\\"id\\\":\\\""+id+"\\\",\\\"meal\\\":\\\"MidloanRiskProbe\\\",\\\"id_md5\\\":\\\"\\\",\\\"cell_md5\\\":[]," +
                "\\\"ifDeactivated\\\":0,\\\"mealStr\\\":\\\"PostloanRepayClues#V1.0#5852\\\"}\"},\"meals\":[{\"id\":\"5852\",\"serviceName\":\"AL_SERVICE\",\"mealName\":\"PostloanRepayClues\",\"apiVersion\":\"\",\"dataVersion\":\"H:ai_nlp|H:loan_data_v2|H:applyLoan_v3\",\"outVersion\":\"\",\"defaultValue\":\"postloanrepayclues\"}]}";
        return paramStr;
    }

    public static String PostloanRepayClues01(){
        String paramStr = "{\"requestParam\":{\"apiCode\":\"7411228\",\"swiftNumber\":\"7411228_20230705154803_1719\",\"jsonData\":\"{\\\"cell\\\":[\\\"15857311344\\\"],\\\"user_date\\\":\\\"2021-08-31\\\",\\\"isMd5\\\":\\\"Y\\\",\\\"id\\\":\\\"330424197212262610\\\",\\\"meal\\\":\\\"MidloanRiskProbe\\\",\\\"id_md5\\\":\\\"\\\",\\\"cell_md5\\\":[],\\\"ifDeactivated\\\":0,\\\"mealStr\\\":\\\"MidloanRiskProbe#V1.0#5618\\\"}\"},\"meals\":[{\"id\":\"5618\",\"serviceName\":\"AL_SERVICE\",\"mealName\":\"MidloanRiskProbe\",\"apiVersion\":\"\",\"dataVersion\":\"H:applyLoan_xifen_v2\",\"outVersion\":\"\",\"defaultValue\":\"midloanriskprobe\"}]}";
        return paramStr;
    }

    public static String MidloanRiskProbeDemo03(){
        String paramStr = "{\"requestParam\":{\"apiCode\":\"7411228\",\"swiftNumber\":\"7411228_20230705154803_1719\",\"jsonData\":\"{\\\"cell\\\":[\\\"15051333933\\\"],\\\"user_date\\\":\\\"2021-08-31\\\",\\\"isMd5\\\":\\\"Y\\\",\\\"id\\\":\\\"320922198612143677\\\",\\\"meal\\\":\\\"MidloanRiskProbe\\\",\\\"id_md5\\\":\\\"\\\",\\\"cell_md5\\\":[],\\\"ifDeactivated\\\":0,\\\"mealStr\\\":\\\"MidloanRiskProbe#V1.0#5618\\\"}\"},\"meals\":[{\"id\":\"5618\",\"serviceName\":\"AL_SERVICE\",\"mealName\":\"MidloanRiskProbe\",\"apiVersion\":\"\",\"dataVersion\":\"H:applyLoan_xifen_v2\",\"outVersion\":\"\",\"defaultValue\":\"midloanriskprobe\"}]}";
        return paramStr;
    }

    public static String MidloanRiskProbeDemo04(){
        String paramStr = "{\"requestParam\":{\"apiCode\":\"7411228\",\"swiftNumber\":\"7411228_20230705154803_1719\"," +
                "\"jsonData\":\"{\\\"cell\\\":[\\\"13517654067\\\"],\\\"user_date\\\":\\\"2021-08-31\\\",\\\"isMd5\\\":\\\"Y\\\",\\\"id\\\":\\\"450923198506158512\\\"," +
                "\\\"meal\\\":\\\"MidloanRiskProbe\\\",\\\"id_md5\\\":\\\"\\\",\\\"cell_md5\\\":[],\\\"ifDeactivated\\\":0,\\\"mealStr\\\":\\\"MidloanRiskProbe#V1.0#5618\\\"}\"}," +
                "\"meals\":[{\"id\":\"5618\",\"serviceName\":\"AL_SERVICE\",\"mealName\":\"MidloanRiskProbe\",\"apiVersion\":\"\",\"dataVersion\":\"H:applyLoan_xifen_v2\",\"outVersion\":\"\",\"defaultValue\":\"midloanriskprobe\"}]}";
        return paramStr;
    }

    public static String MidloanRiskProbeDemo05(){
        String paramStr = "{\"requestParam\":{\"apiCode\":\"7411228\",\"swiftNumber\":\"7411228_20230705154803_1719\"," +
                "\"jsonData\":\"{\\\"cell\\\":[\\\"13838700348\\\"],\\\"user_date\\\":\\\"2021-08-31\\\",\\\"isMd5\\\":\\\"Y\\\",\\\"id\\\":\\\"411323199305203853\\\"," +
                "\\\"meal\\\":\\\"MidloanRiskProbe\\\",\\\"id_md5\\\":\\\"\\\",\\\"cell_md5\\\":[],\\\"ifDeactivated\\\":0,\\\"mealStr\\\":\\\"MidloanRiskProbe#V1.0#5618\\\"}\"}," +
                "\"meals\":[{\"id\":\"5618\",\"serviceName\":\"AL_SERVICE\",\"mealName\":\"MidloanRiskProbe\",\"apiVersion\":\"\",\"dataVersion\":\"H:applyLoan_xifen_v2\",\"outVersion\":\"\",\"defaultValue\":\"midloanriskprobe\"}]}";
        return paramStr;
    }

    public static String MidloanRiskProbeDemo06(){
        String paramStr = "{\"requestParam\":{\"apiCode\":\"7411228\",\"swiftNumber\":\"7411228_20230705154803_1719\"," +
                "\"jsonData\":\"{\\\"cell\\\":[\\\"17773619699\\\"],\\\"user_date\\\":\\\"2021-08-31\\\",\\\"isMd5\\\":\\\"Y\\\",\\\"id\\\":\\\"430702199006103556\\\"," +
                "\\\"meal\\\":\\\"MidloanRiskProbe\\\",\\\"id_md5\\\":\\\"\\\",\\\"cell_md5\\\":[],\\\"ifDeactivated\\\":0,\\\"mealStr\\\":\\\"MidloanRiskProbe#V1.0#5618\\\"}\"}," +
                "\"meals\":[{\"id\":\"5618\",\"serviceName\":\"AL_SERVICE\",\"mealName\":\"MidloanRiskProbe\",\"apiVersion\":\"\",\"dataVersion\":\"H:applyLoan_xifen_v2\",\"outVersion\":\"\",\"defaultValue\":\"midloanriskprobe\"}]}";
        return paramStr;
    }

    //{"jsonData":{"meal":"MidloanRiskProbe","user_date":"2021-09-01","name":"","id":"2A32365CEAE65B901F0B8BEAAFE08C40","cell":"87DB9C3540B0C02A9B8FF7DB397EC8F0"},"tokenid":"7421229_3A6388CC8774244EC2DBDA73D57D0EE9","apiCode":"7421229","checkCode":"380cd0fed804d4bd6d9a263548ae3718"}
    //{"jsonData":{"meal":"MidloanRiskProbe","user_date":"2021-09-01","name":"","id":"2A32365CEAE65B901F0B8BEAAFE08C40","cell":"87DB9C3540B0C02A9B8FF7DB397EC8F0"},"tokenid":"7421229_3A6388CC8774244EC2DBDA73D57D0EE9","apiCode":"7421229","checkCode":"380cd0fed804d4bd6d9a263548ae3718"}
    public static String MidloanRiskProbeDemo07(){
        String paramStr = "{\"requestParam\":{\"apiCode\":\"7411228\",\"swiftNumber\":\"7411228_20230705154803_1719\"," +
                "\"jsonData\":\"{\\\"cell\\\":[\\\"17660402298\\\"],\\\"user_date\\\":\\\"2021-09-01\\\",\\\"isMd5\\\":\\\"Y\\\",\\\"id\\\":\\\"370602199803055217\\\"," +
                "\\\"meal\\\":\\\"MidloanRiskProbe\\\",\\\"id_md5\\\":\\\"\\\",\\\"cell_md5\\\":[],\\\"ifDeactivated\\\":0,\\\"mealStr\\\":\\\"MidloanRiskProbe#V1.0#5618\\\"}\"}," +
                "\"meals\":[{\"id\":\"5618\",\"serviceName\":\"AL_SERVICE\",\"mealName\":\"MidloanRiskProbe\",\"apiVersion\":\"\",\"dataVersion\":\"H:applyLoan_xifen_v2\",\"outVersion\":\"\",\"defaultValue\":\"midloanriskprobe\"}]}";
        return paramStr;
    }

    //{"jsonData":{"meal":"MidloanRiskProbe","user_date":"2021-09-01","name":"","id":"360D3B83DDE80E3B722679D01EE1F9D3","cell":"0B166156903B76DA0E71CF7B47FF8B57"},"tokenid":"7421229_3A6388CC8774244EC2DBDA73D57D0EE9","apiCode":"7421229","checkCode":"e9accc6ea090741fc2969865c40ec5d4"}
    public static String MidloanRiskProbeDemo08(){
        String paramStr = "{\"requestParam\":{\"apiCode\":\"7411228\",\"swiftNumber\":\"7411228_20230705154803_1719\"," +
                "\"jsonData\":\"{\\\"cell\\\":[\\\"15873278105\\\"],\\\"user_date\\\":\\\"2021-09-01\\\",\\\"isMd5\\\":\\\"Y\\\",\\\"id\\\":\\\"43030219801021077X\\\"," +
                "\\\"meal\\\":\\\"MidloanRiskProbe\\\",\\\"id_md5\\\":\\\"\\\",\\\"cell_md5\\\":[],\\\"ifDeactivated\\\":0,\\\"mealStr\\\":\\\"MidloanRiskProbe#V1.0#5618\\\"}\"}," +
                "\"meals\":[{\"id\":\"5618\",\"serviceName\":\"AL_SERVICE\",\"mealName\":\"MidloanRiskProbe\",\"apiVersion\":\"\",\"dataVersion\":\"H:applyLoan_xifen_v2\",\"outVersion\":\"\",\"defaultValue\":\"midloanriskprobe\"}]}";
        return paramStr;
    }

    //{"jsonData":{"meal":"MidloanRiskProbe","user_date":"2021-09-01","name":"","id":"68E00EB945C6D9024EADDB998D7593B9","cell":"E440A5FB233C726B12B338789488C2AB"},"tokenid":"7421229_3A6388CC8774244EC2DBDA73D57D0EE9","apiCode":"7421229","checkCode":"c3cae0b6d51e736955115b8b57c3b545"}
    public static String MidloanRiskProbeDemo09(){
        String paramStr = "{\"requestParam\":{\"apiCode\":\"7411228\",\"swiftNumber\":\"7411228_20230705154803_1719\"," +
                "\"jsonData\":\"{\\\"cell\\\":[\\\"18535664471\\\"],\\\"user_date\\\":\\\"2021-09-01\\\",\\\"isMd5\\\":\\\"Y\\\",\\\"id\\\":\\\"140581198812024220\\\"," +
                "\\\"meal\\\":\\\"MidloanRiskProbe\\\",\\\"id_md5\\\":\\\"\\\",\\\"cell_md5\\\":[],\\\"ifDeactivated\\\":0,\\\"mealStr\\\":\\\"MidloanRiskProbe#V1.0#5618\\\"}\"}," +
                "\"meals\":[{\"id\":\"5618\",\"serviceName\":\"AL_SERVICE\",\"mealName\":\"MidloanRiskProbe\",\"apiVersion\":\"\",\"dataVersion\":\"H:applyLoan_xifen_v2\",\"outVersion\":\"\",\"defaultValue\":\"midloanriskprobe\"}]}";
        return paramStr;
    }

    //330182198807060614
    public static String MidloanRiskProbeDemo10(){
        String paramStr = "{\"requestParam\":{\"apiCode\":\"7411228\",\"swiftNumber\":\"7411228_20230705154803_1719\"," +
                "\"jsonData\":\"{\\\"cell\\\":[\\\"\\\"],\\\"user_date\\\":\\\"2021-09-01\\\",\\\"isMd5\\\":\\\"Y\\\",\\\"id\\\":\\\"330182198807060614\\\"," +
                "\\\"meal\\\":\\\"MidloanRiskProbe\\\",\\\"id_md5\\\":\\\"\\\",\\\"cell_md5\\\":[],\\\"ifDeactivated\\\":0,\\\"mealStr\\\":\\\"MidloanRiskProbe#V1.0#5618\\\"}\"}," +
                "\"meals\":[{\"id\":\"5618\",\"serviceName\":\"AL_SERVICE\",\"mealName\":\"MidloanRiskProbe\",\"apiVersion\":\"\",\"dataVersion\":\"H:applyLoan_xifen_v2\",\"outVersion\":\"\",\"defaultValue\":\"midloanriskprobe\"}]}";
        return paramStr;
    }

    public static String MidloanRiskProbeDemo11(){
        String paramStr = "{\"requestParam\":{\"apiCode\":\"7411228\",\"swiftNumber\":\"7411228_20230705154803_1719\"," +
                "\"jsonData\":\"{\\\"cell\\\":[\\\"\\\"],\\\"user_date\\\":\\\"2021-09-01\\\",\\\"isMd5\\\":\\\"Y\\\",\\\"id\\\":\\\"342425198509252725\\\"," +
                "\\\"meal\\\":\\\"MidloanRiskProbe\\\",\\\"id_md5\\\":\\\"\\\",\\\"cell_md5\\\":[],\\\"ifDeactivated\\\":0,\\\"mealStr\\\":\\\"MidloanRiskProbe#V1.0#5618\\\"}\"}," +
                "\"meals\":[{\"id\":\"5618\",\"serviceName\":\"AL_SERVICE\",\"mealName\":\"MidloanRiskProbe\",\"apiVersion\":\"\",\"dataVersion\":\"H:applyLoan_xifen_v2\",\"outVersion\":\"\",\"defaultValue\":\"midloanriskprobe\"}]}";
        return paramStr;
    }

    public static String MidloanRiskProbeDemo12(){
        String paramStr = "{\"requestParam\":{\"apiCode\":\"7411228\",\"swiftNumber\":\"7411228_20230705154803_1719\"," +
                "\"jsonData\":\"{\\\"cell\\\":[\\\"\\\"],\\\"user_date\\\":\\\"2021-09-01\\\",\\\"isMd5\\\":\\\"Y\\\",\\\"id\\\":\\\"360281199709055212\\\"," +
                "\\\"meal\\\":\\\"MidloanRiskProbe\\\",\\\"id_md5\\\":\\\"\\\",\\\"cell_md5\\\":[],\\\"ifDeactivated\\\":0,\\\"mealStr\\\":\\\"MidloanRiskProbe#V1.0#5618\\\"}\"}," +
                "\"meals\":[{\"id\":\"5618\",\"serviceName\":\"AL_SERVICE\",\"mealName\":\"MidloanRiskProbe\",\"apiVersion\":\"\",\"dataVersion\":\"H:applyLoan_xifen_v2\",\"outVersion\":\"\",\"defaultValue\":\"midloanriskprobe\"}]}";
        return paramStr;
    }

    public static String MidloanRiskProbeDemo13(){
        String paramStr = "{\"requestParam\":{\"apiCode\":\"7434632\",\"swiftNumber\":\"7411228_20230705154803_1719\"," +
                "\"jsonData\":\"{\\\"cell\\\":[\\\"\\\"],\\\"user_date\\\":\\\"2023-09-06\\\",\\\"isMd5\\\":\\\"Y\\\",\\\"id\\\":\\\"51072219951204489X\\\"," +
                "\\\"meal\\\":\\\"MidloanRiskProbe\\\",\\\"id_md5\\\":\\\"\\\",\\\"cell_md5\\\":[],\\\"ifDeactivated\\\":0,\\\"mealStr\\\":\\\"MidloanRiskProbe#V1.0#5618\\\"}\"}," +
                "\"meals\":[{\"id\":\"5618\",\"serviceName\":\"AL_SERVICE\",\"mealName\":\"MidloanRiskProbe\",\"apiVersion\":\"\",\"dataVersion\":\"H:applyLoan_xifen_v2\",\"outVersion\":\"\",\"defaultValue\":\"midloanriskprobe\"}]}";
        return paramStr;
    }




}
*/
