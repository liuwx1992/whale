package com.zoo.whale.example.test;

import java.util.Set;
import java.util.TreeSet;

/**
 * @Date: 2023年04月18日
 * @Time: 15时:13分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class CompareDemo {

    public static void main(String[] args) {
        //System.out.println("com001".compareTo("com003"));
        /*Set<Integer> belongMonthSet = new TreeSet<>();
        belongMonthSet.add(1);
        belongMonthSet.add(1);
        belongMonthSet.add(12);
        belongMonthSet.add(4);
        belongMonthSet.add(3);
        belongMonthSet.add(9);
        belongMonthSet.add(6);

        belongMonthSet.stream().sorted(Integer::compare);

        for (int l : belongMonthSet){
            System.out.println(l);
        }*/

        String ss = "20#白蓉云创".toLowerCase();
        System.out.println(ss);

    }


}
