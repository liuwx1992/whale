package com.zoo.whale.juc.condition;

/**
 * @Date: 2024年 01月 25日
 * @Time:17时:26分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class Set2Runnable implements Runnable {

    ConditionTest conditionTest;


    public Set2Runnable(ConditionTest conditionTest){
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
            conditionTest.set("name");
            conditionTest.set("cell");
            conditionTest.set("id");
            conditionTest.set("1");
            conditionTest.set("2");
            conditionTest.set("3");
            conditionTest.set("4");
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }
}
