package com.zoo.whale.example.thread;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @ClassName:FutureTaskDemo
 * @Description:
 * @Author:weixin.liu
 * @Date:2020/9/17
 * @Time:18:49
 * @Version；1.0
 */
public class FutureTaskDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        List<String> cellList = new ArrayList<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 3; i++) {
            int cell = 21;
            // 调用整合API的TelStatus 判断手机号状态，将有效手机号传回
            CheckCellThread checkCellThread = new CheckCellThread("", cell);
            FutureTask<String> checkCellFutureTask = new FutureTask<>(checkCellThread);
            new Thread(checkCellFutureTask).start();

            //执行
            EvaluateCellThread evaluateCellThread = new EvaluateCellThread("", cell);
            FutureTask<String> evaluateFutureTask = new FutureTask<>(evaluateCellThread);
            new Thread(evaluateFutureTask).start();

            //获取结果
            String checkCellValue = checkCellFutureTask.get();
            String evaluateValue = evaluateFutureTask.get();

            boolean result = "1".equals(checkCellValue) && "1".equals(evaluateValue);
            if (result) {
                cellList.add(cell + "");
                break;
            }
        }
        System.out.println(cellList);
        System.out.println(System.currentTimeMillis() - start);
    }


}
