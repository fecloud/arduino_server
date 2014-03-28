/**
 * @(#) ArduinoSocketListener.java Created on 2014-3-24
 *
 * Copyright © 2013 深圳企业云科技有限公司  版权所有
 */
package com.fcloud.socket;

/**
 * The class <code>ArduinoSocketListener</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public interface ArduinoSocketListener {

	void onAduinoSocketOpen(ArduinoSocket conn);
	
	void onAduinoSocketError(ArduinoSocket conn,Exception e);
	
	void onAduinoSocketClose(ArduinoSocket conn);
	
	void onAduinoSocketMessae(ArduinoSocket conn,String message);
	
	void onWriteDemand(ArduinoSocket conn);
	
}
