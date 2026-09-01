package com.zoo.whale.leetcode.bst;

/**
 * @className IsValidBST
 * @description TODO
 * @Author weixin.liu
 * @Date 2026/5/3 11:35
 */
public class IsValidBST {

    public static void main(String[] args) {
        test01();
        test02();
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


    private static boolean isValidBST(TreeNode root) {
        if (root == null) {
            return false;
        }
        boolean valid = isValid(root);
        if (!valid) {
            return false;
        }
        TreeNode tempNode = root;
        while (tempNode.left != null) {
            boolean validLeft = isValid(tempNode.left);
            if (!validLeft) {
                return false;
            }
            tempNode = tempNode.left;
        }
        TreeNode tempNodeR = root;
        while (tempNodeR.right != null) {
            boolean validRight = isValid(tempNodeR.right);
            if (!validRight) {
                return false;
            }
            tempNodeR = tempNodeR.right;
        }
        return true;
    }

    private static boolean isValid(TreeNode root) {
        if (root == null) {
            return true;
        }
        if (root.left == null && root.right == null) {
            return true;
        }
        boolean left = true;
        boolean right = true;

        if (root.left != null && root.left.val >= root.val) {
            left = false;
        }
        if (root.right != null && root.right.val <= root.val) {
            right = false;
        }
        return left && right;
    }
}
