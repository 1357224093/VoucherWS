package com.esa2000.voucher;


import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;

import com.esa2000.apcore.unit.CommonUtil;

public class WSTest {

	public static final String WS_URL = "http://localhost:8888/VoucherWS/services/VoucherWS?wsdl";
    public static final String WS_NAMESPACE_URI = "http://voucher.esa2000.com";

    public static void main(String args[]) throws AxisFault, FileNotFoundException, UnsupportedEncodingException {
        //String requestXml = "022F8B44F4C3256A";
    	// 从QueryReceiptInfo.xml中读取查询条件    	
    	//new InputStreamReader(ServiceImplTest.class.getResourceAsStream("QueryReceiptInfoRequest.xml"), "UTF-8");
        
        String requestXml = new String(CommonUtil.transInputstreamToBytes(WSTest.class.getResourceAsStream("QueryReceiptInfoRequest.xml")), "UTF-8");
        String responseXml = (String) queryReceiptInfo(requestXml);
        //System.out.println(requestXml);
        System.out.println(responseXml);
    }
    
    /**
     * 根据输入项查询回单信息
     *
     * @param input 输入项
     * @return
     * @throws AxisFault
     */
    public static Object queryReceiptInfo(String input) throws AxisFault {
        //  使用RPC方式调用WebService
        RPCServiceClient serviceClient = new RPCServiceClient();
        Options options = serviceClient.getOptions();
        //  指定调用WebService的URL
        EndpointReference targetEPR = new EndpointReference(WS_URL);
        options.setTo(targetEPR);
        //  指定sayHelloToPerson方法的参数值
        Object[] opAddEntryArgs = new Object[]{input};
        //  指定sayHelloToPerson方法返回值的数据类型的Class对象
        Class[] classes = new Class[]{String.class};
        //  指定要调用的sayHelloToPerson方法及WSDL文件的命名空间
        QName opAddEntry = new QName(WS_NAMESPACE_URI, "queryReceiptInfo");
        //  调用sayHelloToPerson方法并输出该方法的返回值
        return serviceClient.invokeBlocking(opAddEntry, opAddEntryArgs, classes)[0];
    }
	
}
