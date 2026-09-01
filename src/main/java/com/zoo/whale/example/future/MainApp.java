package com.zoo.whale.example.future;


import java.util.concurrent.CountDownLatch;

/**
 * @Date: 2022年09月22日
 * @Time: 15时:50分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class MainApp implements Runnable{

    private String req;
    private String response;

    private CountDownLatch countDownLatch;


    public MainApp(String req,String response,CountDownLatch countDownLatch){
        this.req = req;
        this.response = response;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        doSave();
    }

    private void doSave(){
        String s = ExecutorHolder.writeToHbaseAsynchronous(req, response);
        if(!"89757".equals(s)){
            System.out.println(s);
            System.out.println("not save");
        }
        countDownLatch.countDown();
    }
}
