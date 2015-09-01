package com.zxy.libs.file;

import java.util.List;


public class Test {

	public static void main(String[] args) {
//		FileUtils.readFileByLine();
//		FileUtils.readFileByByte();
		
		List<Class<?>> classes = ClassUtils.getClasses("com.zxy.libs.file");
		
		for(Class<?> c : classes)  {
			System.out.println(c.getName());
		}
	}

}
