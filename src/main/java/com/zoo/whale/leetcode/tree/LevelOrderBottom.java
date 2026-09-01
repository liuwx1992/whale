package com.zoo.whale.leetcode.tree;

import java.util.*;

/**
 * @Date: 2026年 03月 06日
 * @Time:19时:27分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class LevelOrderBottom {

    public static void main(String[] args) {
        test03();
    }

    private static void test01() {
        TreeNode treeNode = new TreeNode(2);
        TreeNode treeNode1 = new TreeNode(1);
        TreeNode treeNode2 = new TreeNode(3);

        treeNode.left = treeNode1;
        treeNode.right = treeNode2;

        List<List<Integer>> result = levelOrderBottom(treeNode);
        System.out.println(result);
    }

    private static void test02() {
        TreeNode treeNode = new TreeNode(0);
        TreeNode treeNode1 = new TreeNode(-1);
        treeNode.left = treeNode1;
        List<List<Integer>> result = levelOrderBottom(treeNode);
        System.out.println(result);
    }

    private static void test03() {
        //[5,4,6,null,null,3,7]
        TreeNode treeNode = new TreeNode(5);
        TreeNode treeNode1 = new TreeNode(4);
        TreeNode treeNode2 = new TreeNode(6);
        TreeNode treeNode5 = new TreeNode(3);
        TreeNode treeNode6 = new TreeNode(7);
        TreeNode treeNode7 = new TreeNode(2);
        TreeNode treeNode8 = new TreeNode(8);

        treeNode.left = treeNode1;
        treeNode.right = treeNode2;

        treeNode2.left = treeNode5;
        treeNode2.right = treeNode6;

        treeNode1.left = treeNode7;
        treeNode1.right = treeNode8;

        List<List<Integer>> result = levelOrderBottom(treeNode);
        System.out.println(result);
    }

    private static void test04() {
        TreeNode treeNode = new TreeNode(2);
        TreeNode treeNode1 = new TreeNode(1);
        TreeNode treeNode2 = new TreeNode(3);

        treeNode.left = treeNode1;
        treeNode.right = treeNode2;

        List<List<Integer>> result = levelOrderBottom(treeNode);
        System.out.println(result);
    }

    private static void test05() {
        TreeNode treeNode = new TreeNode(2147483647);

        List<List<Integer>> result = levelOrderBottom(treeNode);
        System.out.println(result);
    }

    public static List<List<Integer>> levelOrderBottom(TreeNode root) {

        List<List<Integer>> result = new ArrayList<>();

        if(root == null){
            return result;
        }

        Queue<TreeNode> queue = new LinkedList<>();

        queue.offer(root);

        while (!queue.isEmpty()) {
            List<Integer> list = new ArrayList<>();

            int size = queue.size();

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                list.add(node.val);
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
            result.add(0, list);
        }
        return result;
    }

    private static Stack<Integer> levelOrders(TreeNode root, Stack<Integer> stack) {
        if (root == null) {
            return stack;
        }
        stack.push(root.val);
        if (root.left != null) {
            levelOrders(root.left, stack);
        }
        if (root.right != null) {
            levelOrders(root.right, stack);
        }
        return stack;
    }

}
