package com.ereryao.tool.heventbus.handlers;

import com.ereryao.tool.heventbus.EventHandler;
import com.ereryao.tool.heventbus.events.CustomEvent;

public class CustomEventHandler{
	
	@EventHandler(maxExecuteTime = 2000)
	public void handle(CustomEvent event) {
		// TODO Auto-generated method stub
		System.out.println(Thread.currentThread().getId()+"执行CustomEvent事件:" + event.getMessageType());
	}

}
