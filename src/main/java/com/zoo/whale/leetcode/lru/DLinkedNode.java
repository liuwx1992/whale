package com.zoo.whale.leetcode.lru;

/**
 * @Date: 2026年 03月 28日
 * @Time:22时:46分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class DLinkedNode {



    int key;
    int value;

    DLinkedNode prev;

    DLinkedNode next;

    public DLinkedNode(){
    }

    public DLinkedNode(int key, int value){
        this.key = key;
        this.value = value;
    }

}
