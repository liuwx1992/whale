package com.zoo.whale;

import com.zoo.whale.utils.FlowRuleManagerNewUtils;
import com.zoo.whale.utils.FlowRuleManagerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Date: 2025年 06月 17日
 * @Time:20时:10分
 * @Author: weixin.liu
 * @Version:V1.0
 */
@Slf4j
@Service
public class WebService {

    public String getData(String mealName){

        //执行限流操作
        boolean canPass = FlowRuleManagerUtils.isCanPass(mealName);
        /*if (!canPass){
            log.warn("产品{}触发限流阈值", mealName);
        }*/

        return "success";
    }



}
