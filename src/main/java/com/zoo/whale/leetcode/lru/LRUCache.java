package com.zoo.whale.leetcode.lru;

import cn.hutool.db.handler.HandleHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * @Date: 2026年 03月 28日
 * @Time:22时:11分
 * @Author: weixin.liu
 * @Version:V1.0
 *
 * 新节点 → addToHead
 * 老节点 → moveToHead
 * addToHead 顺序：先自己，再别人
 */
public class LRUCache {

    class DLinkedNode {
        int key;
        int value;
        DLinkedNode prev;
        DLinkedNode next;

        public DLinkedNode() {
        }

        public DLinkedNode(int _key, int _value) {
            key = _key;
            value = _value;
        }
    }

    private int capacity;
    private int size;

    private DLinkedNode tail;
    private DLinkedNode head;


    private Map<Integer, DLinkedNode> hashMap = new HashMap<>();

    public LRUCache(int capacity) {
        size = 0;
        this.capacity = capacity;
        // 使用伪头部和伪尾部节点
        head = new DLinkedNode();
        tail = new DLinkedNode();
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        DLinkedNode node = hashMap.get(key);
        if (node == null) {
            return -1;
        }
        moveToHead(node);
        return node.value;
    }

    public void put(int key, int value) {
        DLinkedNode node = hashMap.get(key);
        if (node == null) {
            DLinkedNode newNode = new DLinkedNode(key, value);
            hashMap.put(key, newNode);
            addToHead(newNode);
            size++;
            if (size > capacity) {
                DLinkedNode removeTail = removeTail();
                hashMap.remove(removeTail.key);
                size--;
            }
        } else {
            node.value = value;
            moveToHead(node);
        }
    }

    private void addToHead(DLinkedNode node) {
        //1 先操作自己，再操作其他节点
        node.next = head.next;
        node.prev = head;

        //
        head.next.prev = node;
        head.next = node;
    }

    private void moveToHead(DLinkedNode dLinkedNode) {
        removeNode(dLinkedNode);
        addToHead(dLinkedNode);
    }


    private void removeNode(DLinkedNode dLinkedNode) {
        dLinkedNode.prev.next = dLinkedNode.next;
        dLinkedNode.next.prev = dLinkedNode.prev;
    }

    private DLinkedNode removeTail() {
        DLinkedNode res = tail.prev;
        removeNode(res);
        return res;
    }


}
