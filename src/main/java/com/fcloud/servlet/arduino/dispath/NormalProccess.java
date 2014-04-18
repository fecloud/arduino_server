/**
 * @(#) NormalProccess.java Created on 2014-3-28
 *
 * Copyright © 2013 深圳企业云科技有限公司  版权所有
 */
package com.fcloud.servlet.arduino.dispath;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.fcloud.bean.arduino.ArduinoCmd;
import com.fcloud.bean.arduino.SendMessageClient;
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

	Logger logger = Logger.getLogger(NormalProccess.class);

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
		case ArduinoServletType.sendClient:
			return sendClient(message.getValue());
		}

		return null;
	}

	/**
	 * @param value
	 * @return
	 */
	protected String sendClient(String value) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			SendMessageClient sendMessageClient = mapper.readValue(value, SendMessageClient.class);
			if (center.getConntions() != null) {
				for (ArduinoSocket arduinoSocket : center.getConntions()) {
					if (sendMessageClient.getDevice().equals(arduinoSocket.getName())) {
						return arduinoSocket.sendLine(sendMessageClient.getMessage()) ? "1" : "0";
					}
				}
			}
		} catch (IOException e) {
			logger.error("sendClient", e);
		}
		return "0";
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
		return new int[] { ArduinoServletType.getClientNum, ArduinoServletType.getClientList,
				ArduinoServletType.sendClient };
	}

}
