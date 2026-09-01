package com.zoo.whale.leetcode;

/**
 * @Date: 2024年 11月 28日
 * @Time:16时:27分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class RemoveElements {

    public static void main(String[] args) {
        // 初始化链表
        ListNode head = createList(new int[]{1, 2, 6, 3, 4, 5, 6});

        // 打印链表
        printList(head);

        System.out.println();

        ListNode listNode = removeElements(head, 6);

        System.out.println();

        // 打印链表
        printList(listNode);
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

    // 打印链表的方法
    public static void printList(ListNode node) {
        while (node != null) {
            System.out.print(node.val + " ");
            node = node.next;
        }
    }

    public static ListNode removeElements(ListNode head, int val) {
        if (head == null) {
            return head;
        } else {
            System.out.println(head.val);
        }
        head.next = removeElements(head.next, val);

        System.out.println(head.val);
        return head.val == val ? head.next : head;
    }

    public static ListNode removeElements2(ListNode head, int val) {
        if(head == null){
            return head;
        }
        head.next = removeElements2(head.next,val);
        return head.val == val ? head.next : head;
    }


}
