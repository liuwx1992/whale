package com.zoo.whale.tree;

/**
 * @Date: 2026年 02月 15日
 * @Time:09时:09分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class BuildBinaryTree {

    public static TreeNode buildTree() {
        // 创建所有节点
        TreeNode[] nodes = new TreeNode[11];
        for (int i = 0; i < 11; i++) {
            nodes[i] = new TreeNode(i);
        }

        // 按完全二叉树关系连接
        for (int i = 0; i < 11; i++) {
            int leftIndex = 2 * i + 1;
            int rightIndex = 2 * i + 2;

            if (leftIndex < 11) {
                nodes[i].left = nodes[leftIndex];
            }
            if (rightIndex < 11) {
                nodes[i].right = nodes[rightIndex];
            }
        }

        return nodes[0]; // 返回根节点（值为 0）
    }

    // 测试：打印层序遍历结果，验证是否正确
    public static void main(String[] args) {
        TreeNode root = buildTree();
        System.out.println("层序遍历结果（应为 0~10）:");
        levelOrderPrint(root);
    }

    // 辅助方法：层序遍历打印
    public static void levelOrderPrint(TreeNode root) {
        if (root == null) return;
        java.util.Queue<TreeNode> queue = new java.util.LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            System.out.print(node.value + " ");
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        System.out.println();
    }

}
