/**
 * @(#) ArduinoServletType.java Created on 2014-3-28
 *
 * Copyright © 2013 深圳企业云科技有限公司  版权所有
 */
package com.fcloud.servlet.arduino;

import com.fcloud.utils.Tools;

/**
 * The class <code>ArduinoServletType</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public class ArduinoServletType {

	public static final int getClientNum = 0x1;

	public static final int getClientList = 0x2;

	public static final int sendClient = 0x3;

	public static final String paserString(int cmd) {
		switch (cmd) {
		case getClientNum:
			return "getClientNum";
		case getClientList:
			return "getClientList";
		case sendClient:
			return "sendClient";
		default:
			return "unknow";
		}
	}

	public static final String paserString(String cmd) {
		if (Tools.isNum(cmd)) {
			return paserString(Integer.parseInt(cmd));
		} else {
			return "unknow";
		}
	}

}
