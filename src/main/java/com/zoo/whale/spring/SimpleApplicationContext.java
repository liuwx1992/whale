package com.zoo.whale.spring;

import cn.hutool.core.lang.ClassScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Date: 2026年 01月 27日
 * @Time:22时:00分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class SimpleApplicationContext {


    private Map<String,BeanDefinition> beanDefinitionMap = new HashMap<>();
    private Map<String,Object> singletonObjects = new HashMap<>();

    public SimpleApplicationContext(Class<?> configClass){
        scan(configClass);
        preInstantiateSingletons();

    }

    private void preInstantiateSingletons() {
    }

    private void scan(Class<?> configClass) {
        ComponentScan scan = configClass.getAnnotation(ComponentScan.class);
        String[] basePackage = scan.value();
        Set<Class<?>> classes = ClassScanner.scanPackage(Arrays.toString(basePackage));

        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Component.class)) {
                String beanName = clazz.getSimpleName();
                beanDefinitionMap.put(beanName, new BeanDefinition(clazz));
            }
        }


    }

    public Object getBean(String beanName) {

        // 1. 先从单例池拿
        Object singleton = singletonObjects.get(beanName);
        if (singleton != null) {
            return singleton;
        }

        // 2. 创建 Bean
        BeanDefinition bd = beanDefinitionMap.get(beanName);
        Object bean = createBean(beanName, bd);

        // 3. 放入单例池
        if (bd.isSingleton()) {
            singletonObjects.put(beanName, bean);
        }
        return bean;
    }

    private Object createBean(String beanName, BeanDefinition bd) {
        Object bean = instantiateBean(bd);
        populateProperties(bean);
        initializeBean(beanName, bean);
        return bean;
    }

    private Object instantiateBean(BeanDefinition bd) {
        Class<?> clazz = bd.getClassBean();

        try {
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            for (Constructor<?> ctor : constructors) {
                if (ctor.getParameterCount() == 0) {
                    return ctor.newInstance();
                }
            }
            throw new RuntimeException("No default constructor");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void populateProperties(Object bean) {
        Class<?> clazz = bean.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Object dependency = getBean(field.getType().getSimpleName());
                try {
                    field.setAccessible(true);
                    field.set(bean, dependency);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void initializeBean(String beanName, Object bean) {
        // BeanNameAware / InitializingBean / @PostConstruct
    }

}
