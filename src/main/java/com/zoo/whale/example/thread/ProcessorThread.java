package com.zoo.whale.example.thread;

import java.util.concurrent.Callable;

/**
 * @ClassName:ProcessorThread
 * @Description:
 * @Author:weixin.liu
 * @Date:2020/9/16
 * @Time:21:29
 * @Version；1.0
 */
public class ProcessorThread implements Callable {
    private String name;
    private int num;

    public ProcessorThread(String name, int num) {
        this.name = name;
        this.num = num;
    }


    @Override
    public Object call() throws Exception {
        int result = 0;
        for (int i = 0; i <= num * 10; i++) {
            result += i;
        }
        Thread.sleep(num * 10);
        System.out.println(num + "叠加结果：" + result);
        return result;
    }
}
