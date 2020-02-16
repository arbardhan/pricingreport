package com.arbardhan.ms.pricingreport.util;

import org.springframework.context.ApplicationEvent;

public class ThirdPartyServiceFirstRecordOfNextStream extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3638603742418795178L;

	public ThirdPartyServiceFirstRecordOfNextStream(Object source) 
	{
		super(source);	
	}
	
	public long getCallTime()
	{
		return System.currentTimeMillis();
	}

}
