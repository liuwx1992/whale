package com.zoo.whale.list;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2023年03月27日
 * @Time: 20时:08分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class ListDemo {

    public static void main(String[] args) {
        List<Integer> belongMonths = new ArrayList<>();
        belongMonths.add(1);
        belongMonths.add(3);
        belongMonths.add(4);
        belongMonths.add(6);
        belongMonths.add(10);
        belongMonths.add(10);
        belongMonths.add(9);
        belongMonths.add(5);

        belongMonths.sort(Integer::compareTo);

        for (Integer i : belongMonths){
            System.out.print(i + " ,");
        }

        int max = 0;
        int cur = 0;
        for (int i = 0;i < belongMonths.size();i++){
            int month = belongMonths.get(i);
            if(i == 0){
                cur = month;
                continue;
            }
            if(month - cur <= 1){
                max = max + (month - cur);
            }
            cur = month;
        }

        System.out.println("连续:"+max);

    }

}
