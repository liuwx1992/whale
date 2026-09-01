package com.zoo.whale.leetcode;

import java.util.HashMap;
import java.util.Map;

public class MajorityElement {

    public static void main(String[] args) {

    }


    public int majorityElement(int[] nums) {
        Map<Integer,Integer> countMap = new HashMap<>();
        int majorNum = nums.length/2 + 1;
        for(int num : nums){
            Integer value = countMap.get(num);
            if(value == null){
                countMap.put(num, 1);
            } else {
                countMap.put(num, value + 1);
            }
            Integer newValue = countMap.get(num);
            if (newValue >= majorNum) {
                return num;
            }
        }
        return 0;
    }

}
