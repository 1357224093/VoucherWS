package com.esa2000.voucher;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hibernate.Session;
import org.junit.Test;

import com.esa2000.apcore.unit.CommonUtil;
import com.esa2000.voucher.control.ReceiptControl;
import com.esa2000.voucher.database.HibernateSessionFactory;
import com.esa2000.voucher.entity.ReceiptInfoBak;
import com.esa2000.voucher.parser.RequestXmlParser;
import com.esa2000.voucher.task.ReceiptInfoTask;

/**
 * Created by 李昌文 on 2015/12/31.
 */
public class ServiceImplTest {

    public static String readOperateResult(String xml) {
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new ByteArrayInputStream(xml.getBytes()));
            Element root = document.getRootElement();
            Element msgElement = root.element("MESSAGE");
            String code = msgElement.elementText("MSG_CODE");
            String desc = msgElement.elementText("MSG_DESC");

            System.out.println(code);
            System.out.println(desc);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    @Test
    public void testQueryReceiptInfo_XML_IS_NULL() {
        ServiceImpl service = new ServiceImpl();
        String msg = service.queryReceiptInfo("");
        System.out.println(msg);

        readOperateResult(msg);
    }
    
    @Test
    public void testQueryReceiptInfoServlet() {
    	Session session = HibernateSessionFactory.getSession();
		ReceiptControl control = new ReceiptControl(session);
		
		ReceiptInfoBak r= control.getReceiptBakById("8a8aaf8e53270c050153270ec095013d");
		
        System.out.println(r.getSealTime());

    }
    
    //hehui test
    @Test
    public void testGetTest() {
    	
    	String requestXml = new String(CommonUtil.transInputstreamToBytes(ServiceImplTest.class.getResourceAsStream("QueryReceiptInfoRequest.xml")));
    	
    	Map<String, Object> paramMap = null;
    	Map<String, Object> requestParamMap = null;
    	try {
			requestXml = new String(requestXml.getBytes(), "UTF-8");
			System.out.println("输入：" + requestXml);
			
			paramMap = RequestXmlParser.requestXml2Map(requestXml);
			requestParamMap = (Map<String, Object>) paramMap.get("Request");
		} catch (Exception e1) {			
			e1.printStackTrace();
		}
    	
    	Session session = HibernateSessionFactory.getSession();
		ReceiptControl control = new ReceiptControl(session);
		
		//control.getTest(requestParamMap);
		if(session !=null){
			session.close();
		}
		
    }

    @Test
    public void testQueryReceiptInfo() {
        // 从QueryReceiptInfo.xml中读取查询条件
        String requestXml = new String(CommonUtil.transInputstreamToBytes(ServiceImplTest.class.getResourceAsStream("QueryReceiptInfoRequest.xml")));
        //System.out.println(requestXml);

        ServiceImpl service = new ServiceImpl();
        String msg = service.queryReceiptInfo(requestXml);
        System.out.println(msg);
        //readOperateResult(msg);
    }
    
    @Test
    public void moveShitoryRecordTest() {

    	ReceiptInfoTask rTask = new ReceiptInfoTask();
    	
    	rTask.moveShitoryRecord();
    	
    }
    
}
