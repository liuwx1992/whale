package com.zoo.whale.utils.ylzh;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    public static String encrypt(String data) {
        data = DigestUtils.md5Hex(data);
        return data;
    }


    public static void main(String[] args) {
        /*String data = "123456789";
        data = encrypt(data);
        System.out.println(data);// 25F9E794323B453885F5181F1B624D0B
        data = "25F9E794323B453885F5181F1B624D0B";*/


        String s = "9031001,\n" +
                "        2031001,\n" +
                "        2031002,\n" +
                "        2031204,\n" +
                "        2031205,\n" +
                "        2031206,\n" +
                "        2031207,\n" +
                "        2031208,\n" +
                "        2031209,\n" +
                "        2031210,\n" +
                "        2031211,\n" +
                "        2031212,\n" +
                "        2031213,\n" +
                "        2031214,\n" +
                "        2031215,\n" +
                "        2031216,\n" +
                "        2031217,\n" +
                "        2031218,\n" +
                "        2031219,\n" +
                "        2031220";

        System.out.println(s.trim().replace("\n", "").replace("        ", ""));

    }


}
