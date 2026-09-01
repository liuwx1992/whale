package com.zoo.whale.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName YHSJGenerate
 * @Description 杨辉三角
 * 给定一个非负整数 numRows，生成「杨辉三角」的前 numRows 行。
 * <p>
 * 在「杨辉三角」中，每个数是它左上方和右上方的数的和。
 * @Author weixin.liu
 * @Date 2026/5/3 16:30
 */
public class YHSJGenerate {

    public static void main(String[] args) {
        List<List<Integer>> expected5 = new ArrayList<>();
        expected5.add(Arrays.asList(1));
        expected5.add(Arrays.asList(1, 1));
        expected5.add(Arrays.asList(1, 2, 1));
        expected5.add(Arrays.asList(1, 3, 3, 1));
        expected5.add(Arrays.asList(1, 4, 6, 4, 1));

        List<List<Integer>> got5 = generate(5);
        System.out.println("numRows=5 -> " + got5);
        System.out.println("与标准前 5 行一致: " + expected5.equals(got5));

        System.out.println("numRows=0 -> " + generate(0) + " (期望 [])");
        System.out.println("numRows=1 -> " + generate(1) + " (期望 [[1]])");
        System.out.println("numRows=2 -> " + generate(2) + " (期望 [[1], [1, 1]])");

        List<List<Integer>> g = generate(5);
        List<List<Integer>> gn = generateNew(5);
        System.out.println("generate 与 generateNew(5) 结果一致: " + g.equals(gn));
    }

    /**
     * 杨辉三角：用上一行相邻两项相加生成当前行。
     * <p>
     * 特点：思路直观；需注意上一行取下标用 {@code i - 2}，与「行号从 1 开始」耦合，重构时容易踩坑。
     * 时间与空间均为 O(numRows²)，由输出规模决定，无法再渐近改进。
     */
    public static List<List<Integer>> generate(int numRows) {
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 1; i <= numRows; i++) {
            List<Integer> rows = new ArrayList<>();
            if (i == 1) {
                rows.add(1);
                result.add(rows);
                continue;
            } else if(i == 2){
                rows.add(1);
                rows.add(1);
                result.add(rows);
                continue;
            }
            List<Integer> tempList = result.get(i-2);
            int size = tempList.size();
            for (int j = 0; j <= size; j++) {
                if (j == 0) {
                    rows.add(tempList.get(j));
                    continue;
                } else if (j == size) {
                    rows.add(tempList.get(size - 1));
                    continue;
                }
                rows.add(tempList.get(j - 1) + tempList.get(j));
            }
            result.add(rows);
        }
        return result;
    }

    /**
     * 同一题的另一种实现：第 i 行（0-based）第 k 个数为组合数 C(i,k)，用
     * C(i,k) = C(i,k-1) × (i - k + 1) / k 递推，只需当前行内运算，不访问 {@code result} 里上一行。
     * <p>
     * 中间量用 {@code long}，减轻乘法环节的溢出风险；返回值仍为 {@code int}，极大 {@code numRows} 时仍可能与 {@link #generate} 一同溢出。
     */
    public static List<List<Integer>> generateNew(int numRows) {
        List<List<Integer>> result = new ArrayList<>(numRows);
        for (int i = 0; i < numRows; i++) {
            List<Integer> row = new ArrayList<>(i + 1);
            long val = 1;
            row.add(1);
            for (int k = 1; k <= i; k++) {
                val = val * (i - k + 1) / k;
                row.add((int) val);
            }
            result.add(row);
        }
        return result;
    }

}
