package com.ereryao.tool.heventbus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class Invoker implements Comparable<Invoker>{

	/**
	 * listener对象、handler对象
	 */
	private Object object;
	/**
	 * 方法
	 */
	private Method method;
	private long timeout = 0;
	private int order = 0;
	private Date createTime = new Date();
	
	private Invoker(Object object, Method method) {
		this.object = object;
		this.method = method;
		// 获取超时时间
		EventHandler annotation = this.method.getAnnotation(EventHandler.class);
		this.timeout = annotation.maxExecuteTime();
		this.order = annotation.order();
	}
	
	public static Invoker create(Object object, Method method) {
		return new Invoker(object,method); 
	}

	public static Invoker create(Class<?> clazz, Method method) {
		Object object = null;
		try {
			// Java9 抛弃了clazz.newInstance()写法;
			object = clazz.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Invoker(object,method); 
	}
	
	public Date getCreateTime() {
		return this.createTime;
	}

	public Object getObject() {
		return this.object;
	}
	
	public int getOrder() {
		return this.order;
	}
	
	public void dispatchEvent(Event event) throws TimeoutException {
		try {
//			Method m = this.method;
//			Object o = this.object;
//			Callable<Boolean> callable = new Callable<Boolean>() {
//                @Override
//                public Boolean call() throws Exception {
//                	m.invoke(o, event);
//                    return true;
//                }
//            };
//            
//            FutureTask<Boolean> future = new FutureTask<>(callable);
//            future.get(this.timeout, TimeUnit.MILLISECONDS);
//            future.run();
			this.method.invoke(this.object, event);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 /**
     * 用于对多个Invoker 对象去重
     * 这里指定object.class 为key重写equals方法,以在后续Set<Invoker> 中自动去重
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Invoker) {
        	Invoker result = (Invoker) obj;
            return this.getObject().getClass().equals(result.getObject().getClass());
        } else return false;
    }
 
    /**
     * 用于对多个Invoker 对象去重
     * 这里hashCode的生成规则是：按去重的key生成hashCode
     * @return
     */
    @Override
    public int hashCode() {
        return (this.getObject().getClass()).hashCode();
    }
 
    /**
     * 用于对两个Invoker 对象排序
     * o1.getCreateTime()在前面表示按时间降序，反之相反
     * @param o o
     * @return int
     */
    @Override
    public int compareTo(Invoker invoker) {
    	return this.getOrder() - invoker.getOrder();
    }
	
}
