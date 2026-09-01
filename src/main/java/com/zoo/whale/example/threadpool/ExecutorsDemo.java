package com.zoo.whale.example.threadpool;

import java.util.concurrent.*;

/**
 * @ClassName:ExecutorsDemo
 * @Description:
 * @Author:weixin.liu
 * @Date:2022/3/30
 * @Time:17:17
 * @Version；1.0
 */
public class ExecutorsDemo {

    private static ThreadPoolExecutor writeThreadPoolExecutor = new ThreadPoolExecutor(15, 50, 0,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(300), Executors.defaultThreadFactory());

    /**
     * 异步写入Hbase数据
     *
     * @param result 结果
     */
    public static void writeToHbaseAsynchronous(final String result) {
        /*FutureTask<Integer> task = new FutureTask<>(new HbaseCacheTask(result));
        writeThreadPoolExecutor.submit(task);*/
        try {
            //int ret = task.get();
            /*if(ret != 0){
                System.out.println("异常了！！！");
            }else {
                System.out.println("存储正常！");
            }*/
            System.out.println("存储正常！");

            double d = 1285424 / (1024 * 1024);
            double d2 = 1024 * 1024;
            System.out.println(d);
            System.out.println(d2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        writeToHbaseAsynchronous("2");
        writeToHbaseAsynchronous("1");
    }

}
