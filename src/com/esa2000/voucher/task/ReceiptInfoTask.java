package com.esa2000.voucher.task;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.esa2000.voucher.control.ReceiptControl;
import com.esa2000.voucher.database.HibernateSessionFactory;
import com.esa2000.voucher.entity.ReceiptInfo;
import com.esa2000.voucher.entity.ReceiptInfoBak;

public class ReceiptInfoTask {

	static Properties properties = null;
	Session session = null;
	
	{
		try {
			if(properties == null){
				properties = new Properties();
				properties.load(ReceiptInfoTask.class.getResourceAsStream("task.cfg.properties"));
			}
			
			if(session == null){
				session = HibernateSessionFactory.getSession();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取周期数
	 * @return
	 */
	public int getReserve(){
		return Integer.valueOf(properties.getProperty("bak.task.reserve", "180"));
	}
	
	/**
	 * 获取任务执行小时
	 * @return
	 */
	public int getHour(){
		return Integer.valueOf(properties.getProperty("bak.task.hour", "0"));
	}
	
	/**
	 * 获取任务执行分钟
	 * @return
	 */
	public int getMinute(){
		return Integer.valueOf(properties.getProperty("bak.task.minute", "0"));
	}
	
	/**
	 * 获取任务执行秒数
	 * @return
	 */
	public int getSecond(){
		return Integer.valueOf(properties.getProperty("bak.task.second", "0"));
	}
	
	/**
	 * 主表查询历史记录
	 * 
	 * @param parameter
	 */
	public int moveShitoryRecord() {
		
		//作为查询参数用来避免查询缓存
		SimpleDateFormat randomDate = new SimpleDateFormat("yyyyMMddHHmmss");
		String random = randomDate.format(new Date());
		
		//获取当前日期
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
    	String currTime = f.format(new Date());
    	
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("random", random);
		parameter.put("currTime", currTime);
    	parameter.put("days", properties.getProperty("bak.task.reserve", "180"));
		
		ReceiptControl control = new ReceiptControl(session);
		
		
		List<ReceiptInfo> list = control.queryShitoryRecord(parameter);
		int count = list.size();
		
		Transaction tx = session.beginTransaction();
		
		try{
			for (int i = 0; i < list.size(); i++) {
				ReceiptInfo r = list.get(i);
				
				ReceiptInfoBak receiptInfoBak = new ReceiptInfoBak();
				
				receiptInfoBak.setVoucherName(r.getVoucherName());
				receiptInfoBak.setVoucherId(r.getVoucherId());
				receiptInfoBak.setTransEngName(r.getTransEngName());
				receiptInfoBak.setTransName(r.getTransName());
				receiptInfoBak.setSysName(r.getSysName());
				receiptInfoBak.setFileName(r.getFileName());
				receiptInfoBak.setCode(r.getCode());
				receiptInfoBak.setSealType(r.getSealType());
				receiptInfoBak.setSealOpName(r.getSealOpName());
				receiptInfoBak.setSealModel(r.getSealModel());
				receiptInfoBak.setSealTime(r.getSealTime());
				receiptInfoBak.setSealResult(r.getSealResult());
				receiptInfoBak.setEcmId(r.getEcmId());
				receiptInfoBak.setOrgid(r.getOrgid());
				receiptInfoBak.setOrgname(r.getOrgname());
				receiptInfoBak.setTrnid(r.getTrnid());
				receiptInfoBak.setClientid(r.getClientid());
				receiptInfoBak.setAccopenbnk(r.getAccopenbnk());
				receiptInfoBak.setVoucherbank(r.getVoucherbank());
				receiptInfoBak.setExttellerid(r.getExttellerid());
				receiptInfoBak.setCltacc(r.getCltacc());
				receiptInfoBak.setPayacc(r.getPayacc());
				receiptInfoBak.setPaynam(r.getPaynam());
				receiptInfoBak.setRcvacc(r.getRcvacc());
				receiptInfoBak.setRcvnam(r.getRcvnam());
				receiptInfoBak.setContractid(r.getContractid());
				receiptInfoBak.setAmt(r.getAmt());
				receiptInfoBak.setCertid(r.getCertid());
				receiptInfoBak.setTrndate(r.getTrndate());
				receiptInfoBak.setSelnum(r.getSelnum());
				
				String id = r.getId();
				control.storeReceiptInfoBak(receiptInfoBak);
				control.deleteReceiptInfo(id);
				
			}
			
			tx.commit();
			
		}catch(Exception e){
			count = 0;
			tx.rollback();
			e.printStackTrace();
		}finally{
			if(session != null){				
				session.clear();
			}
			if(session != null){				
				session.close();
			}			
		}
		
		return count;
	}

	/**
	 * 时间加减得到天数
	 * 
	 * @param beginDateStr
	 * @param endDateStr
	 * @return long
	 * @author Administrator
	 */
	public long getDaySub(String beginDateStr, String endDateStr) {
		long day = 0;
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
		java.util.Date beginDate;
		java.util.Date endDate;
		try {
			beginDate = format.parse(beginDateStr);
			endDate = format.parse(endDateStr);
			day = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return day;
	}

}
