/**
 * @(#) ArduinoSocket.java Created on 2014-3-19
 *
 * Copyright © 2013 深圳企业云科技有限公司  版权所有
 */
package com.fcloud.socket;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * The class <code>ArduinoSocket</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public interface ArduinoSocket {

	void onOpen(ArduinoSocket arduinoSocket);

	void onClose();

	void onMessage(ArduinoSocket arduinoSocket,String message);

	void onError(ArduinoSocket arduinoSocket,Exception exception);
	
	boolean isConneted();
	
	void close();
	
	boolean send(String message)throws IOException;
	
	boolean sendLine(String message)throws IOException;
	
	String getName();
	
	void setName(String name);
	
	SocketAddress getSocketAddress();

	void setSocketAddress(SocketAddress address);
	
}
