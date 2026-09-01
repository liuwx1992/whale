package com.zoo.whale.leetcode.tree;

import java.util.*;

/**
 * @Date: 2026年 03月 06日
 * @Time:19时:25分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class ZigzagLevelOrder {

    public static void main(String[] args) {
        test03();
    }

    private static void test01() {
        TreeNode treeNode = new TreeNode(2);
        TreeNode treeNode1 = new TreeNode(1);
        TreeNode treeNode2 = new TreeNode(3);

        treeNode.left = treeNode1;
        treeNode.right = treeNode2;

        List<List<Integer>> result = zigzagLevelOrder(treeNode);
        System.out.println(result);
    }

    private static void test02() {
        TreeNode treeNode = new TreeNode(0);
        TreeNode treeNode1 = new TreeNode(-1);
        treeNode.left = treeNode1;
        List<List<Integer>> result = zigzagLevelOrder(treeNode);
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

        List<List<Integer>> result = zigzagLevelOrder(treeNode);
        System.out.println(result);
    }

    private static void test04() {
        TreeNode treeNode = new TreeNode(2);
        TreeNode treeNode1 = new TreeNode(1);
        TreeNode treeNode2 = new TreeNode(3);

        treeNode.left = treeNode1;
        treeNode.right = treeNode2;

        List<List<Integer>> result = zigzagLevelOrder(treeNode);
        System.out.println(result);
    }

    private static void test05() {
        TreeNode treeNode = new TreeNode(2147483647);

        List<List<Integer>> result = zigzagLevelOrder(treeNode);
        System.out.println(result);
    }

    public static List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();

        if (root == null) {
            return result;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean isOrderLeft = false;
        while (!queue.isEmpty()) {
            Deque<Integer> levelList  = new LinkedList<>();
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if(isOrderLeft){
                    levelList.offerFirst(node.val);
                } else {
                    levelList.offerLast(node.val);
                }
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
            isOrderLeft = !isOrderLeft;
            result.add(new ArrayList<>(levelList));
        }
        return result;
    }

    /*public static List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if(root == null){
            return result;
        }
        Deque<TreeNode> nodeQueue = new LinkedList<>();
        nodeQueue.offer(root);
        boolean isOrderLeft = false;
        while (!nodeQueue.isEmpty()){
            Deque<Integer> levelList  = new LinkedList<>();
            int size = nodeQueue.size();
            for (int i = 0;i < size;i++){
                TreeNode curNode = nodeQueue.poll();
                if(isOrderLeft){
                    levelList.offerFirst(curNode.val);
                }else {
                    levelList.offerLast(curNode.val);
                }
                if(curNode.left != null){
                    nodeQueue.offer(curNode.left);
                }
                if(curNode.right != null){
                    nodeQueue.offer(curNode.right);
                }
            }
            isOrderLeft = !isOrderLeft;
            result.add(new ArrayList<>(levelList));
        }
        return result;
    }*/

}
