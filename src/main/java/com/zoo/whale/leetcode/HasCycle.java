package com.zoo.whale.leetcode;

public class HasCycle {

    public static void main(String[] args) {
        // 初始化链表
        ListNode head = createList(new int[]{1, 2, 6, 3, 4, 5, 6});

        // 打印链表
        printList(head);

        System.out.println();

        boolean listNode = hasCycle(head);

        System.out.println(listNode);
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


    public static boolean hasCycle(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }
        ListNode fast = head;
        ListNode slow = head;
        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;
            if (slow == fast) {
                return true;
            }
        }
        return false;
    }
}
