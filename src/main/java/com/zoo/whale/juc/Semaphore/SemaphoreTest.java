package com.zoo.whale.juc.Semaphore;

import java.util.concurrent.Semaphore;

/**
 * @Date: 2024年 01月 18日
 * @Time:17时:31分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class SemaphoreTest {
    static class Parking{
        //信号量
        private Semaphore semaphore;

        Parking(int count){
            semaphore = new Semaphore(count);
        }

        public void park(){
            try {
                //获取信号量
                semaphore.acquire();
                long time = (long) (Math.random() * 10);
                System.out.println(Thread.currentThread().getName() + "进入停车场，停车" + time + "秒..." );
                Thread.sleep(time);
                System.out.println(Thread.currentThread().getName() + "开出停车场...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        }
    }

    static class Car extends Thread {
        Parking parking ;

        Car(Parking parking){
            this.parking = parking;
        }

        @Override
        public void run() {
            parking.park();     //进入停车场
        }
    }

    public static void main(String[] args){
        Parking parking = new Parking(2);

        for(int i = 0 ; i < 5 ; i++){
            new Car(parking).start();
        }
    }
}
