package com.esa2000.voucher.conf;

import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * 配置数据库连接信息
 * 
 */
public class SysConfig {
	private static Logger logger = Logger.getLogger(SysConfig.class);
	// 配置文件的所有信息
	private static Properties properties = null;

	static {
		initDBSource();
	}


	/**
	 * 读取配置文件
	 */
	private static final void initDBSource() {
		properties = new Properties();
		try {
			// 加载配置文件
			properties.load(SysConfig.class.getResourceAsStream("config.properties"));

			logger.info("加载数据库配置文件成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String getValue(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	public static String getValue(String key) {
		return properties.getProperty(key);
	}

	public static float getFloatValue(String key) {
		return Float.valueOf(properties.getProperty(key));
	}

	public static int getIntValue(String key) {
		String v = properties.getProperty(key);
		if (v == null || v.length() == 0)
			v = "0";
		return Integer.parseInt(v);
	}

	public static boolean getBooleanValue(String key) {
		return Boolean.parseBoolean(getValue(key, "false"));
	}

}
