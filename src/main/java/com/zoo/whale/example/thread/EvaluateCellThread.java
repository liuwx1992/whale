package com.zoo.whale.example.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * @ClassName:EvaluateCellThread
 * @Description:
 * @Author:weixin.liu
 * @Date:2020/9/17
 * @Time:18:16
 * @Version；1.0
 */
public class EvaluateCellThread implements Callable<String> {

    private static final Logger logger = LoggerFactory.getLogger(EvaluateCellThread.class);

    /**
     * request参数
     */
    private String request;
    /**
     * cell
     */
    private int cell;

    /**
     * constructor
     *
     * @param cell
     * @param request
     */
    public EvaluateCellThread(String request, int cell) {
        this.cell = cell;
        this.request = request;
    }

    @Override
    public String call() {
        String value = "1";
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            logger.info("EvaluateCellThreadException:", e);
        }
        return value;
    }
}
