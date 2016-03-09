package com.esa2000.voucher.conf;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.io.InputStream;

public class Log4jLoader {

	private static Logger logger = Logger.getLogger(Log4jLoader.class);
	
	public static void loadLog4j() {
		InputStream is = null;
		try {
			is = Log4jLoader.class.getResourceAsStream("log4j.properties");
			PropertyConfigurator.configure(is);
			is.close();
			logger.info("成功加载日志配置文件");
			
		} catch (IOException e) {
			System.out.println("加载日志配置文件失败");
			e.printStackTrace();
		} finally {
			try {
				if(is != null) {
				is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
