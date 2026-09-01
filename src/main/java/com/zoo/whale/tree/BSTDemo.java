package com.zoo.whale.tree;

/**
 * @Date: 2026年 02月 22日
 * @Time:17时:19分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class BSTDemo {

    public static void main(String[] args) {
        BST<Integer> bst = new BST<>();
        bst.insert(4);
        bst.insert(3);
        bst.insert(1);
        bst.insert(6);
        bst.insert(5);
        bst.insert(7);
        bst.insert(9);
        bst.insert(11);
        bst.insert(8);

        bst.inorder();

        boolean search = bst.search(9);

        System.out.println(search);

        bst.delete(9);

        boolean search2 = bst.search(9);

        System.out.println(search2);

        bst.inorder();
    }


}
