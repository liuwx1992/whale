package com.zoo.whale.juc.Semaphore;

import java.util.concurrent.Semaphore;

/**
 *
 * FULL PRODUCT VERSION :
 * java version "1.6.0_11"
 * Java(TM) SE Runtime Environment (build 1.6.0_11-b03)
 * Java HotSpot(TM) Client VM (build 11.0-b16, mixed mode, sharing)
 *
 * ADDITIONAL OS VERSION INFORMATION :
 * Windows XP SP3
 *
 * EXTRA RELEVANT SYSTEM CONFIGURATION :
 * P4 2.8HT
 *
 * A DESCRIPTION OF THE PROBLEM :
 * Semaphore initial state 0. 4 threads run 4 tasks.
 * Two threads run acquire semaphore once, the other two threads run release semaphore once.
 * One possible result is the semaphore state value is 1 and one thread still waiting.
 *
 * The possible reason:
 * When AbstractQueuedSynchronizer#release are called, head.waitStatus may be 0 because the previous acquire thread may
 * run at AbstractQueuedSynchronizer#doAcquireShared before setHeadAndPropagate is called.
 *
 * STEPS TO FOLLOW TO REPRODUCE THE PROBLEM :
 * The given executable test case will hung. Suggest using dual-core CPU or HT CPU to reproduce.
 *
 *
 * REPRODUCIBILITY :
 * This bug can be reproduced occasionally.
 * @Date: 2024年 01月 24日
 * @Time:20时:32分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class TestSemaphore {

    private static Semaphore sem = new Semaphore(0);

    private static class Thread1 extends Thread {
        @Override
        public void run() {
            sem.acquireUninterruptibly();
        }
    }

    private static class Thread2 extends Thread {
        @Override
        public void run() {
            sem.release();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10000000; i++) {
            Thread t1 = new Thread1();
            Thread t2 = new Thread1();
            Thread t3 = new Thread2();
            Thread t4 = new Thread2();
            t1.start();
            t2.start();
            t3.start();
            t4.start();
            t1.join();
            t2.join();
            t3.join();
            t4.join();
            System.out.println(i);
        }
    }
}