package com.zoo.whale.listener;

import com.zoo.whale.example.thread.ThreadRunDemo;
import lombok.extern.slf4j.Slf4j;

/**
 * @Date: 2023年03月12日
 * @Time: 22时:34分
 * @Author: weixin.liu
 * @Version: 1.0
 */
@Slf4j
public class StartListener {

    public static void init() {
        try {
            ThreadRunDemo.initMethod();
        } catch (Exception e) {
            log.error("[海纳]配置中心初始化失败！", e);
        }
    }

}
