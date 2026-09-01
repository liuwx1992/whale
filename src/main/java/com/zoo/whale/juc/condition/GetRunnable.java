package com.zoo.whale.juc.condition;

/**
 * @Date: 2024年 01月 25日
 * @Time:17时:26分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class GetRunnable implements Runnable {

    ConditionTest conditionTest;


    public GetRunnable(ConditionTest conditionTest) {
        this.conditionTest = conditionTest;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(50000);
                String s = conditionTest.get();
                System.out.println(s);
            }
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }
}
