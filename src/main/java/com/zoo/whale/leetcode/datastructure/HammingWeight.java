package com.zoo.whale.leetcode.datastructure;

/**
 * @Date: 2022年09月21日
 * @Time: 20时:30分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class HammingWeight {




    public int hammingWeight(int n) {
        int ret = 0;
        for (int i = 0; i < 32; i++) {
            if ((n & (1 << i)) != 0) {
                ret++;
            }
        }
        return ret;
    }
}
