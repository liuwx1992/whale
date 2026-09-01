package com.zoo.whale.leetcode.bst;

import java.util.ArrayList;
import java.util.List;

public class ValidSolutionOrder {

    public static void main(String[] args) {
        test01();
        test02();
        test03();
        test04();
        test05();
    }

    private static void test01() {
        TreeNode treeNode = new TreeNode(2);
        TreeNode treeNode1 = new TreeNode(1);
        TreeNode treeNode2 = new TreeNode(3);

        treeNode.left = treeNode1;
        treeNode.right = treeNode2;

        boolean validBST2 = isValidBST(treeNode);
        System.out.println(validBST2);
    }

    private static void test02() {
        TreeNode treeNode = new TreeNode(0);
        TreeNode treeNode1 = new TreeNode(-1);
        treeNode.left = treeNode1;
        boolean validBST2 = isValidBST(treeNode);
        System.out.println(validBST2);
    }

    private static void test03() {
        //[5,4,6,null,null,3,7]
        TreeNode treeNode = new TreeNode(5);
        TreeNode treeNode1 = new TreeNode(4);
        TreeNode treeNode2 = new TreeNode(6);
        TreeNode treeNode5 = new TreeNode(3);
        TreeNode treeNode6 = new TreeNode(7);
        TreeNode treeNode7 = new TreeNode(2);
        TreeNode treeNode8 = new TreeNode(8);

        treeNode.left = treeNode1;
        treeNode.right = treeNode2;

        treeNode2.left = treeNode5;
        treeNode2.right = treeNode6;

        treeNode1.left = treeNode7;
        treeNode1.right = treeNode8;

        boolean validBST2 = isValidBST(treeNode);
        System.out.println(validBST2);
    }

    private static void test04() {
        TreeNode treeNode = new TreeNode(2);
        TreeNode treeNode1 = new TreeNode(1);
        TreeNode treeNode2 = new TreeNode(3);

        treeNode.left = treeNode1;
        treeNode.right = treeNode2;

        boolean validBST2 = isValidBST(treeNode);
        System.out.println(validBST2);
    }

    private static void test05() {
        TreeNode treeNode = new TreeNode(2147483647);

        boolean validBST2 = isValidBST(treeNode);
        System.out.println(validBST2);
    }





    public static boolean isValidBST(TreeNode root) {
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
