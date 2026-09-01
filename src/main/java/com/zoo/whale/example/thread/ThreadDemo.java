package com.zoo.whale.example.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;

/**
 * @ClassName:ThreadDemo
 * @Description:
 * @Author:weixin.liu
 * @Date:2020/9/16
 * @Time:21:29
 * @Version；1.0
 */
public class ThreadDemo {
    public static void main(String[] args) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        List<FutureTask<Integer>> tasks = new ArrayList<>();
        String name = "Tom";
        for (int i = 40; i < 50; i++) {
            int num = i;
            //calculate(num);
            ProcessorThread thread = new ProcessorThread(name, num);
            tasks.add(new FutureTask<>(thread));
        }
        //执行
        for (FutureTask<Integer> task : tasks) {
            new Thread(task).start();
        }
        try {
            //等待服务执行完成
            for (FutureTask<Integer> task : tasks) {
                Integer result = task.get();
                //System.out.println(result);
                if (result == 0) {
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 异常时停止所有任务
            for (FutureTask<Integer> task : tasks) {
                if (task.isDone()) {
                    task.cancel(true);
                }
            }
            tasks.clear();
            tasks = null;
        }
        long entTime = System.currentTimeMillis();
        System.out.println("计算耗时：" + (entTime - startTime) + "ms");

    }

    public static int calculate(int num) throws InterruptedException {
        int result = 0;
        for (int i = 0; i <= num * 10; i++) {
            result += i;
        }
        System.out.println(num + "叠加结果：" + result);
        Thread.sleep(num * 10);
        return result;
    }

}
