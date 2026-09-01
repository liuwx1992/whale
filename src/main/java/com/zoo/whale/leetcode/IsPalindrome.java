package com.zoo.whale.leetcode;

import java.util.ArrayList;
import java.util.List;

public class IsPalindrome {
    public static void main(String[] args) {
        // 初始化链表
        ListNode head = createList(new int[]{1, 2, 2, 1});

        // 打印链表
        printList(head);

        System.out.println();

        boolean listNode = isPalindrome(head);

        // 打印链表
        System.out.println(listNode);;
    }


    // 打印链表的方法
    public static void printList(ListNode node) {
        while (node != null) {
            System.out.print(node.val + " ");
            node = node.next;
        }
    }

    // 创建链表的方法
    public static ListNode createList(int[] values) {
        if (values == null || values.length == 0) {
            return null;
        }

        ListNode head = new ListNode(values[0]);
        ListNode current = head;

        for (int i = 1; i < values.length; i++) {
            current.next = new ListNode(values[i]);
            current = current.next;
        }

        return head;
    }


    public static boolean isPalindrome(ListNode head) {
        List<Integer> list = new ArrayList<>();
        while (head != null) {
            list.add(head.val);
            head = head.next;
        }
        int start = 0;
        int end = list.size() - 1;
        while (start <= end) {
            if(list.get(start) != list.get(end)){
                return false;
            }
            start++;
            end--;
        }
        return true;
    }
}
