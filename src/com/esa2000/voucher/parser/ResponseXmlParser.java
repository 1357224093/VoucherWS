package com.esa2000.voucher.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ResponseXmlParser {
	
	
	/**
     * 请求xml文档结构转map
     * @param responseXml
     * @return
     */
    public static Map<String, Object> responseXml2Map(String responseXml){
    	ByteArrayInputStream is = new ByteArrayInputStream(responseXml.getBytes());
    	Map<String, Object> map = responseXml2Map(is);
    	try {
			is.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}
    	return map;
    }
    
    
    /**
     * 请求xml文档结构转map
     * @param responseXml
     * @return
     */
    public static Map<String, Object> responseXml2Map(byte[] responseXml){
    	ByteArrayInputStream is = new ByteArrayInputStream(responseXml);
    	Map<String, Object> map = responseXml2Map(is);
    	try {
			is.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}
    	return map;
    }
    
    /**
     * 请求xml文档 elementKey 元素下的结构转map
     * @param responseXml
     * @param elementKey
     * @return
     */
    public static Object responseXml2Map(String responseXml, String elementKey){
    	
    	if(elementKey == null){
    		return null;
    	}
    	ByteArrayInputStream is = new ByteArrayInputStream(responseXml.getBytes());
    	Object map = responseXml2Map(is, elementKey);
    	try {
			is.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}
    	return map;    	
    }
    
    
    /**
     * 请求xml文档 elementKey 元素下的结构转map
     * @param responseXml
     * @param elementKey
     * @return
     */
    public static Object responseXml2Map(byte[] responseXml, String elementKey){
    	
    	if(elementKey == null){
    		return null;
    	}
    	ByteArrayInputStream is = new ByteArrayInputStream(responseXml);
    	Object map = responseXml2Map(is, elementKey);
    	try {
			is.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}
    	return map;
    }
    
    /**
     * 请求xml文档 elementKey 元素下的结构转map
     * @param responseXml
     * @param elementKey
     * @return
     */
    public static Object responseXml2Map(InputStream responseXml, String elementKey){
    	return responseXml2Map(responseXml).get(elementKey);
    }
    
	/**
     * 请求xml文档Header元素下的结构转map
     * @param responseXml
     * @return
     */
    public static Map<String, Object> responseXml2Map(InputStream responseXml){
    	
    	Map<String, Object> xmlMap = new HashMap<String, Object>();
    	
    	try{
    		
        	SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(responseXml);
            //logger.info("received xml ======= "+document.asXML());
            Element rootElement = document.getRootElement(); 
                       
            // 获取 Header 标签
            Element headerElement = rootElement.element("Header");            
            Map<String, Object> headerXmlMap = new HashMap<String, Object>();
            for (Iterator iter = headerElement.elements().iterator(); iter.hasNext(); ) {
                Element e = (Element) iter.next();
                if(!e.getName().equals("Response")){
                	headerXmlMap.put(e.getName(), e.getText());
                }                
            }
            
            //装载 Header 元素子节点信息
            xmlMap.put(headerElement.getName(), headerXmlMap);
            
            // 获取 Response 标签            
            Element responseElement = headerElement.element("Response");
            Map<String, Object> responseXmlMap = new HashMap<String, Object>();
            for (Iterator iter = responseElement.elements().iterator(); iter.hasNext(); ) {
                Element e = (Element) iter.next();
                responseXmlMap.put(e.getName(), e.getText());
            } 
            
            //装载 Header 下 Response 元素子节点信息
            xmlMap.put(responseElement.getName(), responseXmlMap);
            
            // 获取 Body 标签
            Element bodyElement = rootElement.element("Body"); 
            // 获取 Body 标签下的  Response 标签
            Element bodyResponseElement = bodyElement.element("Response"); 
            List<Map<String, Object>> tradeDataList = new ArrayList<Map<String, Object>>();
            
            String tradeDataListName = null;
            //循环数据列表元素节点
            for (Iterator iter = bodyResponseElement.elements().iterator(); iter.hasNext(); ) {
                Element e = (Element) iter.next();
                if(tradeDataListName == null){
                	tradeDataListName = e.getName();
                }
                //循环数据列表元素节点的子节点
                Map<String, Object> map = new HashMap<String, Object>();
                for (Iterator i = e.elements().iterator(); i.hasNext(); ) {
                    Element e1 = (Element) i.next();
                    map.put(e1.getName(), e1.getText());                   
                }
                tradeDataList.add(map);                
            }
            
            xmlMap.put(tradeDataListName, tradeDataList);
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}    	
    	
    	return xmlMap;
    }

}
