package com.ereryao.tool.heventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {

	/**
     * 方法最长执行时间,单位 毫秒
     * 异步有用，暂未实现
     */
	@Deprecated
    public long maxExecuteTime() default 0;
	/**
	 * 排序
	 */
	public int order() default 0;
	/**
	 * 是否异步
	 * 暂未实现
	 */
	@Deprecated
	public boolean async() default false;
}
