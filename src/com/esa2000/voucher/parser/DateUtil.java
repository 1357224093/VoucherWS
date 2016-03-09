package com.esa2000.voucher.parser;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static String getStringFromDate(Date date, String pattern) {

		if (pattern == null) {
			pattern = "yyyMMddhhmmss";
		}

		SimpleDateFormat sdf = new SimpleDateFormat(pattern);

		return sdf.format(date);

	}

	/**
	 * 日期比较
	 * 
	 * @param DATE1
	 * @param DATE2
	 * @return
	 */
	public static int compare_date(String DATE1, String DATE2) {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				//System.out.println("dt1 在dt2前");
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				//System.out.println("dt1在dt2后");
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}
	
	
	
	
}
