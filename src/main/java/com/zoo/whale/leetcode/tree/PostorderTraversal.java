package com.zoo.whale.leetcode.tree;

import com.zoo.whale.leetcode.base.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2022年07月21日
 * @Time: 19时:44分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class PostorderTraversal {
    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        TreeNode left = new TreeNode(3);
        TreeNode right = new TreeNode(2);
        root.right = left;
        root.right.left = right;
        List<Integer> integers = postorderTraversal(root);
        for (int i : integers){
            System.out.print(i+",");
        }
    }

    public static List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        postorder(root,res);
        return res;
    }

    private static void postorder(TreeNode root, List<Integer> res) {
        if(root == null){
            return;
        }
        postorder(root.left,res);
        postorder(root.right,res);
        res.add(root.val);
    }

}
