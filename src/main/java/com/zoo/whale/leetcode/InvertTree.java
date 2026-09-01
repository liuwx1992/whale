package com.zoo.whale.leetcode;


import com.zoo.whale.tree.BuildBinaryTree;
import com.zoo.whale.tree.TreeNode;
/**
 * 翻转二叉树
 * 给你一棵二叉树的根节点 root ，翻转这棵二叉树，并返回其根节点。
 * */
public class InvertTree {

    public static void main(String[] args) {
        //       4
        //     /   \
        //    2     7
        //   / \   / \
        //  1   3 6   9
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(7);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);
        root.right.left = new TreeNode(6);
        root.right.right = new TreeNode(9);

        System.out.println("翻转前（层序）:");
        BuildBinaryTree.levelOrderPrint(root);

        invertTree(root);

        System.out.println("翻转后（层序，期望 4 7 2 9 6 3 1）:");
        BuildBinaryTree.levelOrderPrint(root);
    }


    public static TreeNode invertTree(TreeNode root) {
        if (root.left != null) {
            invertTree(root.left);
        }
        if (root.right != null) {
            invertTree(root.right);
        }
        TreeNode temp = root.right;
        root.right = root.left;
        root.left = temp;
        return root;
    }

}
