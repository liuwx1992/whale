package com.zoo.whale.jdk;

import java.util.concurrent.TimeUnit;

/**
 * @Date: 2023年 12月 01日
 * @Time:16时:43分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class ThreadDaemonDemo {

    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("----我是子线程，每隔一秒打印一次，就是玩儿-----");
            }
        });
        thread.setDaemon(true);//true 确保主方法结束时候，子线程随之结束（默认是false）

        thread.start();

        try {
            TimeUnit.SECONDS.sleep(3);
            //让主线程休息一会儿，给子线程充足的执行时间，否则可能造成因主线程执行速度过快，主线程结束了子线程还没来得及执行
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //thread.interrupt();
        //thread.interrupt();
        Thread.currentThread().interrupt();

        try {
            TimeUnit.SECONDS.sleep(3);
            //让主线程休息一会儿，给子线程充足的执行时间，否则可能造成因主线程执行速度过快，主线程结束了子线程还没来得及执行
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("主线程已结束");
        //只有new(新创建)和terminated(死亡)状态的线程下的isAlive()返回值才是false
        // 其他waiting、blocked、runable、running状态下，isAlive()返回值都是true
        System.out.println("子线程状态："+thread.getState());
        System.out.println("是否依然存活："+thread.isAlive());
        System.out.println("是否是守护线程："+thread.isDaemon());




    }
}