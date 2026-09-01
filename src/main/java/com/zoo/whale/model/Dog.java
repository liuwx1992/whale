package com.zoo.whale.model;


import com.zoo.whale.annotations.CareByZookeeper;

@CareByZookeeper(value = "Dog")
public class Dog extends BaseModel {
    final public void run(String name) {
        System.out.println("狗" + name + "----run");
    }

    public void eat(String food) {
        System.out.println("狗----eat" + food);
    }
}
