package com.esa2000.voucher.bll;

import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.esa2000.voucher.entity.ReceiptInfo;
import com.esa2000.voucher.parser.HeaderResponseEntity;
import com.esa2000.voucher.parser.ParserUtil;
import com.esa2000.voucher.parser.StringUtil;

public class Business {
	
	/**
	 * xml 文档 转 字符串
	 * @param headerElement
	 * @param dataList
	 * @return
	 */
	public static String xmlDoc2String(Element headerElement, List<ReceiptInfo> dataList){
		return StringUtil.toString(ParserUtil.buildDocument(headerElement, dataList));
	}
	
	/**
	 * xml 文档 转 字符串
	 * @param headerElement
	 * @param data
	 * @return
	 */
	public static String xmlDoc2String(Map<String, Object> headerParamMap, HeaderResponseEntity hre, ReceiptInfo data){
		return StringUtil.toString(ParserUtil.buildDocument(headerParamMap, hre, data));
	}
	
	/**
	 * xml 文档 转 字符串
	 * @param headerElement
	 * @param data
	 * @return
	 */
	public static String xmlDoc2String(Map<String, Object> headerParamMap, HeaderResponseEntity hre, List<ReceiptInfo> dataList){
		return StringUtil.toString(ParserUtil.buildDocument(headerParamMap, hre, dataList));
	}
	
	/**
	 * xml 文档 转 字符串
	 * @param headerElement
	 * @param data
	 * @return
	 */
	public static String xmlDoc2String(Map<String, Object> headerParamMap, HeaderResponseEntity hre, List<ReceiptInfo> dataList, int recordCount){
		return StringUtil.toString(ParserUtil.buildDocument(headerParamMap, hre, dataList, recordCount));
	}
	
	
}
