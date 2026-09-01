package com.zoo.whale.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName:MealBean
 * @Description:
 * @author:weixin.liu
 * @Date:2020/8/21
 * @Time:18:05
 * @Version；
 */

public class MealBean {

    private String meal;

    private String ver;

    private String name;

    private String recall;

    private List<Set<String>> checkList;

    private Map<String, Set<String>> dependence = new HashMap<>();

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRecall() {
        return recall;
    }

    public void setRecall(String recall) {
        this.recall = recall;
    }

    public List<Set<String>> getCheckList() {
        return checkList;
    }

    public void setCheckList(List<Set<String>> checkList) {
        this.checkList = checkList;
    }

    public Map<String, Set<String>> getDependence() {
        return dependence;
    }

    public void setDependence(Map<String, Set<String>> dependence) {
        this.dependence = dependence;
    }
}

