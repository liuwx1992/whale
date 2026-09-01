package com.zoo.whale.leetcode.random;

/**
 * @ClassName:MinPartitionsSolution
 * @Description: 如果一个十进制数字不含任何前导零，且每一位上的数字不是 0 就是 1 ，那么该数字就是一个 十-二进制数 。例如，101 和 1100 都是 十-二进制数，而 112 和 3001 不是。
 * <p>
 * 给你一个表示十进制整数的字符串 n ，返回和为 n 的 十-二进制数 的最少数目。
 * <p>
 *  
 * <p>
 * 示例 1：
 * <p>
 * 输入：n = "32"
 * 输出：3
 * 解释：10 + 11 + 11 = 32
 * 示例 2：
 * <p>
 * 输入：n = "82734"
 * 输出：8
 * 示例 3：
 * <p>
 * 输入：n = "27346209830709182346"
 * 输出：9
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/partitioning-into-minimum-number-of-deci-binary-numbers
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 * @Author:weixin.liu
 * @Date:2022/1/25
 * @Time:20:41
 * @Version；1.0
 */
public class MinPartitionsSolution {
    public static int minPartitions(String n) {
        char[] chars = n.toCharArray();
        int ans = 0;
        for (char c : chars) {
            int num = Integer.parseInt(c + "");
            if (num > ans) {
                ans = num;
            }
        }
        return ans;
    }
}
