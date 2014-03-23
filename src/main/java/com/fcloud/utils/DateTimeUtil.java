/**
 * @(#) DateTimeUtil.java Created on 2014-2-28
 *
 * Copyright © 2013 深圳企业云科技有限公司  版权所有
 */
package com.fcloud.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The class <code>DateTimeUtil</code>
 * 
 * @author braver
 * @version 1.0
 */
public final class DateTimeUtil {

	/**
	 * Date time format: yyyyMMddHHmmss
	 */
	public final static String FORMAT_1 = "yyyyMMddHHmmss";

	/**
	 * Date time format: yyyy-MM-dd HH:mm:ss
	 */
	public final static String FORMAT_2 = "yyyy-MM-dd HH:mm:ss";

	/**
	 * Date time format: yyyy-MM-dd HH:mm:ss SSS
	 */
	public final static String FORMAT_MS = "yyyy-MM-dd HH:mm:ss.SSS";

	/**
	 * Formatter, format: yyyyMMddHHmmss
	 */
	// private final static SimpleDateFormat formatter = new
	// SimpleDateFormat(FORMAT_1);

	/**
	 * Get current time as format: yyyy-MM-dd HH:mm:ss.
	 */
	public static String now() {
		return new SimpleDateFormat(FORMAT_2).format(new Date());
	}

	/**
	 * Get current time as given format.
	 * 
	 * @param format
	 * @return
	 */
	public static String now(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}

	/**
	 * Convert time object to text as format 'yyyyMMddHHmmss'.
	 * 
	 * @param time
	 *            The date time object of Date.
	 * @return Returns the text of this date time
	 */
	public static String toText(Date time) {
		return new SimpleDateFormat(FORMAT_1).format(time);
	}

	/**
	 * Convert milliseconds time to text as format 'yyyyMMddHHmmss'.
	 * 
	 * @param time
	 *            The millisecond time by long.
	 * @return Returns the text of this date time
	 */
	public static String toText(long time) {
		return new SimpleDateFormat(FORMAT_1).format(new Date(time));
	}

	/**
	 * Convert milliseconds time to text as format 'yyyyMMddHHmmss'.
	 * 
	 * @param time
	 *            The millisecond time by string.
	 * @return the text of this date time
	 */
	public static String toText(String time) {
		return new SimpleDateFormat(FORMAT_1).format(Long.parseLong(time));
	}

	/**
	 * Convert milliseconds time to text as given format.
	 * 
	 * @param time
	 *            The millisecond time by long.
	 * @param format
	 *            The format string for date time.
	 * @return Returns the text of this date time
	 */
	public static String toText(long time, String format) {
		return new SimpleDateFormat(format).format(new Date(time));
	}

	/**
	 * Convert Date object to text as given format.
	 * 
	 * @param time
	 *            The Date Object.
	 * @param format
	 *            The format string for date time.
	 * @return Returns the text of this date time
	 */
	public static String toText(Date time, String format) {
		return new SimpleDateFormat(format).format(time);
	}

	/**
	 * Convert a time string like this 'yyyyMMddHHmmss' to milliseconds.
	 * 
	 * @param timeString
	 *            time string like: 'yyyyMMddHHmmss'
	 * @return Return the milliseconds the given time string represented as a
	 *         long value.
	 * @throws ToolException
	 */
	public static long toMillis(String timeString) throws Exception {
		try {
			return new SimpleDateFormat(FORMAT_1).parse(timeString).getTime();
		} catch (ParseException ex) {
			throw new Exception("Date time parsing error, Detail: " + ex.getMessage(), ex);
		}
	}

	/**
	 * Convert a time string like this 'yyyyMMddHHmmss' to milliseconds.
	 * 
	 * @param timeString
	 *            time string like: 'yyyyMMddHHmmss'
	 * @return Return the milliseconds the given time string represented as a
	 *         string value.
	 * @throws ToolException
	 */
	public static String toMilliString(String timeString) throws Exception {
		return String.valueOf(toMillis(timeString));
	}

	/*
	 * 是否是出太阳时间
	 */
	public static boolean isSun(Date date) {
		final String d = toText(date, "HH");
		if (Integer.valueOf(d) > 6 && Integer.valueOf(d) < 19) {
			return true;
		} else {
			return false;
		}
	}
}
