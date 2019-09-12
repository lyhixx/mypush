package com.lyh.common.test;

public class Load2 {

	public static int age = 2; 
	public final static int sex = 0;
	
	static{
		System.out.println(age);
		System.out.println(sex);
	}
	
	public void loadin(){
		System.out.println("im loaded");
	}
}
