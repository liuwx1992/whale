package com.zoo.whale.leetcode.bst;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2026年 02月 26日
 * @Time:20时:30分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class NumTrees {

    public static void main(String[] args) {
        System.out.println(numTrees(3));
    }

    public static int numTrees(int n) {
        int[] array = new int[n + 1];
        array[0] = 1;
        array[1] = 1;
        for (int end = 2; end <= n; end++) {
            for (int i = 1; i <= end; i++) {
                int nums = array[i - 1] * array[end - i];
                array[end] += nums;
            }
        }
        return array[n];
    }


    public static int numTrees2(int n) {
        if (n == 1) {
            return 1;
        }
        List<TreeNode> treeNodes = numTreesAll(1, n);
        return treeNodes.size();
    }

    private static List<TreeNode> numTreesAll(int start, int end) {
        List<TreeNode> treeNodes = new ArrayList<>();
        if (start > end) {
            treeNodes.add(null);
            return treeNodes;
        }
        for (int i = start; i <= end; i++) {
            //左子树节点全部小于i
            List<TreeNode> left = numTreesAll(start, i - 1);
            //右子树节点全部大于i
            List<TreeNode> right = numTreesAll(i + 1, end);

            for (TreeNode leftTree : left) {
                for (TreeNode rightTree : right) {
                    TreeNode treeNode = new TreeNode(i);
                    treeNode.left = leftTree;
                    treeNode.right = rightTree;
                    treeNodes.add(treeNode);
                }
            }
        }
        return treeNodes;
    }

}
