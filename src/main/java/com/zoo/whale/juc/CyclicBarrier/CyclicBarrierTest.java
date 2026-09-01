package com.zoo.whale.juc.CyclicBarrier;

import java.util.concurrent.CyclicBarrier;

/**
 * @Date: 2024年 01月 18日
 * @Time:14时:18分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class CyclicBarrierTest {

    static CyclicBarrier c = new CyclicBarrier(2);

    public static void main(String[] args) {
        int numberWaiting = c.getNumberWaiting();
        int parties = c.getParties();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    c.await();
                } catch (Exception e) {

                }
            }
        }).start();
        System.out.println(1);
        try {
            c.await();
        } catch (Exception e) {

        }
        System.out.println(2);
    }


}
