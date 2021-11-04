package com.ereryao.tool.heventbus.utils;

import java.util.Collection;
import java.util.HashSet;

public class ReflectUtil {

	/**
	 * 返回所有类(包含类的父级和接口，含自己)
	 * @param clazz
	 * @return
	 */
	public static Collection<? extends Class<?>> findAllClass(Class<?> clazz){
		Collection<Class<?>> clazzSet = new HashSet<Class<?>>();
		return findAllClass(clazzSet, clazz);
	}
	
	/**
	 * 返回所有类(包含类的父级和接口，含自己)
	 * @param collection
	 * @param clazz
	 * @return
	 */
	public static Collection<? extends Class<?>> findAllClass(Collection<Class<?>> collection, Class<?> clazz){
		// 找父类
		Class<?> superclass = clazz.getSuperclass();
		if(superclass != null && superclass != Object.class) {
			collection.add(superclass);
			// 找父类的父类
			findAllClass(collection, superclass);			
		}
		// 找接口
		Class<?>[] interfaces = clazz.getInterfaces();
		for(Class<?> in : interfaces) {
			collection.add(in);
			// 找接口的接口
			findAllClass(collection, in);
		}
		collection.add(clazz);
		return collection;
	}
	
}
