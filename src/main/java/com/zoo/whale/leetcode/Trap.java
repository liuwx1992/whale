package com.zoo.whale.leetcode;

/**
 * @Date: 2026年 04月 04日
 * @Time:14时:17分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class Trap {

    public static void main(String[] args) {
        int[] nums = new int[]{0,1,0,2,1,0,1,3,2,1,2,1};
        int trap = trap(nums);
        System.out.println(trap);
    }

    public static int trap(int[] height) {
        int length = height.length;
        if (length == 0) {
            return 0;
        }
        int[] leftMaxArray = new int[height.length];
        int leftMax = 0;
        for (int i = 0;i < height.length;i++) {
            leftMax = Math.max(leftMax, height[i]);
            leftMaxArray[i] = leftMax;
        }

        int[] rightMaxArray = new int[height.length];
        int rightMax = 0;
        for (int i = height.length - 1;i >= 0;i--) {
            rightMax = Math.max(rightMax, height[i]);
            rightMaxArray[i] = rightMax;
        }
        int ans = 0;
        for (int i = 0;i < height.length;i++) {
            int leftMaxValue = leftMaxArray[i];
            int rightMaxValue = rightMaxArray[i];
            if(leftMaxValue > rightMaxValue){
                ans += rightMaxValue - height[i];
            } else {
                ans += leftMaxValue - height[i];
            }
        }
        return ans;
    }



    /*public static int trap(int[] height) {
        int length = height.length;
        if (length == 0) {
            return 0;
        }
        int ans = 0;
        int maxLeft = 0;
        int maxRight = 0;
        int left = 0, right = height.length - 1;
        while (left < right) {
            maxLeft = Math.max(maxLeft, height[left]);
            maxRight = Math.max(maxRight, height[right]);
            if (maxLeft > maxRight) {
                ans += maxRight - height[right];
                right--;
            } else {
                ans += maxLeft - height[left];
                left++;
            }
        }
        return ans;
    }*/

    /*public int trap(int[] height) {
        for (int i = 1;i < height.length - 1;i++) {
            if(height[i] > height[i + 1] || height[i] > height[i-1]){
                continue;
            }
            int next = height[i + 1] - height[i];
            int pre = height[i] - height[i-1];
            int heightNum = Math.min(pre,next);
            if(){

            }
        }
    }*/



}
