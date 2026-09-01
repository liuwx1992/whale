package com.zoo.whale.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zoo.whale.service.WestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Date: 2025年 11月 13日
 * @Time:16时:57分
 * @Author: weixin.liu
 * @Version:V1.0
 */
@RestController
public class WestvirginiaController {

    private static final Logger logger = LoggerFactory.getLogger(WestvirginiaController.class);

    private final WestService westService;

    public WestvirginiaController(WestService westService) {
        this.westService = westService;
    }


    @RequestMapping(value = "/west/v1/travel", produces = "text/html;charset=UTF-8")
    public String getReport(String apiCode, String jsonData,
                            HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        if (logger.isInfoEnabled()) {
            logger.info("请求画像，apiCode: {}, jsonData{}", apiCode, jsonData);
        }

        String result = westService.doLimit(apiCode, jsonData);

        return result;
    }

    @RequestMapping(value = "/west/v1/test", produces = "text/html;charset=UTF-8")
    public String test(String apiCode, String jsonData, HttpServletRequest request,
                       HttpServletResponse response) throws InterruptedException {
        if (logger.isInfoEnabled()) {
            logger.info("请求画像，apiCode: {}, jsonData{}", apiCode, jsonData);
        }

        westService.doTest();

        return "1";
    }

}
