package com.lyh.common.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class demo {
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		
		// TODO Auto-generated method stub
		// 创建自定义classloader对象。
		DiskClassLoader diskLoader = new DiskClassLoader("/Users/liyanhui/Desktop");
		try {
			// 加载class文件
//			Class c = diskLoader.loadClass("com.lyh.common.test.Load1");
			Class c1 = diskLoader.loadClass("java.lang.String");
//			System.out.println(c1.getClassLoader().toString());
			Object obj = c1.newInstance();
			Method method = c1.getDeclaredMethod("isEmpty", null);
			System.out.println(method.invoke(obj, null));
//			if (c != null) {
//				try {
//					Object obj = c.newInstance();
//					Method method = c.getDeclaredMethod("loadin", null);
//					// 通过反射调用Test类的say方法
//					method.invoke(obj, null);
//				} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException
//						| IllegalArgumentException | InvocationTargetException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
