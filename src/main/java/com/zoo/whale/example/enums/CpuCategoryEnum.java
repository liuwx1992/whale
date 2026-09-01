package com.zoo.whale.example.enums;

import java.util.ArrayList;
import java.util.List;

public enum CpuCategoryEnum {

    zero("Ryzen", "AMD", "3999"),

    one("Tualatin", "Intel", "4999"),

    two("Xeon Max", "Intel", "5999");

    private String name;

    private String brand;

    private String price;

    private static final List<String> ELSE = new ArrayList<>();

    static {
        ELSE.add("HBM");
        ELSE.add("W-3400");
        ELSE.add("CPU Max");
    }

    CpuCategoryEnum(String name, String brand, String price) {
        this.name = name;
        this.brand = brand;
        this.price = price;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public static String getDetail(String name) {
        for (CpuCategoryEnum categoryEnum : values()) {
            if (categoryEnum.getName().equals(name)) {
                return categoryEnum.getName() + ":" + "品牌:" + categoryEnum.getBrand() + ",售价:" + categoryEnum.getPrice();
            }
        }
        if(ELSE.contains(name)){
            return "ELSE";
        }
        return null;
    }

}
