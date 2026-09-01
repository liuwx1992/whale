package com.zoo.whale.thread;

/**
 * @Date: 2026年 02月 26日
 * @Time:22时:16分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class WaitDemo {

    public static Object object = new Object();

    public static void main(String[] args) {
        synchronized (object){
            System.out.println("enter wait");
            try {
                object.wait();
            } catch (InterruptedException e) {
                //throw new RuntimeException(e);
                System.out.println("InterruptedException");
            }
            System.out.println("running");
        }
    }
}
