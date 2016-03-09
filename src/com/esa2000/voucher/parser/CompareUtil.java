package com.esa2000.voucher.parser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.esa2000.voucher.entity.ReceiptInfo;

public class CompareUtil {

	
	/**
	 * 得到时间最大的对象索引
	 * @param dataList
	 * @return
	 */
	public static int getMaxIndexBySealTime(List<ReceiptInfo> dataList){
		
		int i,maxIndex;
		maxIndex = 0;
		for(i=0;i<dataList.size();i++){
		
			if(compare_date(dataList.get(i).getSealTime(), dataList.get(maxIndex).getSealTime())==1){   // 判断最大值
				maxIndex = i;
			}
		}
		return maxIndex;
	}
	
	/**
	 * 得到时间最小的对象索引
	 * @param dataList
	 * @return
	 */
	public static int getMinIndexBySealTime(List<ReceiptInfo> dataList){
		
		int i,minIndex;
		minIndex = 0;
		for(i=0;i<dataList.size();i++){
		
			if(compare_date(dataList.get(i).getSealTime(), dataList.get(minIndex).getSealTime())==-1){   // 判断最大值
				minIndex = i;
			}
		}
		return minIndex;
	}
	
	
	
	/**
	 * 日期比较
	 * @param DATE1
	 * @param DATE2
	 * @return if (dt1 > dt2) return 1;if (dt1==dt2) return 0; else return -1;
	 */
	public static int compare_date(Date dt1, Date dt2) {

		if(dt1==null && dt2==null){
			return 0;
		}
		if(dt1==null){
			return -1;
		}
		if(dt2==null){
			return 1;
		}

		try {
			
			if (dt1.getTime() > dt2.getTime()) {
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
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
