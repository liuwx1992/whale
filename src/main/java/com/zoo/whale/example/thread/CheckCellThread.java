package com.zoo.whale.example.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * 执行CheckCell
 *
 * @ClassName:CheckCellThread
 * @Description:执行CheckCell
 * @Author:weixin.liu
 * @Date:2020/9/17
 * @Time:18:19
 * @Version:1.0
 */
public class CheckCellThread implements Callable<String> {

    private static final Logger logger = LoggerFactory.getLogger(CheckCellThread.class);

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
     */
    public CheckCellThread(String request, int cell) {
        this.cell = cell;
        this.request = request;
    }

    @Override
    public String call() {
        String value = "1";
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            logger.info("CheckCellThreadException:", e);
        }
        return value;
    }
}
