package com.zoo.whale.example.thread;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * @author huixiang.yu
 * @ClassName: QueryFileParseJob
 * @Desc: 异步任务
 * @date 2019年6月5日
 */
public class FileQueryProcessFuture implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(FileQueryProcessFuture.class);

    //private Semaphore semaphore = new Semaphore(5);

    private JSONObject paramJson;

    private List<JSONObject> dataList;

    private CountDownLatch countDownLatch;

    private AdjustableSemaphore semaphore;

    public FileQueryProcessFuture(List<JSONObject> dataList, CountDownLatch countDownLatch, AdjustableSemaphore semaphore) {
        this.dataList = dataList;
        this.countDownLatch = countDownLatch;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        //boolean permit = true;
        try {
            semaphore.acquire();
            //System.out.println("permit===================="+permit);
            for (JSONObject json : dataList) {
                PercentCache.getInstance().incrementCount(35L);
                //System.out.println("id===================="+json.getString("id"));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            semaphore.release();
            System.out.println("countDownLatch===================1:" + countDownLatch.getCount());
            countDownLatch.countDown();
            System.out.println("countDownLatch===================2:" + countDownLatch.getCount());
        }
    }


}
