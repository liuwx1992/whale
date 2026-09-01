package com.zoo.whale.example.bus;

import com.zoo.whale.example.map.MyBloomFilter;

/**
 * Description: TODO
 *
 * @Author: weixin.liu
 * @CreateTime: 2022-07-01  15:39
 * @Version: 1.0
 */
public class BloomFilterTest {
    public static void main(String[] args) {
        String value1 = "https://javaguide.cn/";
        String value2 = "https://github.com/Snailclimb";
        MyBloomFilter filter = new MyBloomFilter();
        System.out.println(filter.contains(value1));
        System.out.println(filter.contains(value2));
        filter.add(value1);
        filter.add(value2);
        System.out.println(filter.contains(value1));
        System.out.println(filter.contains(value2));
    }
}
