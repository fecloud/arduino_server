/**
 * @(#) Arduino.java Created on 2014-3-23
 *
 * Copyright © 2013 深圳企业云科技有限公司  版权所有
 */
package com.fcloud.servlet.arduino;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.fcloud.ArduinoConst;
import com.fcloud.bean.arduino.ArduinoCmd;
import com.fcloud.business.ArduinoCenter;

/**
 * The class <code>Arduino</code>
 * 
 * @author braver
 * @version 1.0
 */
public class Arduino extends HttpServlet {

	Logger logger = Logger.getLogger(Arduino.class);

	ArduinoCenter center ;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Arduino(){
		center = ArduinoCenter.getInstance();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=UTF-8");
		final String reqString = req.getParameter("cmd");

		logger.debug("reqString :" + reqString);

		if (reqString == null || null == ArduinoCmd.fromString(reqString)) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);// 500
		} else {
			// ok
			ArduinoCmd cmd = ArduinoCmd.fromString(reqString);
			if (center.isConneted()) {
				logger.debug("arduino client conneted");
				center.sendln(cmd.toString());
				resp.getWriter().println(ArduinoConst.success);
			} else {
				logger.debug("arduino client disconneted");
				resp.getWriter().println(ArduinoConst.not_connet);
			}
		}
	}

}
