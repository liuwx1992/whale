package com.zoo.whale.thread;

/**
 * @Date: 2025年 07月 09日
 * @Time:20时:34分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class DaemonTest {


    public static void main(String[] args) {
        System.out.println("main enter");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("executing");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        thread.setDaemon(true);

        thread.start();

        System.out.println("main exit");
    }
}
