package com.esa2000.voucher.task;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

/**
 * ReceiptInfo历史记录迁移监听
 * @author Administrator
 *
 */
public class ReceiptInfoTaskListener implements ServletContextListener {

	private static Logger logger = Logger
			.getLogger(ReceiptInfoTaskListener.class);
	
	private static Timer timer = null;  
    public static Timer getTimer(){  
        if(timer==null) {  
            return timer = new Timer();  
        }  
        return timer;  
    }  
  
    public ReceiptInfoTaskListener() {  
        super();  
    }  
	
	
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		timer.cancel();  
        servletContextEvent.getServletContext().log("【INFO】ReceiptInfo 历史记录迁移-定时器结束.");  

	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		if(timer == null) {  
            getTimer();  
        }  
		
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				
				Calendar calendar = Calendar.getInstance();
				//int year = calendar.get(Calendar.YEAR);
				//int month = calendar.get(Calendar.MONTH);
				//int day = calendar.get(Calendar.DAY_OF_MONTH);
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				int minute = calendar.get(Calendar.MINUTE);
				int second = calendar.get(Calendar.SECOND);

				ReceiptInfoTask rTask = new ReceiptInfoTask();
				
				if(hour==rTask.getHour()){
					if(minute==rTask.getMinute()){
						if(second == rTask.getSecond()){
							int recordRows = rTask.moveShitoryRecord();
							logger.info("成功迁移记录数:"+recordRows);
						}
					}
				}
				
			}
		};
        servletContextEvent.getServletContext().log("【INFO】ReceiptInfo 历史记录迁移-定时器启动.");  
        timer.schedule(task,0,1000); 

	}
	
}
