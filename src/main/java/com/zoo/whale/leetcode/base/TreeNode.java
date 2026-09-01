package com.zoo.whale.leetcode.base;

/**
 * @ClassName:TreeNode
 * @Description:Definition for a binary tree node.
 * @Author:weixin.liu
 * @Date:2022/1/26
 * @Time:11:11
 * @Version；1.0
 */
public class TreeNode {

    public int val;
    public TreeNode left;
    public TreeNode right;

    public TreeNode() {
    }

    public TreeNode(int val) {
        this.val = val;
    }

    public TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}
