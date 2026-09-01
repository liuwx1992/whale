package com.zoo.whale.leetcode.bst;

public class ValidSolution {

    public static void main(String[] args) {
        test03();
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
        return isValidBST(root, Long.valueOf(Integer.MIN_VALUE) - 1, Long.valueOf(Integer.MAX_VALUE) + 1);
    }

    private static boolean isValidBST(TreeNode root, long minValue, long maxValue) {
        if (root == null) {
            return true;
        }

        if (root.val <= minValue || root.val >= maxValue) {
            //等于也被认为false
            return false;
        }

        //
        return isValidBST(root.left, minValue, root.val) && isValidBST(root.right, root.val, maxValue);
    }



}
