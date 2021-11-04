package com.ereryao.tool.heventbus;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.ereryao.tool.heventbus.events.CustomEvent;
import com.ereryao.tool.heventbus.handlers.CustomEventHandler;

import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Assertions;

public class EventBusTest {
	
	private EventBus eventbus;
	private IEventPublisher publisher;
	
	@Test
	@RepeatedTest(100) //10为当前用例执行的次数
	@Execution(ExecutionMode.CONCURRENT)  //CONCURRENT表示支持多线程
    public void EventBusTest() {
		// 初始化消息总线
		eventbus = eventbus == null ? new EventBus() : eventbus;
		publisher = eventbus;
		// 在项目启动时注册handler
		eventbus.register(CustomEventHandler.class);
		
		// 在业务时发布
		try {
			publisher.dispatch(new CustomEvent());
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Assertions.assertEquals(eventbus, publisher);
		System.out.println(eventbus.hashCode() + " - " +Thread.currentThread().getId());
    }
}
