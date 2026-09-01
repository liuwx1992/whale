package com.zoo.whale.config;

/**
 * 海纳结果Bean
 * @Date: 2023年05月06日
 * @Time: 19时:29分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class DacResultBean {

    private String status;
    private String code;
    private String message;
    private String data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
