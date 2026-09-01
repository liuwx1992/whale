package com.zoo.whale.atguigu;

public class Exam5 {
	static int s;//��Ա�����������
	int i;//��Ա������ʵ������
	int j;//��Ա������ʵ������
	{
		int i = 1;//�Ǿ�̬������еľֲ����� i
		i++;
		j++;
		s++;
	}
	public void test(int j){//�βΣ��ֲ�����,j
		j++;
		i++;
		s++;
	}
	public static void main(String[] args) {//�βΣ��ֲ�������args
		Exam5 obj1 = new Exam5();//�ֲ�������obj1
		Exam5 obj2 = new Exam5();//�ֲ�������obj1
		obj1.test(10);
		obj1.test(20);
		obj2.test(30);
		System.out.println(obj1.i + "," + obj1.j + "," + obj1.s);
		System.out.println(obj2.i + "," + obj2.j + "," + obj2.s);
	}
}

