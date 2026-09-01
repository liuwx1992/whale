package com.zoo.whale.leetcode.random;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName:CountMatchesSolution
 * @Description:
 * @Author:weixin.liu
 * @Date:2022/1/25
 * @Time:18:11
 * @Version；1.0 给你一个数组 items ，其中 items[i] = [typei, colori, namei] ，描述第 i 件物品的类型、颜色以及名称。
 * <p>
 * 另给你一条由两个字符串 ruleKey 和 ruleValue 表示的检索规则。
 * <p>
 * 如果第 i 件物品能满足下述条件之一，则认为该物品与给定的检索规则 匹配 ：
 * <p>
 * ruleKey == "type" 且 ruleValue == typei 。
 * ruleKey == "color" 且 ruleValue == colori 。
 * ruleKey == "name" 且 ruleValue == namei 。
 * 统计并返回 匹配检索规则的物品数量 。
 * <p>
 * <p>
 * <p>
 * 示例 1：
 * <p>
 * 输入：items = [["phone","blue","pixel"],["computer","silver","lenovo"],["phone","gold","iphone"]], ruleKey = "color", ruleValue = "silver"
 * 输出：1
 * 解释：只有一件物品匹配检索规则，这件物品是 ["computer","silver","lenovo"] 。
 * 示例 2：
 * <p>
 * 输入：items = [["phone","blue","pixel"],["computer","silver","phone"],["phone","gold","iphone"]], ruleKey = "type", ruleValue = "phone"
 * 输出：2
 * 解释：只有两件物品匹配检索规则，这两件物品分别是 ["phone","blue","pixel"] 和 ["phone","gold","iphone"] 。注意，["computer","silver","phone"] 未匹配检索规则。
 */
public class CountMatchesSolution {

    public static int countMatches(List<List<String>> items, String ruleKey, String ruleValue) {
        int index = -1;
        switch (ruleKey) {
            case "type":
                index = 0;
                break;
            case "color":
                index = 1;
                break;
            case "name":
                index = 2;
                break;
        }
        if (index == -1 || StringUtils.isEmpty(ruleValue)) {
            return 0;
        }
        int result = 0;
        for (List<String> item : items) {
            String realValue = item.get(index);
            if (ruleValue.equals(realValue)) {
                result++;
            }
        }
        return result;
    }


    public static void main(String[] args) {
        /*String item0 = "phone,blue,pixel";
        String item1 = "computer,silver,lenovo";
        String item2 = "phone,gold,iphone";*/
        List<String> list0 = new ArrayList<>();
        list0.add("phone");
        list0.add("blue");
        list0.add("pixel");
        List<String> list1 = new ArrayList<>();
        list1.add("computer");
        list1.add("silver");
        list1.add("lenovo");
        List<String> list2 = new ArrayList<>();
        list2.add("phone");
        list2.add("gold");
        list2.add("iphone");
        List<List<String>> items = new ArrayList<>();
        items.add(list0);
        items.add(list1);
        items.add(list2);
        String ruleKey = "color";
        String ruleValue = "silver";
        int ret = countMatches(items, ruleKey, ruleValue);
        System.out.println(ret);
    }
}
