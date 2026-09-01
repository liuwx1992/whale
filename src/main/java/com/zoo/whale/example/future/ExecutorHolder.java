package com.zoo.whale.example.future;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @Date: 2022年09月22日
 * @Time: 15时:43分
 * @Author: weixin.liu
 * @Version: 1.0
 */
@Slf4j
public class ExecutorHolder {

    private static ThreadPoolExecutor writeCacheThreadPoolExecutor;

    static {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("writeCache-thread-%d").build();
        writeCacheThreadPoolExecutor = new ThreadPoolExecutor(1, 4, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(3),namedThreadFactory);
    }

    /**
     * 异步写入Hbase数据
     * */
    public static String writeToHbaseAsynchronous(String req,String response) {
        String ret = null;
        try {
            writeCacheThreadPoolExecutor.submit(new WriteTask(req,response));

            System.out.println("---核心线程数:" + writeCacheThreadPoolExecutor.getCorePoolSize()+",线程池中线程数目："+
                    writeCacheThreadPoolExecutor.getPoolSize()+",队列中等待执行的任务数目："+ writeCacheThreadPoolExecutor.getQueue().size());

            //System.out.println("---线程池中线程数目："+exs.getPoolSize());
            //System.out.println("---队列中等待执行的任务数目："+ exs.getQueue().size());

            for(int i=0;i<11;i++){
                //通过 ExecutorService 对象的 execute 方法将任务添加到线程去执行
                writeCacheThreadPoolExecutor.submit(new WriteTask(req,response));
                System.out.println("---核心线程数:" + writeCacheThreadPoolExecutor.getCorePoolSize()+",线程池中线程数目："+
                        writeCacheThreadPoolExecutor.getPoolSize()+",队列中等待执行的任务数目："+ writeCacheThreadPoolExecutor.getQueue().size());

            }


           /* Future<String> submit = writeCacheThreadPoolExecutor.submit(new WriteTask(req,response));
            try {
                ret = submit.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }*/
        }catch (Exception e){
            log.error("线程池执行异常:",e);
            WriteTask.writeFileToLocation(req,"J:\\pictures\\","test.txt");
        }
        return ret;
    }

}
