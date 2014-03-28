/**
 * @(#) NormalProccess.java Created on 2014-3-28
 *
 * Copyright © 2013 深圳企业云科技有限公司  版权所有
 */
package com.fcloud.servlet.arduino.dispath;

import com.fcloud.bean.arduino.ArduinoCmd;
import com.fcloud.business.ArduinoCenter;
import com.fcloud.servlet.arduino.ArduinoServletType;
import com.fcloud.servlet.arduino.ServletProccess;

/**
 * The class <code>NormalProccess</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public class NormalProccess implements ServletProccess {

	protected ArduinoCenter center = ArduinoCenter.getInstance();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fcloud.servlet.arduino.ServletProccess#process(com.fcloud.bean.arduino
	 * .ArduinoCmd)
	 */
	@Override
	public String process(ArduinoCmd message) {

		switch (message.getType()) {
		case ArduinoServletType.getClientNum:
			return "" + center.getClientNum();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fcloud.servlet.arduino.ServletProccess#getType()
	 */
	@Override
	public int[] getType() {
		return new int[] { ArduinoServletType.getClientNum };
	}

}
