package com.zoo.whale.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @Date: 2026年 02月 15日
 * @Time:08时:56分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class PreorderRecursive {

    public static void main(String[] args) {
        TreeNode root = BuildBinaryTree.buildTree();
        List<Integer> res = new ArrayList<>();
        preorderRecursive(root, res);
        System.out.println(res);
        List<Integer> integers = preorderIterative(root);
        System.out.println(integers);
    }

    public static void preorderRecursive(TreeNode root, List<Integer> res) {
        if (root == null) {
            return;
        }
        res.add(root.value);
        preorderRecursive(root.left, res);
        preorderRecursive(root.right, res);
    }

    public static List<Integer> preorderIterative(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        if (root == null) {
            return res;
        }
        Stack<TreeNode> stack = new Stack<>();
        stack.add(root);
        while (!stack.isEmpty()) {
            TreeNode pop = stack.pop();
            res.add(pop.value);

            if (pop.right != null) {
                stack.push(pop.right);
            }
            if (pop.left != null) {
                stack.push(pop.left);
            }
        }
        return res;
    }


}
