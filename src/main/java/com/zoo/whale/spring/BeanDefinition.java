package com.zoo.whale.spring;

/**
 * @Date: 2026年 01月 27日
 * @Time:21时:56分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class BeanDefinition {


    private Class<?> beanClass;

    private String scope = "singleton";

    public BeanDefinition(Class<?> beanClass){
        this.beanClass = beanClass;
    }

    public Class<?> getClassBean(){
        return beanClass;
    }

    public boolean isSingleton(){
        return "singleton".equals(scope);
    }

    // getter / setter



}
