package com.zoo.whale.thread.stop;

/**
 * @Date: 2026年 02月 19日
 * @Time:19时:02分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class InterruptedDemo {

    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.interrupted()) {
                    System.out.println("线程被中断了");
                    break;
                } else {
                    System.out.println("线程执行中...");
                }
            }
        });

        t1.start();

        Thread.sleep(100);

        t1.interrupt();

    }

}
