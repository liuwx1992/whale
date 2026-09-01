package com.zoo.whale.leetcode;


/**
 * @Date: 2022年10月10日
 * @Time: 15时:03分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class ReverseList {

    public static void main(String[] args) {
        // 初始化链表
        ListNode head = createList(new int[]{1, 2, 6, 3, 4, 5, 6});

        // 打印链表
        printList(head);

        System.out.println();

        ListNode listNode = reverseList(head);

        System.out.println();

        // 打印链表
        printList(listNode);
    }

    public static ListNode reverseList(ListNode head) {
        ListNode pre = null;
        ListNode curr = head;
        while (curr != null){
            ListNode next = curr.next;
            curr.next = pre;
            pre = curr;
            curr = next;
        }
        return pre;
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
}
