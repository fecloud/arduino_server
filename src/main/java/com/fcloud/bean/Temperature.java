/**
 * @(#) Temperature.java Created on 2014-3-11
 *
 * Copyright © 2013 深圳企业云科技有限公司  版权所有
 */
package com.fcloud.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * The class <code>Temperature</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public class Temperature implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private long id ;
	
	private double temperature;
	
	private Date time;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the temperature
	 */
	public double getTemperature() {
		return temperature;
	}

	/**
	 * @param temperature the temperature to set
	 */
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	/**
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(Date time) {
		this.time = time;
	}
	
	

}
