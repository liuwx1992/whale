package com.zoo.whale.example.test;

import com.zoo.whale.example.enums.Signal;

import static com.zoo.whale.example.enums.Signal.*;

/**
 * @Date: 2023年04月06日
 * @Time: 11时:13分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class TrafficLight {

    public static void main(String[] args) {
        change();
    }


    public static void change() {
        Signal color = RED;
        switch(color) {
            case RED:
                color = GREEN;
                break;
            case YELLOW:
                color = RED;
                break;
            case GREEN:
                color = YELLOW;
                break;
        }
        System.out.println(color);
    }
}
