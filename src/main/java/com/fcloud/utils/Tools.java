/**
 * @(#) Tools.java Created on 2014-2-24
 *
 * Copyright © 2013 深圳企业云科技有限公司  版权所有
 */
package com.fcloud.utils;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * The class <code>Tools</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public final class Tools {

	/**
	 * 数组转成arraylist
	 * 
	 * @param args
	 * @return
	 */
	public static final ArrayList<String> toArray(String... args) {
		if (null != args) {
			final ArrayList<String> list = new ArrayList<String>();
			for (String s : args) {
				list.add(s);
			}
			return list;
		}
		return null;
	}

	public static final String[] toArrayString(String... args) {
		return args;
	}

	public static final String joinStrings(String[] args) {
		if (null != args && args.length > 0) {
			final StringBuilder builder = new StringBuilder();
			for (String s : args) {
				builder.append(s);
			}
			return builder.toString();
		}
		return null;

	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isNum(String string) {
		if (null != string) {
			return Pattern.compile("\\d+").matcher(string).matches();
		}
		return false;
	}

}
