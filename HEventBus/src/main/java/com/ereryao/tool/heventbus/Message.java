package com.ereryao.tool.heventbus;

import java.util.UUID;

public abstract class Message
{
    protected Message() {
    	this.messageType = this.getClass().getName();
    };

    private String messageType;
    private UUID aggregateId = UUID.randomUUID();
    
    protected void setMessageType(String messageType) {
    	this.messageType = messageType;
    }
    
    public UUID getUUID() {
    	return this.aggregateId;
    }

	public String getMessageType() {
		return messageType;
	}
    
}