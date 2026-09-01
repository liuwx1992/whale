package com.zoo.whale.al.test;/*
package com.zoo.whale.al.test;

import com.zoo.whale.utils.grpc.EncodeMappingResultBean;
import com.zoo.whale.utils.grpc.EncodeMappingServiceRetryUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

*/
/**
 * @Date: 2023年 06月 20日
 * @Time:18时:09分
 * @Author: weixin.liu
 * @Version:V1.0
 *//*

public class InfoCntEvaluateCust1p1Test {

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
        read();
    }


    private static void read() throws Exception {
        String filePath = "K:\\画像API\\Al-需求文档\\20231115\\icec_v1_1_toWX_20231220\\icec_v1_1_toWX_20231220.xlsx";

        // 创建文件对象
        File file = new File(filePath);
        // 创建文件输入流对象
        FileInputStream inputStream = new FileInputStream(file);
        // 创建工作簿对象
        Workbook workbook = WorkbookFactory.create(inputStream);
        // 获取第一个工作表对象
        Sheet sheet = workbook.getSheetAt(0);

        Map<String, String> keyMap = new HashMap<>();
        Map<String, String> retMap = new HashMap<>();
        String num = "";
        // 遍历行
        int countNum = 0;
        int count = 0;
        int countNull = 0;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            boolean b = readZero(i, keyMap, row);
            if (b){
                continue;
            }
            readData(retMap,keyMap,row);

            JSONObject InfoCntEvaluateCust = sendReq(row);

            num = readNum(row);

            int countSingle = 0;
            int countNullSingle = 0;
            for (Map.Entry<String, String> entry : retMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                String ret = InfoCntEvaluateCust.get(key) == null ? "" : InfoCntEvaluateCust.getString(key);

                if (StringUtils.isEmpty(ret) && StringUtils.isEmpty(value)) {
                    continue;
                } else if (StringUtils.isEmpty(ret) || StringUtils.isEmpty(value)) {
                    countNullSingle++;
                    countNull++;
                    System.out.println(key + "不一致了,value:" + value + ",ret:" + ret);
                    continue;
                }
                try {
                    if (new BigDecimal(ret).compareTo(new BigDecimal(value)) != 0) {
                        countSingle++;
                        count++;
                        System.out.println(key + "不一致,value:" + value + ",ret:" + ret);
                    }
                } catch (Exception e) {
                    System.out.println("异常导致：***" + key + "不一致,value:" + value + ",ret:" + ret);
                }
            }
            if (countSingle != 0 || countNullSingle != 0) {
                System.out.println("编号:" + num + ":   " + countSingle + "条不一致数据");
                System.out.println("编号:" + num + ":   " + countNullSingle + "条不存在数据");
                countNum++;
            }
        }

        System.out.println("-------------------------");
        System.out.println("共计：" + count + "个字段不一致数据");
        System.out.println("共计：" + countNull + "个字段不存在数据");
        System.out.println("共计：" + countNum + " 条样本不一致数据");

        // 关闭工作簿和输入流对象
        workbook.close();
        inputStream.close();
    }

    private static String readNum(Row row) {
        row.getCell(0).setCellType(CellType.STRING);
        return row.getCell(0).getStringCellValue();
    }

    private static void readData(Map<String, String> retMap, Map<String, String> keyMap, Row row) {
        // 遍历单元格
        if(row.getLastCellNum() == 4){
            for (Map.Entry<String,String> entry : keyMap.entrySet()) {
                retMap.put(entry.getValue(), "");
            }
            return;
        }
        for (int j = 00; j< keyMap.entrySet().size();j++) {
            Cell cell = row.getCell(j);
            String key = keyMap.get(j + "");
            if (cell == null) {
                retMap.put(key, "");
                continue;
            }
            String value = "";
            // 判断单元格类型是否为公式类型
            if (cell.getCellType() != null && cell.getCellType() == CellType.NUMERIC) {
                // 打印计算后的单元格值
                value = cell.getNumericCellValue() + "";
            } else {
                // 打印单元格值
                value = cell.getStringCellValue();
            }
            if ("cus_num".equals(key) || "id".equals(key) || "cell".equals(key) || "user_date".equals(key)) {
                continue;
            }
            retMap.put(key, value);
        }

    }

    private static JSONObject sendReq(Row row) throws Exception {
        row.getCell(1).setCellType(CellType.STRING);
        id = decideId(row.getCell(1).getStringCellValue());

        row.getCell(2).setCellType(CellType.STRING);
        cells = decideCell(row.getCell(2).getStringCellValue());

        Date date = row.getCell(3).getDateCellValue();
        user_date = formatForDate2(date);

        String param = InfoCntEvaluateCust();
        String response = send(param);

        //System.out.println(response);

        JSONObject jsonObject = JSONObject.fromObject(response);

        JSONObject InfoCntEvaluateCust = jsonObject.getJSONObject("InfoCntEvaluateCust");

        return InfoCntEvaluateCust;
    }

    private static boolean readZero(int i, Map<String, String> keyMap, Row row) {
        if (i == 0) {
            String value = "";
            //for (Cell cell : row) {
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                // 判断单元格类型是否为公式类型
                if (cell.getCellType() == CellType.NUMERIC) {
                    // 打印计算后的单元格值
                    //System.out.println(cell.getNumericCellValue());
                    value = cell.getNumericCellValue() + "";
                } else {
                    // 打印单元格值
                    //System.out.println(cell.getStringCellValue());
                    value = cell.getStringCellValue();
                }
                keyMap.put(j + "", value);
            }
            return true;
        }
        return false;
    }


    public static String formatForDate2(Date date) {
        SimpleDateFormat FORMAT_DATE_2_PATTERN = new SimpleDateFormat("yyyy-MM-dd");
        return FORMAT_DATE_2_PATTERN.format(date);
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

    private static String cells = "";
    private static String id = "";
    private static String user_date = "";

    public static String InfoCntEvaluateCust() {
        String paramStr = "{\"requestParam\":{\"apiCode\":\"7411228\",\"swiftNumber\":\"7411228_20230705154803_1719\"," +
                "\"jsonData\":\"{\\\"cell\\\":[\\\"" + cells + "\\\"],\\\"user_date\\\":\\\"" + user_date + "\\\",\\\"isMd5\\\":\\\"Y\\\"," +
                "\\\"id\\\":\\\"" + id + "\\\",\\\"meal\\\":\\\"InfoCntEvaluateCust\\\",\\\"id_md5\\\":\\\"\\\",\\\"cell_md5\\\":[]," +
                "\\\"ifDeactivated\\\":0,\\\"mealStr\\\":\\\"InfoCntEvaluateCust#V1.0#5852\\\"}\"},\"meals\":[{\"id\":\"5852\"," +
                "\"serviceName\":\"AL_SERVICE\",\"mealName\":\"InfoCntEvaluateCust\",\"apiVersion\":\"V1.1\"," +
                "\"dataVersion\":\"H:applyLoan_xifen_v2\",\"outVersion\":\"A1.1\",\"defaultValue\":\"InfoCntEvaluateCust\"}]}";
        return paramStr;
    }

}
*/
