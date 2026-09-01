package com.zoo.whale.leetcode.tree;

/**
 * @Date: 2026年 03月 06日
 * @Time:19时:55分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class TreeNode {


    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}
