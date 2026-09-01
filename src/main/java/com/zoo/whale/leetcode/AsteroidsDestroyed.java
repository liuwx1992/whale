package com.zoo.whale.leetcode;

import java.util.Arrays;

/**
 * @ClassName AsteroidsDestroyed
 * @Description TODO
 * @Author weixin.liu
 * @Date 2026/5/31 13:38
 */
public class AsteroidsDestroyed {

    public static void main(String[] args) {
        System.out.println("mass=10, asteroids=" + Arrays.toString(new int[]{77244,19898,13062,79891,33924,90485,2244})
                + ", result=" + asteroidsDestroyed(14359, new int[]{77244,19898,13062,79891,33924,90485,2244}));
 /*       System.out.println("mass=5, asteroids=" + Arrays.toString(new int[]{4, 3, 2, 1})
                + ", result=" + asteroidsDestroyed(5, new int[]{4, 3, 2, 1}));
        System.out.println("mass=1, asteroids=" + Arrays.toString(new int[]{10})
                + ", result=" + asteroidsDestroyed(1, new int[]{10}));*/
    }

    public static boolean asteroidsDestroyed(int mass, int[] asteroids) {
        Arrays.sort(asteroids);
        long  massNew = mass;
        for (int asteroid : asteroids) {
            if (massNew < asteroid) {
                return false;
            }
            massNew += asteroid;
        }
        return true;
    }

}
