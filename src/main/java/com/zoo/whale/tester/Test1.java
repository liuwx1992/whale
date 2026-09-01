package com.zoo.whale.tester;

import org.apache.commons.collections.MapUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * @version 1.0
 * @Class Test1
 * @Author weixin.liu
 * @Date 2026/7/6 11:33
 */
public class Test1 {

    public static void main(String[] args) {
        HashSet<String> bidToCidSet = new HashSet<>();
        //然后获取cid再通过 cidToInterestLevelMapping 去获取所有的利率等级InterestLevel，同样需要小于等于userDate
        for (String singleCid : bidToCidSet) {
            System.out.println(singleCid);
        }
    }

}
