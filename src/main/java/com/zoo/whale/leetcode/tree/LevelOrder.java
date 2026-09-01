package com.zoo.whale.leetcode.tree;

import com.zoo.whale.leetcode.base.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @Date: 2022年07月21日
 * @Time: 19时:54分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class LevelOrder {

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        TreeNode left = new TreeNode(2);
        TreeNode right = new TreeNode(3);
        TreeNode left2 = new TreeNode(4);
        TreeNode right2 = new TreeNode(5);
        root.left = left;
        root.right = right;

        right.left = left2;
        right.right = right2;
        List<List<Integer>> integers = levelOrder2(root);
        for (List<Integer> list: integers){
            System.out.println("");
            for (int i : list){
                System.out.print(i+",");
            }
        }
    }

    public static List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if(root == null){
            return result;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        //根节点入队
        queue.add(root);
        while (!queue.isEmpty()){
            //BFS打印，levelNum表示的是每层的结点数
            int leaveNum = queue.size();
            //subList存储的是每层的结点值
            List<Integer> subValueList = new ArrayList<>();
            for (int i = 0;i < leaveNum;i++){
                //出队,并保存到集合
                TreeNode current  = queue.poll();
                subValueList.add(current .val);

                //左右子节点如果不为空就加入到队列中
                if (current .left != null){
                    queue.add(current .left);
                }
                if (current .right != null){
                    queue.add(current .right);
                }
            }
            //把每层的结点值存储在res中
            result.add(subValueList);
        }
        return result;
    }

    public static List<List<Integer>> levelOrder2(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if(root == null){
            return result;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()){
            int subLevelSize = queue.size();
            List<Integer> subList = new ArrayList<>();
            for (int i = 0;i < subLevelSize;i++){
                TreeNode currentNode = queue.poll();
                subList.add(currentNode.val);
                if(currentNode.left != null){
                    queue.add(currentNode.left);
                }
                if(currentNode.right != null){
                    queue.add(currentNode.right);
                }
            }
            result.add(subList);
        }
        return result;
    }

}
