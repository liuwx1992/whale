package com.zoo.whale.leetcode;


import com.zoo.whale.leetcode.base.TreeNode;

import java.util.*;

/**
 * @ClassName LevelOrder
 * @Description 二叉树的层序遍历
 * @Author weixin.liu
 * @Date 2026/5/3 9:16
 */
public class LevelOrderNew {
    public static void main(String[] args) {
        //        3
        //       / \
        //      9  20
        //        /  \
        //       15   7
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(9);
        root.right = new TreeNode(20);
        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);

        LevelOrderNew sol = new LevelOrderNew();
        List<List<Integer>> res = sol.levelOrder(root);
        System.out.println(res);
        System.out.println("当前实现逐节点成列表，期望: [[3], [9], [20], [15], [7]]");

        System.out.println("空树: " + sol.levelOrder(null));
    }


    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            List<Integer> level = new ArrayList<>();
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode current = queue.poll();
                if (current == null) {
                    continue;
                }
                level.add(current.val);
                if (current.left != null) {
                    queue.offer(current.left);
                }
                if (current.right != null) {
                    queue.offer(current.right);
                }
            }
            result.add(level);
        }
        return result;
    }

}
