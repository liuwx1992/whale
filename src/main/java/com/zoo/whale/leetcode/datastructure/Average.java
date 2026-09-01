package com.zoo.whale.leetcode.datastructure;

import java.math.BigDecimal;

/**
 * @Date: 2022年09月21日
 * @Time: 19时:36分
 * @Author: weixin.liu
 * @Version: 1.0
 *
 * 提示：
 *
 * 3 <= salary.length <= 100
 * 10^3 <= salary[i] <= 10^6
 * salary[i] 是唯一的。
 * 与真实值误差在 10^-5 以内的结果都将视为正确答案。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode.cn/problems/average-salary-excluding-the-minimum-and-maximum-salary
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 */
public class Average {

    public static void main(String[] args) {
        //int[] average = new int[]{4000,3000,1000,2000};
        int[] average = new int[]{48000,59000,99000,13000,78000,45000,31000,17000,39000,37000,93000,77000,33000,28000,4000,54000,67000,6000,1000,11000};
        double average1 = average(average);
        System.out.println(average1);
    }

    public static double average(int[] salary) {
        int length = salary.length;
        int max = salary[0];
        int min = salary[0];
        int sum = 0;
        for (int s : salary){
            if(s > max){
                max = s;
            }
            if(s < min){
                min = s;
            }
            sum += s;
        }
        BigDecimal sumDecimal = new BigDecimal(sum - max -min);
        BigDecimal lengthDecimal = new BigDecimal(length - 2);
        double average = Double.parseDouble(sumDecimal.divide(lengthDecimal,5,BigDecimal.ROUND_HALF_UP).toString());
        return average;
    }
}
