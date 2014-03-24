/**
 * @(#) ArduinoCmd.java Created on 2014-3-23
 *
 * Copyright © 2013 深圳企业云科技有限公司  版权所有
 */
package com.fcloud.bean.arduino;

/**
 * The class <code>ArduinoCmd</code>
 * 
 * @author braver
 * @version 1.0
 */
public class ArduinoCmd {

	private String key;

	private String value;
	
	public static final String Tokenizer = ":";

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(key).append(":").append(value);
		return builder.toString();
	}

	public static final ArduinoCmd fromString(String str) {
		if (null != str && str.contains(":")) {
			final String[] strs = str.split(":");
			if (null != strs && strs.length == 2) {
				ArduinoCmd arduinoCmd = new ArduinoCmd();
				arduinoCmd.setKey(strs[0]);
				arduinoCmd.setValue(strs[1]);
				return arduinoCmd;
			}
		}
		return null;

	}
}
