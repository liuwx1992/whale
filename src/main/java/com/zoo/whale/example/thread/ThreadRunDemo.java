package com.zoo.whale.example.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @Date: 2023年03月12日
 * @Time: 21时:41分
 * @Author: weixin.liu
 * @Version: 1.0
 */
@Slf4j
public class ThreadRunDemo {

    public static volatile BlockingDeque<Runnable> configureTasks = new LinkedBlockingDeque<>();

    public static void main(String[] args) {
        String s = "{\"ss\":\"ss\",ll}";
        ThreadRunDemo.configureTasks.push(new ConfigureTask(s));
        initMethod();
    }

    public static void initMethod()  {
        Runnable runnable = () -> {
            while (true) {
                Runnable event;
                try {
                    event = ThreadRunDemo.configureTasks.take();
                    event.run();
                } catch (InterruptedException ex) {
                    log.error("[disconf同步]中断配置同步线程 ", ex);
                    Thread.currentThread().interrupt();
                }
            }
        };
        Thread thread = new Thread(runnable, "配置同步线程");
        thread.start();
    }

}
