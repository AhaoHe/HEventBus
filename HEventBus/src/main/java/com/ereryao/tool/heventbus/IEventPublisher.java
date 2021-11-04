package com.ereryao.tool.heventbus;

import java.util.concurrent.TimeoutException;

public interface IEventPublisher {
	public boolean dispatch(Event event) throws TimeoutException;
	public boolean dispatchAsync(Event event) throws TimeoutException;
}
