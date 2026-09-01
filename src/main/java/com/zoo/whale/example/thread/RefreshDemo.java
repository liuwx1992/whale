package com.zoo.whale.example.thread;

/**
 * @Date: 2023年03月12日
 * @Time: 22时:06分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class RefreshDemo {

    public static void main(String[] args) {
        refresh();
    }
    public static void refresh() {
        String s = "{\"ss\":\"ss\"}";
        ThreadRunDemo.configureTasks.push(new ConfigureTask(s));
    }
}
