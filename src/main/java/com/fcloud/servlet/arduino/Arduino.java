/**
 * @(#) Arduino.java Created on 2014-3-23
 *
 * Copyright © 2013 深圳企业云科技有限公司  版权所有
 */
package com.fcloud.servlet.arduino;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.fcloud.bean.arduino.ArduinoCmd;
import com.fcloud.business.ArduinoCenter;
import com.fcloud.servlet.arduino.dispath.NormalProccess;
import com.fcloud.utils.Tools;

/**
 * The class <code>Arduino</code>
 * 
 * @author braver
 * @version 1.0
 */
public class Arduino extends HttpServlet {

	Logger logger = Logger.getLogger(Arduino.class);

	ArduinoCenter center;

	protected List<ServletProccess> wxProcesses = new ArrayList<ServletProccess>();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Arduino() {
		center = ArduinoCenter.getInstance();
		wxProcesses.add(new NormalProccess());
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
		final String t = req.getParameter("t");
		final String v = req.getParameter("v");

		logger.debug(String.format("request info [type:%1$s value:%2$s]",
				ArduinoServletType.paserString(t), v));

		if (t == null || !Tools.isNum(t)) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);// 500
		} else {
			ArduinoCmd cmd = new ArduinoCmd(Integer.parseInt(t), v);
			final String w = messageProcess(cmd);
			if (null != w)
				resp.getWriter().write(w);
			else
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);// 404
		}
	}

	protected String messageProcess(ArduinoCmd message) {

		for (ServletProccess p : wxProcesses) {
			for (int t : p.getType()) {
				if (t == message.getType()) {
					return p.process(message);
				}
			}
		}
		return null;
	}

}
