/**
 * @(#) ArduinoCenter.java Created on 2014-3-23
 *
 * Copyright © 2013 深圳企业云科技有限公司  版权所有
 */
package com.fcloud.business;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.fcloud.socket.ArduinoServer;
import com.fcloud.socket.ArduinoSocket;

/**
 * The class <code>ArduinoCenter</code>
 * 
 * @author braver
 * @version 1.0
 */
public class ArduinoCenter extends ArduinoServer {

	Logger logger = Logger.getLogger(ArduinoCenter.class);

	private static ArduinoCenter arduinoCenter;

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
	public void onOpen(ArduinoSocket arduinoSocket) {
		logger.debug("onOpen name:" + arduinoSocket.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fcloud.socket.ArduinoSocket#onClose()
	 */
	@Override
	public void onClose() {
		logger.debug("onClose");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fcloud.socket.ArduinoSocket#onMessage(java.lang.String)
	 */
	@Override
	public void onMessage(ArduinoSocket arduinoSocket, String message) {
		logger.debug("message:" + message);
		// if(message.contains("hand")){
		// sendln("200");
		// }
		try {
			arduinoSocket.sendLine("hello!" + System.currentTimeMillis());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fcloud.socket.ArduinoSocket#onError(java.lang.Exception)
	 */
	@Override
	public void onError(ArduinoSocket arduinoSocket, Exception exception) {
		logger.debug("onError");
	}

	public static void main(String[] args) {
		ArduinoCenter.getInstance();
	}
	
	public int getClientNum(){
		return conntions.size();
	}
}
