package com.zoo.whale.thread;

/**
 * @Date: 2025年 07月 09日
 * @Time:20时:42分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class MyThread extends Thread {

    private Object lock = new Object();

    private boolean stop = false;

    public void run(){
        while (!stop) {
            /*synchronized (lock) {

            }*/
            System.out.println("enter wait");
            try {
                lock.wait();
            } catch (InterruptedException e) {
                //throw new RuntimeException(e);
                System.out.println("InterruptedException");
            }
            System.out.println("running");
        }
    }

    public void stopState(){
        System.out.println("stop state");
        stop = true;
    }

    public static void main(String[] args) {
        MyThread myThread = new MyThread();
        myThread.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        myThread.interrupt();

        myThread.stopState();
        try {
            myThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
