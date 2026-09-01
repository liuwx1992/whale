package com.zoo.whale.web;

import com.zoo.whale.WebService;
import com.zoo.whale.egress.EgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Date: 2022年10月09日
 * @Time: 14时:22分
 * @Author: weixin.liu
 * @Version: 1.0
 */
@RestController
public class WhaleController {

    @Autowired
    private EgressService egressService;

    @Autowired
    private WebService webService;

    @ResponseBody
    @PostMapping(value = "/whale/v1/getData", produces = "application/json;charset=UTF-8")
    public String ping(String jsonData, String code) {

        return "pong-"+System.currentTimeMillis();
    }

    @ResponseBody
    @PostMapping(value = "/haina/v1/getData", produces = "application/json;charset=UTF-8")
    public String getData(String code,String jsonData) {
        return egressService.getData(code,jsonData);
    }

    @ResponseBody
    @PostMapping(value = "/whale/v1/getSentinel", produces = "application/json;charset=UTF-8")
    public String getSentinel(String mealName) {

        return "pong-"+System.currentTimeMillis() + webService.getData(mealName);
    }

}
