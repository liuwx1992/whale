package com.zoo.whale.leetcode.datastructure;

/**
 * @Date: 2022年09月14日
 * @Time: 18时:15分
 * @Author: weixin.liu
 * @Version: 1.0
 *
 * 1523. 在区间范围内统计奇数数目
 * 给你两个非负整数 low 和 high 。请你返回 low 和 high 之间（包括二者）奇数的数目。
 * 如果我们暴力枚举 {\rm [low, high]}[low,high] 中的所有元素会超出时间限制。
 *
 * 我们可以使用前缀和思想来解决这个问题，定义 {\rm pre}(x)pre(x) 为区间 [0, x][0,x] 中奇数的个数，很显然：
 *
 * {\rm pre}(x) = \lfloor \frac{x + 1}{2} \rfloor
 * pre(x)=⌊
 * 2
 * x+1
 * ​
 *  ⌋
 *
 * 作者：LeetCode-Solution
 * 链接：https://leetcode.cn/problems/count-odd-numbers-in-an-interval-range/solution/zai-qu-jian-fan-wei-nei-tong-ji-qi-shu-shu-mu-by-l/
 * 来源：力扣（LeetCode）
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 */
public class CountOdds {

    public static void main(String[] args) {
        int i = countOdds(10, 20);
        System.out.println(i);
    }

    public static int countOdds(int low, int high) {
        return (high+1)/2 - (low)/2;
    }
}
