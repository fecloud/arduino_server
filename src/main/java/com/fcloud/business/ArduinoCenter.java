/**
 * @(#) ArduinoCenter.java Created on 2014-3-23
 *
 * Copyright © 2013 深圳企业云科技有限公司  版权所有
 */
package com.fcloud.business;

import org.apache.log4j.Logger;

import com.fcloud.socket.ArduinoServer;

/**
 * The class <code>ArduinoCenter</code>
 * 
 * @author braver
 * @version 1.0
 */
public class ArduinoCenter extends ArduinoServer {
	
	Logger logger = Logger.getLogger(ArduinoCenter.class);
	
	private static ArduinoCenter arduinoCenter;

	private boolean conneted;

	public static final ArduinoCenter getInstance() {
		if (null == arduinoCenter) {
			arduinoCenter = new ArduinoCenter();
			arduinoCenter.start();
		}
		return arduinoCenter;
	}

	/**
	 * @param port
	 */
	private ArduinoCenter() {
		super(9000);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fcloud.socket.ArduinoSocket#onOpen()
	 */
	@Override
	public void onOpen() {
		logger.debug("onOpen");
		conneted = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fcloud.socket.ArduinoSocket#onClose()
	 */
	@Override
	public void onClose() {
		logger.debug("onClose");
		conneted = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fcloud.socket.ArduinoSocket#onMessage(java.lang.String)
	 */
	@Override
	public void onMessage(String message) {
		logger.debug("message:" + message);
		if(message.contains("hand")){
			sendln("200");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fcloud.socket.ArduinoSocket#onError(java.lang.Exception)
	 */
	@Override
	public void onError(Exception exception) {
		logger.debug("onError");
		conneted = false;
	}

	/**
	 * @return the conneted
	 */
	public boolean isConneted() {
		return conneted;
	}

}
