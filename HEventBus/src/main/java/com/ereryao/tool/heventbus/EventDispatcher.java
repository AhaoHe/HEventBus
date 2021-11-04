package com.ereryao.tool.heventbus;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public class EventDispatcher {
	
	// 线程本地队列
	private final ThreadLocal<Queue<EvenAndInvokersStore>> queue =
            new ThreadLocal<Queue<EvenAndInvokersStore>>() {
              @Override
              protected Queue<EvenAndInvokersStore> initialValue() {
            	  // 双端队列
            	  return new ArrayDeque<EvenAndInvokersStore>();
              }
			};
    
	// 线程本地flag
    private final ThreadLocal<Boolean> dispatching =
            new ThreadLocal<Boolean>() {
              @Override
              protected Boolean initialValue() {
            	  return false;
              }
            };
    
    
    protected void dispatch(Event event, Set<Invoker> invokers) throws TimeoutException{
    	if(event == null) return;
    	if(invokers == null) return;
    	// 获取线程私有的队列
    	Queue<EvenAndInvokersStore> queueForThread = queue.get();
    	// 往队列写入需要被转发的 Event(事件本身+监听者们)
    	queueForThread.offer(new EvenAndInvokersStore(event, invokers));

    	// 分发是否已经结束
    	if (!dispatching.get()) {
    		// 结束就开始新一轮分发
    		dispatching.set(true);
    		try {
    			EvenAndInvokersStore nextEvent;
    			// 从队列中取出下一个事件
    			while ((nextEvent = queueForThread.poll()) != null) {
    				// 使用迭代器 遍历执行队列中的事件
    				while (nextEvent.getInvokerIterator().hasNext()) {
						// 监听者通过反射执行方法: Invoker.java:line 26
						nextEvent.getInvokerIterator().next().dispatchEvent(nextEvent.getEvent());
					}
    			}
    		} finally {
    			// 移除当前线程的分发
    			dispatching.remove();
    			queue.remove();
    		}
    	}
	}
}  
