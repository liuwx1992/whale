package com.zoo.whale.leetcode.tree;

import com.zoo.whale.leetcode.base.TreeNode;

/**
 * 自顶向下 -> 先操作根节点（状态已知）-> 前序遍历
 * 自底向上 -> 后操作根节点（状态可能未知）-> 后续遍历
 * @Date: 2022年07月22日
 * @Time: 17时:04分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class MaximumDepth {

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        TreeNode left = new TreeNode(2);
        TreeNode right = new TreeNode(3);
        TreeNode left2 = new TreeNode(4);
        TreeNode right2 = new TreeNode(5);
        root.left = left;
        root.right = right;
        right.left = left2;
        right.right = right2;

        int maximumDepth = maximumDepth(root, 1);
        System.out.println(maximumDepth);

    }

    private static int maximumDepth(TreeNode root, int depth) {
        int answer = 0;
        if(root == null){
            return answer;
        }
        if(root.left == null && root.right == null){
            answer = Math.max(answer,depth);
        }
        maximumDepth(root.left,depth+1);
        maximumDepth(root.right,depth+1);
        return answer;
    }

}
