package com.zoo.whale.leetcode.easy;

/**
 * @ClassName:CalculateDemo
 * @Description:
 * @Author:weixin.liu
 * @Date:2021/8/11
 * @Time:11:10
 * @Version；1.0
 */
public class CalculateDemo {

    public static void main(String[] args) {
        //
        /*String calculate = calculate(900);
        System.out.println(calculate);*/

        String s = "98\n" +
                "100\n" +
                "101\n" +
                "102\n" +
                "103\n" +
                "200\n" +
                "201\n" +
                "202\n" +
                "203\n" +
                "204\n" +
                "205\n" +
                "206\n" +
                "207\n" +
                "208\n" +
                "226\n" +
                "300\n" +
                "301\n" +
                "302\n" +
                "303\n" +
                "307\n" +
                "308\n" +
                "400\n" +
                "401\n" +
                "402\n" +
                "403\n" +
                "404\n" +
                "405\n" +
                "406\n" +
                "407\n" +
                "408\n" +
                "409\n" +
                "410\n" +
                "411\n" +
                "412\n" +
                "415\n" +
                "416\n" +
                "417\n" +
                "422\n" +
                "423\n" +
                "424\n" +
                "425\n" +
                "426\n" +
                "428\n" +
                "429\n" +
                "431\n" +
                "451\n" +
                "500\n" +
                "501\n" +
                "502\n" +
                "503\n" +
                "504\n" +
                "505\n" +
                "506\n" +
                "507\n" +
                "508\n" +
                "509\n" +
                "510\n" +
                "511\n" +
                "512\n" +
                "513\n" +
                "514\n" +
                "515\n" +
                "516\n" +
                "517\n" +
                "518\n" +
                "-100\n" +
                "-200\n" +
                "-400\n" +
                "-450\n" +
                "-500\n" +
                "-300\n";
        String ss = s.replace("\n", ",");
        System.out.println(ss);
        String a = "98,100,101,102,103,200,201,202,203,204,205,206,207,208,226,300,301,302,303,304,305,307,308,400,401,402,403,404,405,406,407,408,409,410,411,412,413,414,415,416,417,418,419,420,421,422,423,424,425,426,428,429,431,451,500,501,502,503,504,505,506,507,508,509,510,511,512,513,514,515,516,517,518,-100,-200,-400,-450,-500,-300";
        String[] split = a.split(",");


        String b = "98,100,101,102,103,200,201,202,203,204,205,206,207,208,226,300,301,302,303,307,308,400,401,402,403,404,405,406,407,408,409,410,411,412,415,416,417,422,423,424,425,426,428,429,431,451,500,501,502,503,504,505,506,507,508,509,510,511,512,513,514,515,516,517,518,-100,-200,-400,-450,-500,-300";
        String[] split2 = b.split(",");

        for (String aa : split2) {
            if (!a.contains(aa)) {
                System.out.println("覆盖补全");
            }
        }

        System.out.println(split.length);
        System.out.println(split2.length);
    }

    /**
     * score
     */
    public static String calculate(int score) {
        if (score == 1000) {
            return "score参数不合法";
        }
        String pcp_income;
        Long tmp = Math.round(Math.log(700 / (1000 - score)) * 10000);
        if (tmp < 5 || tmp == null) {
            pcp_income = "[0, 5]";
        } else {
            long tmpLow = tmp - 5;
            long tmpHign = tmp + 5;
            pcp_income = "[" + tmpLow + "," + tmpHign + "]";
        }
        return pcp_income;
    }

}
