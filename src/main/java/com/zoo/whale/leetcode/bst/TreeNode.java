package com.zoo.whale.leetcode.bst;

/**
 * @Date: 2026年 02月 23日
 * @Time:17时:31分
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
