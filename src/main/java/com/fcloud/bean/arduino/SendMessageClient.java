/**
 * @(#) SendMessageClient.java Created on 2014-4-18
 *
 * Copyright © 2013 深圳企业云科技有限公司  版权所有
 */
package com.fcloud.bean.arduino;

/**
 * The class <code>SendMessageClient</code>
 * 
 * @author braver
 * @version 1.0
 */
public class SendMessageClient {

	private String device;
	
	private String message;

	/**
	 * @return the device
	 */
	public String getDevice() {
		return device;
	}

	/**
	 * @param device the device to set
	 */
	public void setDevice(String device) {
		this.device = device;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}

