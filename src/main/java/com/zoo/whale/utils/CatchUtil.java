package com.zoo.whale.utils;

/**
 * @Date: 2022年08月10日
 * @Time: 10时:28分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class CatchUtil {

    public static void main(String[] args) {
        try {
            int i = 1/0;
        }catch (Exception e){
            System.out.println("error:"+e);
        }
        System.out.println("aaa");
    }

}
