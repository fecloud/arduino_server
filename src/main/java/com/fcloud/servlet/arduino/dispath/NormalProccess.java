/**
 * @(#) NormalProccess.java Created on 2014-3-28
 *
 * Copyright © 2013 深圳企业云科技有限公司  版权所有
 */
package com.fcloud.servlet.arduino.dispath;

import java.util.List;

import com.fcloud.bean.arduino.ArduinoCmd;
import com.fcloud.business.ArduinoCenter;
import com.fcloud.servlet.arduino.ArduinoServletType;
import com.fcloud.servlet.arduino.ServletProccess;
import com.fcloud.socket.ArduinoSocket;

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
		case ArduinoServletType.getClientList:
			return buildClientList();
		}

		return null;
	}

	protected String buildClientList() {
		final StringBuilder builder = new StringBuilder();
		final List<ArduinoSocket> arduinoSockets = center.getConntions();
		if (null != arduinoSockets) {
			for (ArduinoSocket socket : arduinoSockets) {
				builder.append(socket.getSocketAddress().toString().replaceAll("/", "")).append(
						"\r\n");
			}
		}
		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fcloud.servlet.arduino.ServletProccess#getType()
	 */
	@Override
	public int[] getType() {
		return new int[] { ArduinoServletType.getClientNum, ArduinoServletType.getClientList };
	}

}
