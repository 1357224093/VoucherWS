package com.esa2000.voucher.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class RequestXmlParser {

	/**
	 * 请求xml文档结构转map
	 * 
	 * @param requestXml
	 * @return
	 */
	public static Map<String, Object> requestXml2Map(String requestXml) throws Exception{
		ByteArrayInputStream is = new ByteArrayInputStream(
				requestXml.getBytes("UTF-8"));
		
		Map<String, Object> map = requestXml2Map(is);
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 请求xml文档结构转map
	 * 
	 * @param requestXml
	 * @return
	 */
	public static Map<String, Object> requestXml2Map(byte[] requestXml) throws Exception{
		ByteArrayInputStream is = new ByteArrayInputStream(requestXml);
		Map<String, Object> map = requestXml2Map(is);
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 请求xml文档 elementKey 元素下的结构转map
	 * 
	 * @param requestXml
	 * @param elementKey
	 * @return
	 */
	public static Object requestXml2Map(String requestXml, String elementKey) throws Exception{

		if (elementKey == null) {
			return null;
		}
		ByteArrayInputStream is = new ByteArrayInputStream(
				requestXml.getBytes());
		Object map = requestXml2Map(is).get(elementKey);
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 请求xml文档 elementKey 元素下的结构转map
	 * 
	 * @param requestXml
	 * @param elementKey
	 * @return
	 */
	public static Object requestXml2Map(byte[] requestXml, String elementKey) throws Exception{

		if (elementKey == null) {
			return null;
		}
		ByteArrayInputStream is = new ByteArrayInputStream(requestXml);
		Object map = requestXml2Map(is).get(elementKey);
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 请求xml文档 elementKey 元素下的结构转map
	 * 
	 * @param requestXml
	 * @return
	 */
	public static Object requestXml2Map(InputStream requestXml,
			String elementKey) throws Exception {
		return requestXml2Map(requestXml).get(elementKey);
	}

	/**
	 * 请求xml文档Request元素下的结构转map
	 * 
	 * @param requestXml
	 * @return
	 * @throws Exception 
	 */
	public static Map<String, Object> requestXml2Map(InputStream requestXml) throws Exception {

		Map<String, Object> xmlMap = new HashMap<String, Object>();
		
		SAXReader saxReader = new SAXReader();
		saxReader.setEncoding("UTF-8");
		Document document = saxReader.read(requestXml);
		// logger.info("received xml ======= "+document.asXML());
		Element rootElement = document.getRootElement();

		// 获取Header标签
		Map<String, Object> headerXmlMap = new LinkedHashMap<String, Object>();
		Element headerElement = rootElement.element("Header");
		for (Iterator iter = headerElement.elements().iterator(); iter
				.hasNext();) {
			Element e = (Element) iter.next();
			headerXmlMap.put(e.getName(), e.getText());
		}
		// 装载 Header 元素子节点
		xmlMap.put(headerElement.getName(), headerXmlMap);

		// 获取Request标签
		Map<String, Object> requestXmlMap = new HashMap<String, Object>();
		Element bodyElement = rootElement.element("Body");
		Element requestElement = bodyElement.element("Request");
		for (Iterator iter = requestElement.elements().iterator(); iter
				.hasNext();) {
			Element e = (Element) iter.next();
			requestXmlMap.put(e.getName(), e.getText());
		}
		// 装载 Request 元素子节点
		xmlMap.put(requestElement.getName(), requestXmlMap);

		return xmlMap;
	}

}
