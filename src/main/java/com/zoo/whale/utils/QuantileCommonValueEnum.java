package com.zoo.whale.utils;

/**
 * 分位数常用值
 */
public enum QuantileCommonValueEnum {

    P50("p50", "50"),
    P90("p90", "90"),
    P99("p99", "99"),
    P999("p999", "99.9");


    QuantileCommonValueEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    private String code;

    private String value;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
