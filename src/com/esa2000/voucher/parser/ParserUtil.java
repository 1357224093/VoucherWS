package com.esa2000.voucher.parser;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.esa2000.voucher.entity.ReceiptInfo;

public class ParserUtil {

	/**
	 * map 转 xml
	 * 
	 * @param map
	 * @return
	 */
	public static Element map2Element(Map<String, Object> map) {

		Document document = DocumentHelper.createDocument();
		Element headerElement = document.addElement("Header");
		Set<String> sets = map.keySet();
		Iterator<String> iter = sets.iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			String value = map.get(key).toString();
			headerElement.addElement(key).setText(value);
			// System.out.println(key+":"+value);
		}
		// System.out.println(headerElement.element("ServiceCode").getText());
		return document.getRootElement();

	}

	public static Document buildDocument(Element headerElement,
			List<ReceiptInfo> receiptInfoList) {
		
		//创建 xml 文档对象
		Document document = DocumentHelper.createDocument();
		
		//添加根元素节点
		Element serviceElement = document.addElement("Service");
		
		//根元素节点下添加 Header 元素节点
		serviceElement.add(headerElement);
		
		//根元素节点下 添加 Body 元素节点
		Element bodyElement = serviceElement.addElement("Body");
		
		//Body 元素节点下添加 Response 元素节点
		Element bodyResponseElement = bodyElement.addElement("Response");

		if(receiptInfoList != null){
			for (ReceiptInfo r : receiptInfoList) {
				
				//Body->Response 元素节点下添加数据集元素节点
				Element tradeDataListElement = bodyResponseElement.addElement("TradeDataList");
				
				tradeDataListElement.addElement("Lid").setText(r.getId()==null? "":r.getId());
				tradeDataListElement.addElement("VoucherName").setText(r.getVoucherName()==null? "":r.getVoucherName());
				tradeDataListElement.addElement("VoucherID").setText(r.getVoucherId()==null? "":r.getVoucherId());
				tradeDataListElement.addElement("VersionName").setText(r.getTransEngName()==null? "":r.getTransEngName());
				tradeDataListElement.addElement("VersionChiName").setText(r.getTransName()==null? "":r.getTransName());
				tradeDataListElement.addElement("BusiSysName").setText(r.getSysName()==null? "":r.getSysName());
				tradeDataListElement.addElement("FileName").setText(r.getFileName()==null? "":r.getFileName());
				tradeDataListElement.addElement("UniqueCode").setText(r.getCode()==null? "":r.getCode());
				tradeDataListElement.addElement("SealType").setText(String.valueOf(r.getSealType()==null? "":r.getSealType()));
				tradeDataListElement.addElement("SealOperator").setText(r.getSealOpName()==null? "":r.getSealOpName());
				tradeDataListElement.addElement("SealOperType").setText(String.valueOf(r.getSealModel()==null? "":r.getSealModel()));
				tradeDataListElement.addElement("SealTime").setText(r.getSealTime()==null? "":DateUtil.getStringFromDate(r.getSealTime(), "yyyyMMddhhmmss"));
				tradeDataListElement.addElement("SealResult").setText(r.getSealResult()==null? "":r.getSealResult());
				tradeDataListElement.addElement("EcmBtchNo").setText(r.getEcmId()==null? "":r.getEcmId());
				tradeDataListElement.addElement("TxnReference").setText(r.getTrnid()==null? "":r.getTrnid());
				tradeDataListElement.addElement("BusinessBank").setText(r.getOrgname()==null? "":r.getOrgname());
				tradeDataListElement.addElement("AccountBank").setText(r.getAccopenbnk()==null? "":r.getAccopenbnk());
				tradeDataListElement.addElement("DistributeBank").setText(r.getVoucherbank()==null? "":r.getVoucherbank());
				tradeDataListElement.addElement("T24OperatorId").setText(r.getExttellerid()==null? "":r.getExttellerid());
				tradeDataListElement.addElement("CustomerId").setText(r.getClientid()==null? "":r.getClientid());
				tradeDataListElement.addElement("AccNo").setText(r.getCltacc()==null? "":r.getCltacc());
				tradeDataListElement.addElement("PayerAcc").setText(r.getPayacc()==null? "":r.getPayacc());
				tradeDataListElement.addElement("PayerName").setText(r.getPaynam()==null? "":r.getPaynam());
				tradeDataListElement.addElement("PayeeAccount").setText(r.getRcvacc()==null? "":r.getRcvacc());
				tradeDataListElement.addElement("PayeeName").setText(r.getRcvnam()==null? "":r.getRcvnam());
				tradeDataListElement.addElement("ContId").setText(r.getContractid()==null? "":r.getContractid());
				tradeDataListElement.addElement("Amount").setText(String.valueOf(r.getAmt()==null? "":r.getAmt()));//new DecimalFormat("#,###,###,###.##").format(r.getAmt());
				tradeDataListElement.addElement("IdNumber").setText(r.getCertid()==null? "":r.getCertid());
				tradeDataListElement.addElement("TranDate").setText(r.getTrndate()==null? "":DateUtil.getStringFromDate(r.getTrndate(), "yyyyMMdd"));
				tradeDataListElement.addElement("QueryTime").setText(String.valueOf(r.getSelnum()==null? "":r.getSelnum()));
				
			}
		}
		return document;
	}
	
	/**
	 * 根据 ReceiptInfo 单条记录 创建 xml 文档
	 * @param headerParamMap
	 * @param hre 头部响应实体对象 用来生成  Header 元素子元素Response元素节点
	 * @param r 数据信息
	 * @return
	 */
	public static Document buildDocument(Map<String, Object> headerParamMap, HeaderResponseEntity hre, List<ReceiptInfo> dataList) {
		
		// 把实体对象转成 xml 文档中元素节点
		Element headerResponseElement = ElementUtil.toXmlElement(hre);

		// 把元素节点重新命名为 Response
		headerResponseElement.setName("Response");

		// 根据 map 键值对生成 xml header 元素节点
		Element headerElement = ParserUtil.map2Element(headerParamMap);

		// 添加到 Header 元素节点下
		headerElement.add(headerResponseElement);
		
		//创建 xml 文档对象
		Document document = DocumentHelper.createDocument();
		
		//添加根元素节点
		Element serviceElement = document.addElement("Service");
		
		//根元素节点下添加 Header 元素节点
		serviceElement.add(headerElement);
		
		//根元素节点下 添加 Body 元素节点
		Element bodyElement = serviceElement.addElement("Body");
		
		//Body 元素节点下添加 Response 元素节点
		Element bodyResponseElement = bodyElement.addElement("Response");

		if(dataList != null){
			
			for(ReceiptInfo r:dataList){
			
				//Body->Response 元素节点下添加数据集元素节点
				Element tradeDataListElement = bodyResponseElement.addElement("TradeDataList");
				
				tradeDataListElement.addElement("Lid").setText(r.getId()==null? "":r.getId());
				tradeDataListElement.addElement("VoucherName").setText(r.getVoucherName()==null? "":r.getVoucherName());
				tradeDataListElement.addElement("VoucherID").setText(r.getVoucherId()==null? "":r.getVoucherId());
				tradeDataListElement.addElement("VersionName").setText(r.getTransEngName()==null? "":r.getTransEngName());
				tradeDataListElement.addElement("VersionChiName").setText(r.getTransName()==null? "":r.getTransName());
				tradeDataListElement.addElement("BusiSysName").setText(r.getSysName()==null? "":r.getSysName());
				tradeDataListElement.addElement("FileName").setText(r.getFileName()==null? "":r.getFileName());
				tradeDataListElement.addElement("UniqueCode").setText(r.getCode()==null? "":r.getCode());
				tradeDataListElement.addElement("SealType").setText(String.valueOf(r.getSealType()==null? "":r.getSealType()));
				tradeDataListElement.addElement("SealOperator").setText(r.getSealOpName()==null? "":r.getSealOpName());
				tradeDataListElement.addElement("SealOperType").setText(String.valueOf(r.getSealModel()==null? "":r.getSealModel()));
				tradeDataListElement.addElement("SealTime").setText(r.getSealTime()==null? "":DateUtil.getStringFromDate(r.getSealTime(), "yyyyMMddHHmmss"));
				tradeDataListElement.addElement("SealResult").setText(r.getSealResult()==null? "":r.getSealResult());
				tradeDataListElement.addElement("EcmBtchNo").setText(r.getEcmId()==null? "":r.getEcmId());
				tradeDataListElement.addElement("TxnReference").setText(r.getTrnid()==null? "":r.getTrnid());
				tradeDataListElement.addElement("BusinessBank").setText(r.getOrgname()==null? "":r.getOrgname());
				tradeDataListElement.addElement("AccountBank").setText(r.getAccopenbnk()==null? "":r.getAccopenbnk());
				tradeDataListElement.addElement("DistributeBank").setText(r.getVoucherbank()==null? "":r.getVoucherbank());
				tradeDataListElement.addElement("T24OperatorId").setText(r.getExttellerid()==null? "":r.getExttellerid());
				tradeDataListElement.addElement("CustomerId").setText(r.getClientid()==null? "":r.getClientid());
				tradeDataListElement.addElement("AccNo").setText(r.getCltacc()==null? "":r.getCltacc());
				tradeDataListElement.addElement("PayerAcc").setText(r.getPayacc()==null? "":r.getPayacc());
				tradeDataListElement.addElement("PayerName").setText(r.getPaynam()==null? "":r.getPaynam());
				tradeDataListElement.addElement("PayeeAccount").setText(r.getRcvacc()==null? "":r.getRcvacc());
				tradeDataListElement.addElement("PayeeName").setText(r.getRcvnam()==null? "":r.getRcvnam());
				tradeDataListElement.addElement("ContId").setText(r.getContractid()==null? "":r.getContractid());
				tradeDataListElement.addElement("Amount").setText(String.valueOf(r.getAmt()==null? "":r.getAmt()));
				tradeDataListElement.addElement("IdNumber").setText(r.getCertid()==null? "":r.getCertid());
				tradeDataListElement.addElement("TranDate").setText(r.getTrndate()==null? "":DateUtil.getStringFromDate(r.getTrndate(), "yyyyMMdd"));
				tradeDataListElement.addElement("QueryTime").setText(String.valueOf(r.getSelnum()==null? "":r.getSelnum()));
			}	
			
		}
		return document;
	}
	
	/**
	 * 根据 ReceiptInfo 单条记录 创建 xml 文档
	 * @param headerParamMap
	 * @param hre 头部响应实体对象 用来生成  Header 元素子元素Response元素节点
	 * @param r 数据信息
	 * @return
	 */
	public static Document buildDocument(Map<String, Object> headerParamMap, HeaderResponseEntity hre, List<ReceiptInfo> dataList, int recordCount) {
		
		// 把实体对象转成 xml 文档中元素节点
		Element headerResponseElement = ElementUtil.toXmlElement(hre);

		// 把元素节点重新命名为 Response
		headerResponseElement.setName("Response");

		// 根据 map 键值对生成 xml header 元素节点
		Element headerElement = ParserUtil.map2Element(headerParamMap);

		// 添加到 Header 元素节点下
		headerElement.add(headerResponseElement);
		
		//创建 xml 文档对象
		Document document = DocumentHelper.createDocument();
		
		//添加根元素节点
		Element serviceElement = document.addElement("Service");
		
		//根元素节点下添加 Header 元素节点
		serviceElement.add(headerElement);
		
		//根元素节点下 添加 Body 元素节点
		Element bodyElement = serviceElement.addElement("Body");
		
		//Body 元素节点下添加 Response 元素节点
		Element bodyResponseElement = bodyElement.addElement("Response");
		
		//Body->Response 元素节点下添加数据总记录数节点
		bodyResponseElement.addElement("RecordCount").setText(String.valueOf(recordCount));
		
		if(dataList != null){
			
			for(ReceiptInfo r:dataList){
			
				//Body->Response 元素节点下添加数据集元素节点
				Element tradeDataListElement = bodyResponseElement.addElement("TradeDataList");
				
				tradeDataListElement.addElement("Lid").setText(r.getId()==null? "":r.getId());
				tradeDataListElement.addElement("VoucherName").setText(r.getVoucherName()==null? "":r.getVoucherName());
				tradeDataListElement.addElement("VoucherID").setText(r.getVoucherId()==null? "":r.getVoucherId());
				tradeDataListElement.addElement("VersionName").setText(r.getTransEngName()==null? "":r.getTransEngName());
				tradeDataListElement.addElement("VersionChiName").setText(r.getTransName()==null? "":r.getTransName());
				tradeDataListElement.addElement("BusiSysName").setText(r.getSysName()==null? "":r.getSysName());
				tradeDataListElement.addElement("FileName").setText(r.getFileName()==null? "":r.getFileName());
				tradeDataListElement.addElement("UniqueCode").setText(r.getCode()==null? "":r.getCode());
				tradeDataListElement.addElement("SealType").setText(String.valueOf(r.getSealType()==null? "":r.getSealType()));
				tradeDataListElement.addElement("SealOperator").setText(r.getSealOpName()==null? "":r.getSealOpName());
				tradeDataListElement.addElement("SealOperType").setText(String.valueOf(r.getSealModel()==null? "":r.getSealModel()));
				tradeDataListElement.addElement("SealTime").setText(r.getSealTime()==null? "":DateUtil.getStringFromDate(r.getSealTime(), "yyyyMMddHHmmss"));
				tradeDataListElement.addElement("SealResult").setText(r.getSealResult()==null? "":r.getSealResult());
				tradeDataListElement.addElement("EcmBtchNo").setText(r.getEcmId()==null? "":r.getEcmId());
				tradeDataListElement.addElement("TxnReference").setText(r.getTrnid()==null? "":r.getTrnid());
				tradeDataListElement.addElement("BusinessBank").setText(r.getOrgname()==null? "":r.getOrgname());
				tradeDataListElement.addElement("AccountBank").setText(r.getAccopenbnk()==null? "":r.getAccopenbnk());
				tradeDataListElement.addElement("DistributeBank").setText(r.getVoucherbank()==null? "":r.getVoucherbank());
				tradeDataListElement.addElement("T24OperatorId").setText(r.getExttellerid()==null? "":r.getExttellerid());
				tradeDataListElement.addElement("CustomerId").setText(r.getClientid()==null? "":r.getClientid());
				tradeDataListElement.addElement("AccNo").setText(r.getCltacc()==null? "":r.getCltacc());
				tradeDataListElement.addElement("PayerAcc").setText(r.getPayacc()==null? "":r.getPayacc());
				tradeDataListElement.addElement("PayerName").setText(r.getPaynam()==null? "":r.getPaynam());
				tradeDataListElement.addElement("PayeeAccount").setText(r.getRcvacc()==null? "":r.getRcvacc());
				tradeDataListElement.addElement("PayeeName").setText(r.getRcvnam()==null? "":r.getRcvnam());
				tradeDataListElement.addElement("ContId").setText(r.getContractid()==null? "":r.getContractid());
				tradeDataListElement.addElement("Amount").setText(String.valueOf(r.getAmt()==null? "":r.getAmt()));
				tradeDataListElement.addElement("IdNumber").setText(r.getCertid()==null? "":r.getCertid());
				tradeDataListElement.addElement("TranDate").setText(r.getTrndate()==null? "":DateUtil.getStringFromDate(r.getTrndate(), "yyyyMMdd"));
				tradeDataListElement.addElement("QueryTime").setText(String.valueOf(r.getSelnum()==null? "":r.getSelnum()));
			}	
			
		}
		return document;
	}
	
	/**
	 * 根据 ReceiptInfo 单条记录 创建 xml 文档
	 * @param headerParamMap
	 * @param hre 头部响应实体对象 用来生成  Header 元素子元素Response元素节点
	 * @param r 数据信息
	 * @return
	 */
	public static Document buildDocument(Map<String, Object> headerParamMap, HeaderResponseEntity hre, ReceiptInfo r) {
		
		// 把实体对象转成 xml 文档中元素节点
		Element headerResponseElement = ElementUtil.toXmlElement(hre);

		// 把元素节点重新命名为 Response
		headerResponseElement.setName("Response");

		// 根据 map 键值对生成 xml header 元素节点
		Element headerElement = ParserUtil.map2Element(headerParamMap);

		// 添加到 Header 元素节点下
		headerElement.add(headerResponseElement);
		
		//创建 xml 文档对象
		Document document = DocumentHelper.createDocument();
		
		//添加根元素节点
		Element serviceElement = document.addElement("Service");
		
		//根元素节点下添加 Header 元素节点
		serviceElement.add(headerElement);
		
		//根元素节点下 添加 Body 元素节点
		Element bodyElement = serviceElement.addElement("Body");
		
		//Body 元素节点下添加 Response 元素节点
		Element bodyResponseElement = bodyElement.addElement("Response");

		if(r != null){
			
			//Body->Response 元素节点下添加数据集元素节点
			Element tradeDataListElement = bodyResponseElement.addElement("TradeDataList");
			
			tradeDataListElement.addElement("Lid").setText(r.getId()==null? "":r.getId());
			tradeDataListElement.addElement("VoucherName").setText(r.getVoucherName()==null? "":r.getVoucherName());
			tradeDataListElement.addElement("VoucherID").setText(r.getVoucherId()==null? "":r.getVoucherId());
			tradeDataListElement.addElement("VersionName").setText(r.getTransEngName()==null? "":r.getTransEngName());
			tradeDataListElement.addElement("VersionChiName").setText(r.getTransName()==null? "":r.getTransName());
			tradeDataListElement.addElement("BusiSysName").setText(r.getSysName()==null? "":r.getSysName());
			tradeDataListElement.addElement("FileName").setText(r.getFileName()==null? "":r.getFileName());
			tradeDataListElement.addElement("UniqueCode").setText(r.getCode()==null? "":r.getCode());
			tradeDataListElement.addElement("SealType").setText(String.valueOf(r.getSealType()==null? "":r.getSealType()));
			tradeDataListElement.addElement("SealOperator").setText(r.getSealOpName()==null? "":r.getSealOpName());
			tradeDataListElement.addElement("SealOperType").setText(String.valueOf(r.getSealModel()==null? "":r.getSealModel()));
			tradeDataListElement.addElement("SealTime").setText(r.getSealTime()==null? "":DateUtil.getStringFromDate(r.getSealTime(), "yyyyMMddHHmmss"));
			tradeDataListElement.addElement("SealResult").setText(r.getSealResult()==null? "":r.getSealResult());
			tradeDataListElement.addElement("EcmBtchNo").setText(r.getEcmId()==null? "":r.getEcmId());
			tradeDataListElement.addElement("TxnReference").setText(r.getTrnid()==null? "":r.getTrnid());
			tradeDataListElement.addElement("BusinessBank").setText(r.getOrgname()==null? "":r.getOrgname());
			tradeDataListElement.addElement("AccountBank").setText(r.getAccopenbnk()==null? "":r.getAccopenbnk());
			tradeDataListElement.addElement("DistributeBank").setText(r.getVoucherbank()==null? "":r.getVoucherbank());
			tradeDataListElement.addElement("T24OperatorId").setText(r.getExttellerid()==null? "":r.getExttellerid());
			tradeDataListElement.addElement("CustomerId").setText(r.getClientid()==null? "":r.getClientid());
			tradeDataListElement.addElement("AccNo").setText(r.getCltacc()==null? "":r.getCltacc());
			tradeDataListElement.addElement("PayerAcc").setText(r.getPayacc()==null? "":r.getPayacc());
			tradeDataListElement.addElement("PayerName").setText(r.getPaynam()==null? "":r.getPaynam());
			tradeDataListElement.addElement("PayeeAccount").setText(r.getRcvacc()==null? "":r.getRcvacc());
			tradeDataListElement.addElement("PayeeName").setText(r.getRcvnam()==null? "":r.getRcvnam());
			tradeDataListElement.addElement("ContId").setText(r.getContractid()==null? "":r.getContractid());
			tradeDataListElement.addElement("Amount").setText(String.valueOf(r.getAmt()==null? "":r.getAmt()));
			tradeDataListElement.addElement("IdNumber").setText(r.getCertid()==null? "":r.getCertid());
			tradeDataListElement.addElement("TranDate").setText(r.getTrndate()==null? "":DateUtil.getStringFromDate(r.getTrndate(), "yyyyMMdd"));
			tradeDataListElement.addElement("QueryTime").setText(String.valueOf(r.getSelnum()==null? "":r.getSelnum()));
			
			
		}
		return document;
	}
}
