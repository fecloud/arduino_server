/**
 * @(#) ServletProccess.java Created on 2014-3-28
 *
 * Copyright © 2013 深圳企业云科技有限公司  版权所有
 */
package com.fcloud.servlet.arduino;

import com.fcloud.bean.arduino.ArduinoCmd;

/**
 * The class <code>ServletProccess</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public interface ServletProccess {
	
	public String process(ArduinoCmd message);

	int [] getType();
}
