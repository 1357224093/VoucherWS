package com.esa2000.voucher.parser;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.esa2000.voucher.util.JAXbUtil;

public class ElementUtil {

	public static Element toXmlElement(Object o){
    	String xml = JAXbUtil.convertToXml(o);    	
    	Document doc = null;
    	try {
			doc = DocumentHelper.parseText(xml);
		} catch (DocumentException e) {			
			e.printStackTrace();
		}
    	return doc.getRootElement();
    }
}
