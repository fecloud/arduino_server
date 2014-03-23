/**
 * @(#) ArduinoSocket.java Created on 2014-3-19
 *
 * Copyright © 2013 深圳企业云科技有限公司  版权所有
 */
package com.fcloud.socket;

/**
 * The class <code>ArduinoSocket</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public interface ArduinoSocket {

	void onOpen();

	void onClose();

	void onMessage(String message);

	void onError(Exception exception);

}
