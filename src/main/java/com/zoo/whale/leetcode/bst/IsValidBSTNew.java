package com.zoo.whale.leetcode.bst;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName IsValidBSTNew
 * @Description TODO
 * @Author weixin.liu
 * @Date 2026/5/3 12:59
 */
public class IsValidBSTNew {

    public static void main(String[] args) {
        // 合法 BST:    2
        //            / \
        //           1   3
        TreeNode valid = new TreeNode(2);
        valid.left = new TreeNode(1);
        valid.right = new TreeNode(3);
        System.out.println("合法 BST -> " + isValidBST(valid) + " (期望 true)");

        // 非法: 右子树里出现了小于根的值
        //        5
        //       / \
        //      1   4
        //         / \
        //        3   6
        TreeNode invalid = new TreeNode(5);
        invalid.left = new TreeNode(1);
        invalid.right = new TreeNode(4);
        invalid.right.left = new TreeNode(3);
        invalid.right.right = new TreeNode(6);
        System.out.println("非法 BST -> " + isValidBST(invalid) + " (期望 false)");

        System.out.println("null -> " + isValidBST(null) + " (期望 true)");

        TreeNode single = new TreeNode(1);
        System.out.println("单节点 -> " + isValidBST(single) + " (期望 true)");
    }

    private static boolean isValidBST(TreeNode root) {
        List<Integer> list = new ArrayList<>();
        inOrder(root, list);
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i + 1) <= list.get(i)) {
                return false;
            }
        }
        return true;
    }

    private static void inOrder(TreeNode root, List<Integer> list) {
        if (root == null) {
            return;
        }
        inOrder(root.left, list);
        list.add(root.val);
        inOrder(root.right, list);
    }

}
