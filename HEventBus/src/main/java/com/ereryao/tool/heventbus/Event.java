package com.ereryao.tool.heventbus;

import java.util.Date;

/**
 * 事件，用于标识发布事件的
 * 自定义一个类继承此类
 * @author ahaoh
 *
 */
public abstract class Event extends Message {

	private Date timestamp;
	
    protected Event()
    {
    	timestamp = new Date();
    }
    
    public Date getCreatTime() {
    	return this.timestamp;
    }
    
}
