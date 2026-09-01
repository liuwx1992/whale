package com.zoo.whale.leetcode.tree;

import com.zoo.whale.leetcode.base.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName:PreorderTraversal
 * @Description:
 * @Author:weixin.liu
 * @Date:2022/1/27
 * @Time:16:59
 * @Version；1.0
 */
public class PreorderTraversal {


    public static List<Integer> preorderTraversal01(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        List<Integer> leftResult = new ArrayList<>();
        List<Integer> rightResult = new ArrayList<>();
        if (root == null) {
            return result;
        }
        if (root != null) {
            result.add(root.val);
        }
        if (root.left != null) {
            leftResult = preorderTraversal01(root.left);
        }
        if (root.right != null) {
            rightResult = preorderTraversal01(root.right);
        }
        result.addAll(leftResult);
        result.addAll(rightResult);
        return result;
    }

    public static List<Integer> preorderTraversal02(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        preorder(root, res);
        return res;
    }

    public static void preorder(TreeNode root, List<Integer> res) {
        if (root == null) {
            return;
        }
        res.add(root.val);
        preorder(root.left, res);
        preorder(root.right, res);
    }


}
