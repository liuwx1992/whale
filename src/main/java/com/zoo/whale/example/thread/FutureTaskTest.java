package com.zoo.whale.example.thread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @ClassName:FutureTaskTest
 * @Description:
 * @Author:weixin.liu
 * @Date:2021/9/14
 * @Time:17:39
 * @Version；1.0
 */
public class FutureTaskTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /*ExecutorService pool2 = Executors.newSingleThreadExecutor();
        Future result2 = pool2.submit(new FutureTask<String>(
                new Callable<String>() {
                    public String call() throws Exception {
                        return "TEST";
                    }
                }));
        System.out.println(result2.get());
        String res2 = (String) result2.get();
        Thread.sleep(5000);
        System.out.println(res2);// 结果怎么是null，我以为是 "TEST" 呢*/

        ExecutorService pool2 = Executors.newSingleThreadExecutor();
        FutureTask<String> task = new FutureTask<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "TEST";
            }
        });

        Future result2 = pool2.submit(task);
        Thread.sleep(100);
        if (task.isDone()) {
            System.out.println(task.get());
            System.out.println(result2.get());
        } else {
            System.err.println("Error executing task");
        }


        ExecutorService executor2 = Executors.newFixedThreadPool(5);
        class Task implements Callable<String> {
            @Override
            public String call() throws Exception {

                Random rand = new Random();
                TimeUnit.SECONDS.sleep(rand.nextInt(10));
                return Thread.currentThread().getName();
            }
        }

        List<Future<String>> results = new ArrayList<Future<String>>();
        for (int i = 0; i < 5; i++) {
            Future<String> f = executor2.submit(new Task());
            results.add(f);
        }

        boolean flag = true;
        while (flag) {
            for (Iterator<Future<String>> iter = results.iterator(); iter.hasNext(); ) {
                Future<String> f = iter.next();
                if (f.isDone()) {
                    System.out.println(f.get());
                    iter.remove();
                }
            }
            if (results.size() == 0) {
                flag = false;
            }
        }

        System.out.println("执行完毕");

        executor2.shutdownNow();
    }


}
