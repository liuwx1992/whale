package com.zoo.whale.utils.date;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @Date: 2023年05月10日
 * @Time: 11时:16分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class GmtDemo {

    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("GMT"));
        System.out.println(now);
    }

}
