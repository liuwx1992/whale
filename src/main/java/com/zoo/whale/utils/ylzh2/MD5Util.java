package com.zoo.whale.utils.ylzh2;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    public static String encrypt(String data) {
        data = DigestUtils.md5Hex(data);
        return data;
    }


    public static void main(String[] args) {
        String data = "123456789";
        data = encrypt(data);
        System.out.println(data);// 25F9E794323B453885F5181F1B624D0B
        data = "25F9E794323B453885F5181F1B624D0B";
    }
}
