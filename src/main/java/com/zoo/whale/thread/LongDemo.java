package com.zoo.whale.thread;

/**
 * @Date: 2025年 07月 10日
 * @Time:19时:50分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class LongDemo {

    static LongDemo longDemo = new LongDemo();

    private long a = 0;

    public long getA() {
        return a;
    }

    public void setA(long a) {
        this.a = a;
    }

    public static void main(String[] args) {
        Thread thread01 = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i < 1000) {
                    longDemo.setA(100);
                    i++;
                }
            }
        });

        Thread thread02 = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i < 1000) {
                    if(longDemo.getA() != 100){
                        System.out.println("A=" + longDemo.getA());
                    }
                    i++;
                }
            }
        });

        thread01.start();
        thread02.start();

    }

}
