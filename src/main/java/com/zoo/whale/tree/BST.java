package com.zoo.whale.tree;


import java.util.ArrayList;
import java.util.List;

/**
 * @Date: 2026年 02月 21日
 * @Time:12时:53分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class BST<T extends Comparable<T>> {

    public class Node<T> {
        T value;

        Node left;

        Node right;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> root;


    // 插入操作
    public void insert(T value) {
        root = insert(root, value);
    }

    private Node<T> insertIterative(Node<T> root, T value) {
        Node<T> res = root;
        while (true) {
            if (root.value.compareTo(value) >= 0) {
                root.left = root;
                if (root == null) {
                    root = new Node(value);
                    break;
                }
            } else {
                root.right = root;
                if (root == null) {
                    root = new Node(value);
                    break;
                }
            }
        }
        return res;
    }

    private Node<T> insert(Node<T> root, T value) {
        if (root == null) {
            return new Node<>(value);
        }
        if (value.compareTo(root.value) <= 0) {
            //相等插入到左侧子节点
            root.left = insert(root.left, value);
        } else {
            root.right = insert(root.right, value);
        }
        return root;
    }

    // 查找操作
    public boolean search(T value) {
        return search(root, value) != null;
    }

    private Node<T> search(Node<T> node, T value) {
        if (node == null || node.value == null) {
            return null;
        }
        Node<T> res;
        if (value.compareTo(node.value) < 0) {
            res = search(node.left, value);
        } else if (value.compareTo(node.value) > 0) {
            res = search(node.right, value);
        } else {
            res = node;
        }
        return res;
    }


    // 删除操作
    public void delete(T value) {
        root = delete(root, value);
    }

    /*private Node<T> delete(Node<T> node, T value) {
        if (node == null) {
            return null;
        }
        if (value.compareTo(node.value) < 0) {
            node.left = delete(node.left, value);
        } else if (value.compareTo(node.value) > 0) {
            node.right =  delete(node.right, value);
        } else {
            //情况1	删除叶子节点（无子节点）	直接删除，父节点指向null
            //情况2	删除只有一个子节点的节点	用子节点替代被删除节点
            //情况3	删除有两个子节点的节点	找右子树的最小节点（或左子树的最大节点）替代
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            } else {
                //找右子树的最小节点
                Node<T> minNode = findMin(node.right);
                node.value = minNode.value;
                node.right = delete(node.right, minNode.value);
            }
        }
        return node;
    }*/

    private Node<T> delete(Node<T> node, T value) {
        if (node == null) {
            return null;
        }
        if (value.compareTo(node.value) < 0) {
            node.left = delete(node.left, value);
        } else if (value.compareTo(node.value) > 0) {
            node.right = delete(node.right, value);
        } else {
            //情况1	删除叶子节点（无子节点）	直接删除，父节点指向null
            //情况2	删除只有一个子节点的节点	用子节点替代被删除节点
            //情况3	删除有两个子节点的节点	找右子树的最小节点（或左子树的最大节点）替代
            if (node.left == null && node.right == null) {
                return null;  // 直接返回null，父节点指向null
            } else if (node.left == null) {
                return node.right;  // 用右子节点替代
            } else if (node.right == null) {
                return node.left;   // 用左子节点替代
            } else {
                // 找右子树的最小节点
                Node<T> minNode = findMin(node.right);
                // 用最小节点的值替换当前节点
                node.value = minNode.value;
                // 删除原来的最小节点
                node.right = delete(node.right, minNode.value);
            }
        }
        return node;
    }

    private Node findMin(Node<T> root) {
        if (root.left == null) {
            return root;
        }
        return findMin(root.left);
    }


    // 中序遍历（输出有序序列）
    public void inorder() {
        List<T> list = new ArrayList<>();
        inorder(root, list);
        System.out.println(list);
    }

    private void inorder(Node<T> root, List<T> list) {
        if (root == null) {
            return;
        }
        inorder(root.left, list);
        list.add(root.value);
        inorder(root.right, list);
    }

}
