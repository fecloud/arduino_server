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

	private int type;

	private String value;

	public ArduinoCmd() {
	}

	/**
	 * @param type
	 * @param value
	 */
	public ArduinoCmd(int type, String value) {
		super();
		this.type = type;
		this.value = value;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		this.type = type;
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
		builder.append("type").append("=").append(this.type);
		if (null != value) {
			builder.append("value").append("=").append(this.value);
		}
		return builder.toString();
	}

}
