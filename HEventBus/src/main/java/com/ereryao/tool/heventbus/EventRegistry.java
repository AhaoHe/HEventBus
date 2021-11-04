package com.ereryao.tool.heventbus;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import com.ereryao.tool.heventbus.utils.ReflectUtil;

public class EventRegistry {

	private final EventBus eventbus;
	// 线程安全的Map和线程安全的Set
	// key是事件class类型，value是Invoker的set（包含监听的对象和方法）
	private final ConcurrentMap<Class<?>, CopyOnWriteArraySet<Invoker>> listeners;
	
	private EventRegistry() {
		this.eventbus = null;
		this.listeners = null;
	}
	
	protected EventRegistry(EventBus eventbus) {
		this.eventbus = eventbus;
		listeners = new ConcurrentHashMap<Class<?>, CopyOnWriteArraySet<Invoker>>();

	}
	
	protected void register(Object eventHandler) {
		register(eventHandler.getClass());
	}
	
	/**
	 * 注册订阅者
	 * @param eventHandler
	 */
	protected void register(Class<?> eventHandler) {
		// 根据缓存获取方法 (key是class，value是Invoker类)
		for(Entry<Class<?>, Collection<Invoker>> entry : findAllInvokers(eventHandler).entrySet()) {
			// 事件class类型 和 这个事件的监听/订阅者集合
			Class<?> eventType = entry.getKey();
			Collection<Invoker> eventInvokers = entry.getValue();
			// 从缓存中按事件类型查找订阅者集合
			CopyOnWriteArraySet<Invoker> invokers = listeners.get(eventType);
			// 如果Map找不到对应的值，就新建
			if(invokers == null) {
				CopyOnWriteArraySet<Invoker> newSet = new CopyOnWriteArraySet<>();
				// 放入listener中
				CopyOnWriteArraySet<Invoker> listenerSet = listeners.putIfAbsent(eventType, newSet);
				// 如果putIfAbsent返回空，就使用newSet
				invokers = listenerSet == null ? newSet : listenerSet;
			}
			// 加入缓存中的订阅者集合
			invokers.addAll(eventInvokers);
		}
	}
	
	/**
	 * 获取监听中的订阅者
	 * @param event
	 * @return
	 */
	protected Set<Invoker> getInvokers(Event event){
		Set<Invoker> invokers = listeners.get(event.getClass());
		//listeners.remove(event.getClass());
		return invokers;
	}

	/**
	 * 获取所有符合的方法封装的对象
	 * @param clazz 订阅者的类
	 * @return
	 */
	private Map<Class<?>, Collection<Invoker>> findAllInvokers(Class<?> clazz) {
		// 事件和订阅者的Map。key是事件类，value是订阅者集合
		Map<Class<?>, Collection<Invoker>> eventInvokersMap = new HashMap<Class<?>, Collection<Invoker>>();
		for(Method method : getAnnotatedMethods(clazz)) {
			Class<?>[] parameterTypes = method.getParameterTypes();
			Class<?> eventType = parameterTypes[0];
			// 加入Map
			Collection<Invoker> collection = eventInvokersMap.get(eventType);
			if(collection == null) {
				// 这里因为不可能存在重复，就不用putIfAbsent了
				collection = new LinkedList<Invoker>();
				eventInvokersMap.put(eventType, collection);
			}
			// 加入evnetType对应的collection
			collection.add(Invoker.create(clazz, method));
		}
		return eventInvokersMap;
	}
	
	// 利用反射获得某个类所有的方法，包括父类、接口等.
    // 由于反射性能问题，这里加了缓存
    private Collection<Method> getAnnotatedMethods(Class<?> clazz) {
		//优先查找缓存
		Collection<Method> methods = EventHandlerMethodCache.get(clazz);
		if(methods == null) {
			// 查找非缓存的
			methods = getAnnotatedMethodsNotCached(clazz);
			// 放入缓存
			EventHandlerMethodCache.put(clazz, methods);
		}
		return methods;
	}
    
    /**
     * 获取非缓存的监听对象的有注解的方法
     * @param clazz
     * @return
     */
    private Collection<Method> getAnnotatedMethodsNotCached(Class<?> clazz) {
    	// 所有符合的方法
    	Collection<Method> methodCollection = new CopyOnWriteArrayList<Method>();
    	// 找到类的所有父类和接口（包含自己）
    	Collection<? extends Class<?>> allClazz = ReflectUtil.findAllClass(clazz);
    	for(Class<?> c : allClazz) {
    		Method[] methods = c.getMethods();
    		for(Method method : methods) {
    			// 方法合法 参数合法
    			if(isMethodLegal(method) && isParameterLegal(method)) {
    				methodCollection.add(method);
    			}    			
    		}
    	}
    	return methodCollection;
    }
	
	/**
	 * 方法存在EventHandler注解
	 * 方法不为静态方法
	 * @param method
	 * @return
	 */
	private boolean isMethodLegal(Method method) {
		return method.isAnnotationPresent(EventHandler.class) &&
				!method.isSynthetic();
	}
	
	/**
	 * 只有一个参数
	 * 参数是继承Event的
	 * @param method
	 * @return
	 */
	private boolean isParameterLegal(Method method) {
		// 获取参数类集合
		Class<?>[] clazz = method.getParameterTypes();
		return clazz.length == 1 && Event.class.isAssignableFrom(clazz[0]);
	}
}
