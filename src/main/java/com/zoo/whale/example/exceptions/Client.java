package com.zoo.whale.example.exceptions;

import lombok.extern.slf4j.Slf4j;

/**
 * @Date: 2022年09月28日
 * @Time: 14时:16分
 * @Author: weixin.liu
 * @Version: 1.0
 */
@Slf4j
public class Client {

    public static void main(String[] args) {
        String ret = getInfo(1);
        System.out.println("返回结果:"+ret);
    }

    public static String getInfo(int para){
        try {
            int i = 1/0;
            if(para == 1){
                return "1";
            }
        }catch (Exception e){
            log.error("getInfo error:",e);
        }
        return "0";
    }

}
