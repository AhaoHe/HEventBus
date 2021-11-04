package com.ereryao.tool.heventbus;

import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 * 事件总线（领域模型或理解为单个线程），单例，也可以放到领域模型的Entity中方便单元测试
 * 
 * 使用方法：
 * 1.一个请求发送过来，使用EventBus.register(new CustomEventHandler())注册一个事件处理类监听事件
 * CustomEventHandler自建类中包含方法，方法含@EventHandler注解，和唯一参数CustomEvent（自建类继承Event）
 * 此时，CustomEventHandler监听CustomEventEvent事件。
 * 2.然后在处理请求的过程中，触发事件。EventBus.dispatch(new CustomEvent())
 * EventBus事件总线就会开始执行CustomEventHandler监听的方法了。
 * 
 * 设计思想：
 * 发布订阅模式
 * 
 * @author ahaoh
 *
 */
public final class EventBus implements IEventHandlerRegister, IEventPublisher {

    // 注册器
    private final EventRegistry invokerRegistry = new EventRegistry(this);

    // 事件分发器
	private final EventDispatcher dispatcher = new EventDispatcher(/* ExecutorFactory.getDirectExecutor() */);

    // 异步事件分发器 （暂时未实现）
	private final EventDispatcher asyncDispatcher = new EventDispatcher(/* ExecutorFactory.getThreadPoolExecutor() */);

	private static EventBus eventbus;
	
	public static EventBus getSingleInstance() {
		return eventbus == null ? new EventBus() : eventbus;
	}
	
    // 事件分发
    public boolean dispatch(Event event) throws TimeoutException {
        return dispatch(event, dispatcher);
    }

    // 异步事件分发
    public boolean dispatchAsync(Event event) throws TimeoutException {
        return dispatch(event, asyncDispatcher);
    }

    // 内部事件分发
    private boolean dispatch(Event event, EventDispatcher dispatcher) throws TimeoutException {
        checkEvent(event);
        // 1.获取事件数组
        Set<Invoker> invokers = invokerRegistry.getInvokers(event);
        // 如果不存在监听事件，说明是死事件
        if(invokers == null) {
        	return false;
        }
        // 2.一个事件可以被监听N次，不关心调用结果
        dispatcher.dispatch(event, invokers);
        return true;
    }

    // 事件总线注册
    public void register(Class<?> listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener can not be null!");
        }
        invokerRegistry.register(listener);
    }

    private void checkEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("event");
        }
        if (!(event instanceof Event)) {
            throw new IllegalArgumentException("Event type must by " + Event.class);
        }
    }
	
	
}
