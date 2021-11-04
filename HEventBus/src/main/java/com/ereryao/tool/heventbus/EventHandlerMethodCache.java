package com.ereryao.tool.heventbus;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 随意写的线程安全缓存类
 * 未来可能要改成通用点的，比如用Loader<K,V>泛型这样，工厂生成缓存对象
 * @author ahaoh
 *
 */
public class EventHandlerMethodCache {

	private static final ConcurrentMap<Class<?>, Future<Collection<Method>>> 
								cache = new ConcurrentHashMap<Class<?>, Future<Collection<Method>>>();
	
	public static Collection<Method> get(Class<?> clazz) {
        Future<Collection<Method>> future = cache.get(clazz);
        if (future == null) {
            return null;
        }
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
 
    /**
     * 存入缓存
     *
     * @param key
     * @param val
     * @return
     */
    public static Collection<Method> put(Class<?> key, Collection<Method> val) {
        Future<Collection<Method>> future = cache.get(key);
        if (future == null) {
            Callable<Collection<Method>> callable = new Callable<Collection<Method>>() {
                @Override
                public Collection<Method> call() throws Exception {
                    return val;
                }
            };
            FutureTask<Collection<Method>> task = new FutureTask<>(callable);
            future = cache.putIfAbsent(key, task);
            if (future == null) {
                future = task;
                task.run();
            }
        }
        try {
            return future.get();
        } catch (Exception e) {
            cache.remove(key);
            e.printStackTrace();
        }
        return null;
    }
 
    /**
     * 更新缓存中相应的值
     *
     * @param key
     * @param val
     * @return
     */
    public static Collection<Method> update(Class<?> key, Collection<Method> val) {
        Future<Collection<Method>> future = cache.get(key);
        if (future == null) {
            return null;
        }
 
        Callable<Collection<Method>> callable = new Callable<Collection<Method>>() {
            @Override
            public Collection<Method> call() throws Exception {
                return val;
            }
        };
        FutureTask<Collection<Method>> task = new FutureTask<>(callable);
        Future<Collection<Method>> replace = cache.replace(key, task);
        if (replace != null) {
            future = task;
            task.run();
        }
        try {
            return task.get();
        } catch (Exception e) {
            cache.remove(key);
        }
        return null;
    }
}
