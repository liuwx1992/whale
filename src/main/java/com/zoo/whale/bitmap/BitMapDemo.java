package com.zoo.whale.bitmap;

/**
 * @Date: 2026年 01月 16日
 * @Time:17时:38分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class BitMapDemo {

    private byte[] bits;

    private long maxValue;


    public BitMapDemo(long maxValue){
        if (maxValue < 0) {
            throw new IllegalArgumentException("maxValue must be >= 0");
        }
        this.maxValue = maxValue;
        this.bits = new byte[(int)((maxValue + 7) / 8)];
    }

    public void add(long value) {
        checkRange(value);
        int byteIndex = (int)(value/8);
        int bitIndex = (int)(value % 8);    // value % 8
        bits[byteIndex] = (byte) (bits[byteIndex] | (1 << bitIndex));
    }

    private void checkRange(long value) {
        if (value < 0 || value > maxValue) {
            throw new IndexOutOfBoundsException("value out of range");
        }
    }


}
