package com.zoo.whale.atguigu;


public class Test {

	public static void main(String[] args) {
		//堆内存初始化i=1
		int i = 1;
		i = i++;
		int j = i++;
		int k = i + ++i * i++;
		System.out.println("i=" + i);
		System.out.println("j=" + j);
		System.out.println("k=" + k);
	}
}
