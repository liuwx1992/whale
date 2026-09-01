package com.zoo.whale.leetcode.tree;

import com.zoo.whale.leetcode.base.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2022年07月21日
 * @Time: 15时:39分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class InorderTraversal {

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        TreeNode left = new TreeNode(3);
        TreeNode right = new TreeNode(2);
        root.right = left;
        root.right.left = right;
        List<Integer> integers = inorderTraversal(root);
        for (int i : integers){
            System.out.print(i+",");
        }
    }

    public static List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        inorder(root,res);
        return res;
    }

    public static void inorder(TreeNode root, List<Integer> res) {
        if(root == null){
           return;
        }
        inorder(root.left,res);
        res.add(root.val);
        inorder(root.right,res);
    }


}
