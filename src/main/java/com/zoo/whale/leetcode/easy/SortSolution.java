package com.zoo.whale.leetcode.easy;

/**
 * @ClassName:SortrSolution
 * @Description: https://www.cnblogs.com/morethink/p/8419151.html#%E7%9B%B4%E6%8E%A5%E6%8F%92%E5%85%A5%E6%8E%92%E5%BA%8F
 * @Author:weixin.liu
 * @Date:2021/10/28
 * @Time:10:33
 * @Version；1.0
 */
public class SortSolution {

    public static void main(String[] args) {
        int[] a = new int[]{9, 8, 7, 6, 4, 2, 3, 1, 6, 7};
        sort2(a);
        for (int i : a) {
            System.out.print(i + ",");
        }
    }


    /**
     * 通过交换进行插入排序，借鉴冒泡排序
     *
     * @param a
     */
    public static void sort(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            for (int j = i + 1; j > 0; j--) {
                if (a[j] < a[j - 1]) {
                    swapPlace(a, j, j - 1);
                }
            }
        }
    }

    /**
     * 通过将较大的元素都向右移动而不总是交换两个元素
     *
     * @param a
     */
    public static void sort2(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int num = a[i];
            int j;
            for (j = i; j > 0 && a[j - 1] > num; j--) {
                a[j] = a[j - 1];
            }
            a[j] = num;
        }
    }


    /**
     * 交换位置
     */
    private static void swapPlace(int[] a, int j, int i) {
        int temp = a[j];
        a[j] = a[i];
        a[i] = temp;
    }
}
