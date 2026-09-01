package com.zoo.whale.example.thread;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName:Demo
 * @Description:
 * @Author:weixin.liu
 * @Date:2020/12/2
 * @Time:16:44
 * @Version；1.0
 */
public class Demo {

    private static ExecutorService executorPool = Executors.newCachedThreadPool();

    public static void main(String[] args) {

        List<JSONObject> jsonDataList = new ArrayList<>();
        setData(jsonDataList);


        PercentCache.getInstance().initPercentMap(35L);
        AdjustableSemaphore semaphore = AdjustableSemaphore.getInstance();
        int pageSize = semaphore.getMaxSize();
//        int pageSize = 1000;
        int total = jsonDataList.size();
        int pageCount = total / pageSize + 1;

        executorPool.submit(new Runnable() {
            @Override
            public void run() {
                CountDownLatch countDownLatch = new CountDownLatch(pageCount);

                Map<Long, AtomicInteger> percentMap = PercentCache.getInstance().getMap();
                for (int i = 0; i < pageCount; i++) {
                    int fromIndex = i * pageSize;
                    int toIndex = (i + 1) * pageSize;
                    if (total < toIndex) {
                        toIndex = total;
                    }
                    List<JSONObject> subList = jsonDataList.subList(fromIndex, toIndex);
                    executorPool.execute(new FileQueryProcessFuture(subList, countDownLatch, semaphore));
                }
                try {
                    //等待执行完
                    countDownLatch.await();
                    System.out.println("上传路径文件名称");
                    int sucCount = 0;
                    if (percentMap.containsKey(35L)) {
                        sucCount = percentMap.get(35L).get();
                    }
                    System.out.println("sucCount====================" + sucCount);

                } catch (Exception e) {
                    System.out.println("处理数据异常返回 {} ");
                }
            }
        });
    }

    private static void setData(List<JSONObject> jsonDataList) {
        for (int i = 0; i < 170000; i++) {
            JSONObject json = new JSONObject();
            json.put("id", i);
            jsonDataList.add(json);
        }
    }
}
