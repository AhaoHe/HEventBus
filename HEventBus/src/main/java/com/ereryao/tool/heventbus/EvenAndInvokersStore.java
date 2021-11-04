package com.ereryao.tool.heventbus;

import java.util.Iterator;
import java.util.Set;

/**
 * 用于临时存储Event和订阅的集合
 * @author ahaoh
 *
 */
public class EvenAndInvokersStore {

	private Event event;
	private Set<Invoker> invokers;
	private Iterator<Invoker> invokerIterator;
	
	protected EvenAndInvokersStore(Event event, Set<Invoker> invokers) {
		this.event = event;
		this.invokers = invokers;
		this.invokerIterator = invokers.iterator();
	}

	public Event getEvent() {
		return event;
	}

	public Set<Invoker> getInvokers() {
		return invokers;
	}
	
	public Iterator<Invoker> getInvokerIterator() {
		return invokerIterator;
	}
	
}
