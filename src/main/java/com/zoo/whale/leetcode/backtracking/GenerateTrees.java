package com.zoo.whale.leetcode.backtracking;

import java.util.*;


public class GenerateTrees {

    public static void main(String[] args) {
        List<TreeNode> treeNodes = generateTrees(3);

        for (TreeNode treeNode : treeNodes) {
            List<Integer> list = new ArrayList<>();
            levelOrder(treeNode, list);
            System.out.println(list);
        }
    }

    /*public static List<TreeNode> generateTrees(int n) {
        List<TreeNode> trees = new LinkedList<>();
        if (n < 1) {
            return trees;
        }
        return generateTrees(1, n);
    }

    private static List<TreeNode> generateTrees(int start, int end) {
        List<TreeNode> allTrees = new LinkedList<>();
        if (start >= end) {
            allTrees.add(null);
            return allTrees;
        }
        for (int i = start; i < end; i++) {

            List<TreeNode> leftTrees = generateTrees(start, i - 1);

            List<TreeNode> rightTrees = generateTrees(i + 1, end);

            // 从左子树集合中选出一棵左子树，从右子树集合中选出一棵右子树，拼接到根节点上
            for (TreeNode left : leftTrees) {
                for (TreeNode right : rightTrees) {
                    TreeNode currTree = new TreeNode(i);
                    currTree.left = left;
                    currTree.right = right;
                    allTrees.add(currTree);
                }
            }
        }
        return allTrees;
    }*/

    public static List<TreeNode> generateTrees(int n) {
        List<TreeNode> allTrees = new LinkedList<>();
        if (n < 1) {
            return allTrees;
        }
        return generateTrees(1, n);
    }

    public static List<TreeNode> generateTrees(int start, int end) {
        List<TreeNode> allTrees = new LinkedList<>();
        if (start > end) {
            allTrees.add(null);
            return allTrees;
        }

        for (int i = start; i <= end; i++) {

            List<TreeNode> leftTrees = generateTrees(start, i - 1);

            List<TreeNode> rightTrees = generateTrees(i + 1, end);

            for (TreeNode left : leftTrees) {
                for (TreeNode right : rightTrees) {
                    TreeNode currentNode = new TreeNode(i);
                    currentNode.left = left;
                    currentNode.right = right;

                    allTrees.add(currentNode);
                }
            }
        }
        return allTrees;
    }

    public static void inOrder(TreeNode treeNode, List<Integer> list) {
        if (treeNode == null) {
            return;
        }
        if (treeNode.left != null) {
            inOrder(treeNode.left, list);
        }
        list.add(treeNode.val);
        if (treeNode.right != null) {
            inOrder(treeNode.right, list);
        }
    }

    public static void levelOrder(TreeNode root, List<Integer> list) {
        if (root == null) {
            list.add(null);
            return;
        }
        Queue<TreeNode> queue = new LinkedList<>();

        queue.offer(root);

        while (!queue.isEmpty()) {

            TreeNode poll = queue.poll();

            if (poll == null) {
                list.add(null);
                continue;
            }
            list.add(poll.val);

            queue.offer(poll.left);
            queue.offer(poll.right);
        }
    }

}
