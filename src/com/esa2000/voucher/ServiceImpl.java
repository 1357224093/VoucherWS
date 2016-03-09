package com.esa2000.voucher;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.esa2000.apcore.unit.CommonUtil;
import com.esa2000.voucher.bll.Business;
import com.esa2000.voucher.conf.Log4jLoader;
import com.esa2000.voucher.control.ReceiptControl;
import com.esa2000.voucher.database.HibernateSessionFactory;
import com.esa2000.voucher.entity.ReceiptInfo;
import com.esa2000.voucher.parser.CompareUtil;
import com.esa2000.voucher.parser.DateUtil;
import com.esa2000.voucher.parser.HeaderResponseEntity;
import com.esa2000.voucher.parser.RequestXmlParser;
import com.esa2000.voucher.util.ResponseMsgBuilder;
import com.esa2000.xmbank.util.AztAESUtil;

public class ServiceImpl {
	private static Logger logger = Logger.getLogger(ServiceImpl.class);

	static {
		Log4jLoader.loadLog4j();
	}

	/**
	 * 此方法调用原生SQL查询方法实现数据查询
	 * @param requestXml
	 * @return
	 */
	public String queryReceiptInfo(String requestXml) {
				
		// 1. 接收数据并解析XML
		logger.info("输入：" + requestXml);
		
		//转换成指定编码字符串
		try {
			requestXml = new String(requestXml.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e1) {			
			e1.printStackTrace();
		}
		
		if (!CommonUtil.isNotNull(requestXml)) {			
			return ResponseMsgBuilder.buildMSG("001", "输出的数据不能为空", null);
		}

		// 2. 解析XML
		Map<String, Object> paramMap = null;
		Map<String, Object> headerParamMap = null;
		Map<String, Object> requestParamMap = null;

		try {
			// paramMap = RequestXMLParser.parse(requestXml.getBytes());
			paramMap = RequestXmlParser.requestXml2Map(requestXml);
			headerParamMap = (Map<String, Object>) paramMap.get("Header");
			requestParamMap = (Map<String, Object>) paramMap.get("Request");

		} catch (Exception e) {			
			logger.error(e.getMessage(), e);			
			return ResponseMsgBuilder.buildMSG("002", "解析输入值失败", null);
		}
		if (paramMap == null) {			
			return ResponseMsgBuilder.buildMSG("002", "解析输入值失败", null);
		}

		String msg = null;
		Session session = null;
		try {
			
			//参数合法性校验
			//开始交易时间
			if(requestParamMap.get("BeginDate") !=null && requestParamMap.get("BeginDate").toString().length()>0){
				if(requestParamMap.get("BeginDate").toString().length()!=8){
					return ResponseMsgBuilder.buildMSG("003", "BeginDate value invalid, Length can only be 8. ", null);
				}
				try{
					int temp = Integer.parseInt(requestParamMap.get("BeginDate").toString());
				}catch(Exception e){
					return ResponseMsgBuilder.buildMSG("003", "BeginDate value invalid, (eg:20160101). ", null);
				}
				
				//把日期参数格式化
				StringBuffer newStr = new StringBuffer(requestParamMap.get("BeginDate").toString());
				String str = newStr.insert(4, "-").insert(7, "-").toString();
				requestParamMap.put("BeginDate", str);
				
			}
			
			//截止交易时间
			if(requestParamMap.get("EndDate") !=null && requestParamMap.get("EndDate").toString().length()>0){
				if(requestParamMap.get("EndDate").toString().length()!=8){
					return ResponseMsgBuilder.buildMSG("003", "EndDate value invalid, Length can only be 8. ", null);
				}
				try{
					int temp = Integer.parseInt(requestParamMap.get("EndDate").toString());
				}catch(Exception e){
					return ResponseMsgBuilder.buildMSG("003", "EndDate value invalid, (eg:20160101). ", null);
				}
				
				//把日期参数格式化
				StringBuffer newStr = new StringBuffer(requestParamMap.get("EndDate").toString());
				String str = newStr.insert(4, "-").insert(7, "-").toString();
				requestParamMap.put("EndDate", str);
				
			}
			
			//盖章类型
			if(requestParamMap.get("SealType") !=null && requestParamMap.get("SealType").toString().length()>0){
				try{
					int temp = Integer.parseInt(requestParamMap.get("SealType").toString());
				}catch(Exception e){
					return ResponseMsgBuilder.buildMSG("003", "SealType value invalid. ", null);
				}				
			}
			
			//金额下限
			if(requestParamMap.get("AmountMin") !=null && requestParamMap.get("AmountMin").toString().length()>0){
				try{
					Double temp = Double.parseDouble(requestParamMap.get("AmountMin").toString());
				}catch(Exception e){
					return ResponseMsgBuilder.buildMSG("003", "AmountMin value invalid. ", null);
				}				
			}
			
			//金额上限
			if(requestParamMap.get("AmountMax") !=null && requestParamMap.get("AmountMax").toString().length()>0){
				try{
					Double temp = Double.parseDouble(requestParamMap.get("AmountMax").toString());
				}catch(Exception e){
					return ResponseMsgBuilder.buildMSG("003", "AmountMax value invalid. ", null);
				}				
			}
			
			//是否加密
			//code 值是否经过加密
			String IsDecryptCode = "N";
			if(requestParamMap.get("IsDecryptCode") !=null && requestParamMap.get("IsDecryptCode").toString().length()>0){
				IsDecryptCode = requestParamMap.get("IsDecryptCode").toString();
				if(IsDecryptCode.length()>1){
					return ResponseMsgBuilder.buildMSG("003", "IsDecryptCode value invalid, Length can only be 1. ", null);
				}
				if(!"N".equals(IsDecryptCode) && !"Y".equals(IsDecryptCode)){
					return ResponseMsgBuilder.buildMSG("003", "IsDecryptCode value invalid, Can only be 'N' or 'Y'. ", null);
				}
				
			}
			
			//是否查历史表
			if(requestParamMap.get("IsHistory") !=null && requestParamMap.get("IsHistory").toString().length()>0){
				if(requestParamMap.get("IsHistory").toString().length()>1){
					return ResponseMsgBuilder.buildMSG("003", "IsHistory value invalid, Length can only be 1. ", null);
				}
				String temp = requestParamMap.get("IsHistory").toString();
				if(!"N".equals(temp) && !"Y".equals(temp)){
					return ResponseMsgBuilder.buildMSG("003", "IsHistory value invalid, Can only be 'N' or 'Y'. ", null);
				}				
			}
			
			//重复数据按盖章时间排序值
			if(requestParamMap.get("RepetiveDataOrdinalType") !=null && requestParamMap.get("RepetiveDataOrdinalType").toString().length()>0){
				try{
					if(requestParamMap.get("RepetiveDataOrdinalType").toString().length()>1){
						return ResponseMsgBuilder.buildMSG("003", "RepetiveDataOrdinalType value invalid, Length can only be 1. ", null);
					}
					int temp = Integer.parseInt(requestParamMap.get("RepetiveDataOrdinalType").toString());
				}catch(Exception e){
					return ResponseMsgBuilder.buildMSG("003", "RepetiveDataOrdinalType value invalid, Can only be 1 or 0. ", null);
				}				
			}
			
			//返回数据按盖章时间排序值
			if(requestParamMap.get("ReturnDataOrdinalType") !=null && requestParamMap.get("ReturnDataOrdinalType").toString().length()>0){
				try{
					if(requestParamMap.get("ReturnDataOrdinalType").toString().length()>1){
						return ResponseMsgBuilder.buildMSG("003", "ReturnDataOrdinalType value invalid, Length can only be 1. ", null);
					}
					int temp = Integer.parseInt(requestParamMap.get("ReturnDataOrdinalType").toString());
				}catch(Exception e){
					return ResponseMsgBuilder.buildMSG("003", "ReturnDataOrdinalType value invalid, Can only be 1 or 0. ", null);
				}				
			}
			
			//请求记录条数(也是每页查询量)
			if(requestParamMap.get("ReqListNum") !=null && requestParamMap.get("ReqListNum").toString().length()>0){
				try{
					if(requestParamMap.get("ReqListNum").toString().length()>5){
						return ResponseMsgBuilder.buildMSG("003", "ReqListNum value invalid, Length can only be 5. ", null);
					}
					int temp = Integer.parseInt(requestParamMap.get("ReqListNum").toString());
				}catch(Exception e){
					return ResponseMsgBuilder.buildMSG("003", "ReqListNum value invalid. ", null);
				}				
			}
			
			//请求数据页码
			if(requestParamMap.get("PageNo10") !=null && requestParamMap.get("PageNo10").toString().length()>0){
				try{
					if(requestParamMap.get("PageNo10").toString().length()>5){
						return ResponseMsgBuilder.buildMSG("003", "PageNo10 value invalid, Length can only be 5. ", null);
					}
					int temp = Integer.parseInt(requestParamMap.get("PageNo10").toString());
				}catch(Exception e){
					return ResponseMsgBuilder.buildMSG("003", "PageNo10 value invalid. ", null);
				}				
			}
					
			
			// 是否去除重复值
			if(requestParamMap.get("IsRepetive") !=null && requestParamMap.get("IsRepetive").toString().length()>0){
				if(requestParamMap.get("IsRepetive").toString().length()>1){
					return ResponseMsgBuilder.buildMSG("003", "IsRepetive value invalid, Length can only be 1. ", null);
				}
				String temp = requestParamMap.get("IsRepetive").toString();
				if(!"N".equals(temp) && !"Y".equals(temp)){
					return ResponseMsgBuilder.buildMSG("003", "IsRepetive value invalid, Can only be 'N' or 'Y'. ", null);
				}			
			}
			
			//code 如果经过加密, 则进行解密
			if("Y".equals(IsDecryptCode)){
				String code = requestParamMap.get("UniqueCode").toString();
				code = AztAESUtil.aztDecrypt(code);
				requestParamMap.put("UniqueCode", code);
			}
			
			session = HibernateSessionFactory.getSession();
			ReceiptControl control = new ReceiptControl(session);
			List<ReceiptInfo> dataList = control.getReceiptBySQL(requestParamMap);
			int recordCount = control.getReceiptCount(requestParamMap);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			
			// 创建 xml 文档中Header子元素节点Response信息对象
			HeaderResponseEntity hre = new HeaderResponseEntity("000000000", "交易成功",
					sdf.format(new Date()), "C57", sdf.format(new Date()));

			msg = Business.xmlDoc2String(headerParamMap, hre, dataList, recordCount);
			
			// msg = ResponseMsgBuilder.buildMSG("000", "成功",
			// receiptInfo.toXmlElement());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		return msg.replace("\n", "");
	}
	
	public String queryReceiptInfo1(String requestXml) {
		
		// 1. 接收数据并解析XML
		logger.info("输入：" + requestXml);
		
		//转换成指定编码字符串
		try {
			requestXml = new String(requestXml.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e1) {			
			e1.printStackTrace();
		}
		
		if (!CommonUtil.isNotNull(requestXml)) {			
			return ResponseMsgBuilder.buildMSG("001", "输出的数据不能为空", null);
		}

		// 2. 解析XML
		Map<String, Object> paramMap = null;
		Map<String, Object> headerParamMap = null;
		Map<String, Object> requestParamMap = null;

		try {
			// paramMap = RequestXMLParser.parse(requestXml.getBytes());
			paramMap = RequestXmlParser.requestXml2Map(requestXml);
			headerParamMap = (Map<String, Object>) paramMap.get("Header");
			requestParamMap = (Map<String, Object>) paramMap.get("Request");

		} catch (Exception e) {			
			logger.error(e.getMessage(), e);			
			return ResponseMsgBuilder.buildMSG("002", "解析输入值失败", null);
		}
		if (paramMap == null) {			
			return ResponseMsgBuilder.buildMSG("002", "解析输入值失败", null);
		}

		String msg = null;
		Session session = null;
		try {
			
			//参数合法性校验
			//开始交易时间
			if(requestParamMap.get("BeginDate") !=null && requestParamMap.get("BeginDate").toString().length()>0){
				if(requestParamMap.get("BeginDate").toString().length()!=8){
					return ResponseMsgBuilder.buildMSG("003", "BeginDate value invalid, Length can only be 8. ", null);
				}
				try{
					int temp = Integer.parseInt(requestParamMap.get("BeginDate").toString());
				}catch(Exception e){
					return ResponseMsgBuilder.buildMSG("003", "BeginDate value invalid, (eg:20160101). ", null);
				}
				
				//把日期参数格式化
				StringBuffer newStr = new StringBuffer(requestParamMap.get("BeginDate").toString());
				String str = newStr.insert(4, "-").insert(7, "-").toString();
				requestParamMap.put("BeginDate", str);
				
			}
			
			//截止交易时间
			if(requestParamMap.get("EndDate") !=null && requestParamMap.get("EndDate").toString().length()>0){
				if(requestParamMap.get("EndDate").toString().length()!=8){
					return ResponseMsgBuilder.buildMSG("003", "EndDate value invalid, Length can only be 8. ", null);
				}
				try{
					int temp = Integer.parseInt(requestParamMap.get("EndDate").toString());
				}catch(Exception e){
					return ResponseMsgBuilder.buildMSG("003", "EndDate value invalid, (eg:20160101). ", null);
				}
				
				//把日期参数格式化
				StringBuffer newStr = new StringBuffer(requestParamMap.get("EndDate").toString());
				String str = newStr.insert(4, "-").insert(7, "-").toString();
				requestParamMap.put("EndDate", str);
				
			}
			
			//盖章类型
			if(requestParamMap.get("SealType") !=null && requestParamMap.get("SealType").toString().length()>0){
				try{
					int temp = Integer.parseInt(requestParamMap.get("SealType").toString());
				}catch(Exception e){
					return ResponseMsgBuilder.buildMSG("003", "SealType value invalid. ", null);
				}				
			}
			
			//金额下限
			if(requestParamMap.get("AmountMin") !=null && requestParamMap.get("AmountMin").toString().length()>0){
				try{
					Double temp = Double.parseDouble(requestParamMap.get("AmountMin").toString());
				}catch(Exception e){
					return ResponseMsgBuilder.buildMSG("003", "AmountMin value invalid. ", null);
				}				
			}
			
			//金额上限
			if(requestParamMap.get("AmountMax") !=null && requestParamMap.get("AmountMax").toString().length()>0){
				try{
					Double temp = Double.parseDouble(requestParamMap.get("AmountMax").toString());
				}catch(Exception e){
					return ResponseMsgBuilder.buildMSG("003", "AmountMax value invalid. ", null);
				}				
			}
			
			//是否加密
			//code 值是否经过加密
			String IsDecryptCode = "N";
			if(requestParamMap.get("IsDecryptCode") !=null && requestParamMap.get("IsDecryptCode").toString().length()>0){
				IsDecryptCode = requestParamMap.get("IsDecryptCode").toString();
				if(IsDecryptCode.length()>1){
					return ResponseMsgBuilder.buildMSG("003", "IsDecryptCode value invalid, Length can only be 1. ", null);
				}
				if(!"N".equals(IsDecryptCode) && !"Y".equals(IsDecryptCode)){
					return ResponseMsgBuilder.buildMSG("003", "IsDecryptCode value invalid, Can only be 'N' or 'Y'. ", null);
				}
				
			}
			
			//是否查历史表
			if(requestParamMap.get("IsHistory") !=null && requestParamMap.get("IsHistory").toString().length()>0){
				if(requestParamMap.get("IsHistory").toString().length()>1){
					return ResponseMsgBuilder.buildMSG("003", "IsHistory value invalid, Length can only be 1. ", null);
				}
				String temp = requestParamMap.get("IsHistory").toString();
				if(!"N".equals(temp) && !"Y".equals(temp)){
					return ResponseMsgBuilder.buildMSG("003", "IsHistory value invalid, Can only be 'N' or 'Y'. ", null);
				}				
			}
			
			//重复数据按盖章时间排序值
			if(requestParamMap.get("RepetiveDataOrdinalType") !=null && requestParamMap.get("RepetiveDataOrdinalType").toString().length()>0){
				try{
					if(requestParamMap.get("RepetiveDataOrdinalType").toString().length()>1){
						return ResponseMsgBuilder.buildMSG("003", "RepetiveDataOrdinalType value invalid, Length can only be 1. ", null);
					}
					int temp = Integer.parseInt(requestParamMap.get("RepetiveDataOrdinalType").toString());
				}catch(Exception e){
					return ResponseMsgBuilder.buildMSG("003", "RepetiveDataOrdinalType value invalid, Can only be 1 or 0. ", null);
				}				
			}
			
			//返回数据按盖章时间排序值
			if(requestParamMap.get("ReturnDataOrdinalType") !=null && requestParamMap.get("ReturnDataOrdinalType").toString().length()>0){
				try{
					if(requestParamMap.get("ReturnDataOrdinalType").toString().length()>1){
						return ResponseMsgBuilder.buildMSG("003", "ReturnDataOrdinalType value invalid, Length can only be 1. ", null);
					}
					int temp = Integer.parseInt(requestParamMap.get("ReturnDataOrdinalType").toString());
				}catch(Exception e){
					return ResponseMsgBuilder.buildMSG("003", "ReturnDataOrdinalType value invalid, Can only be 1 or 0. ", null);
				}				
			}
			
			//请求记录条数(也是每页查询量)
			if(requestParamMap.get("ReqListNum") !=null && requestParamMap.get("ReqListNum").toString().length()>0){
				try{
					if(requestParamMap.get("ReqListNum").toString().length()>5){
						return ResponseMsgBuilder.buildMSG("003", "ReqListNum value invalid, Length can only be 5. ", null);
					}
					int temp = Integer.parseInt(requestParamMap.get("ReqListNum").toString());
				}catch(Exception e){
					return ResponseMsgBuilder.buildMSG("003", "ReqListNum value invalid. ", null);
				}				
			}
			
			//请求数据页码
			if(requestParamMap.get("PageNo10") !=null && requestParamMap.get("PageNo10").toString().length()>0){
				try{
					if(requestParamMap.get("PageNo10").toString().length()>5){
						return ResponseMsgBuilder.buildMSG("003", "PageNo10 value invalid, Length can only be 5. ", null);
					}
					int temp = Integer.parseInt(requestParamMap.get("PageNo10").toString());
				}catch(Exception e){
					return ResponseMsgBuilder.buildMSG("003", "PageNo10 value invalid. ", null);
				}				
			}
					
			
			// 是否去除重复值
			if(requestParamMap.get("IsRepetive") !=null && requestParamMap.get("IsRepetive").toString().length()>0){
				if(requestParamMap.get("IsRepetive").toString().length()>1){
					return ResponseMsgBuilder.buildMSG("003", "IsRepetive value invalid, Length can only be 1. ", null);
				}
				String temp = requestParamMap.get("IsRepetive").toString();
				if(!"N".equals(temp) && !"Y".equals(temp)){
					return ResponseMsgBuilder.buildMSG("003", "IsRepetive value invalid, Can only be 'N' or 'Y'. ", null);
				}			
			}
			
			//code 如果经过加密, 则进行解密
			if("Y".equals(IsDecryptCode)){
				String code = requestParamMap.get("UniqueCode").toString();
				code = AztAESUtil.aztDecrypt(code);
				requestParamMap.put("UniqueCode", code);
			}
			
			session = HibernateSessionFactory.getSession();
			ReceiptControl control = new ReceiptControl(session);
			List<ReceiptInfo> dataList = control.getReceiptByCode(requestParamMap);
			int recordCount = control.getReceiptCount(requestParamMap);
			
			// 去除重复code相同的数据
			if(requestParamMap.get("IsRepetive") !=null && requestParamMap.get("IsRepetive").toString().length()>0){
				String temp = requestParamMap.get("IsRepetive").toString();
				if("N".equals(temp)){
					if(dataList.size()>0){
						String codeA = dataList.get(0).getCode();
						for(int i=1; i<dataList.size(); i++){
							String codeB = dataList.get(i).getCode();
							if(codeA.equals(codeB)){
								dataList.remove(i);
							}else{
								codeA = codeB;
							}
						}
					}
				}
			}
			
			/**
			 * 符合查询条件的数据库总记录数
			System.out.println("recordCount:"+recordCount);
			*/
			if (dataList == null) {
				return ResponseMsgBuilder.buildMSG("001", "Code对应的数据不存在", null);
			}

			/**
			System.out.println("------------------从数据库查询出来的数据------------------");
			for(int i=0; i<dataList.size(); i++){
				System.out.println(dataList.get(i).getCode()+"--"+dataList.get(i).getSealTime());
			}
			System.out.println("----------------end--------------------");
			**/
			
			//最新经排序后的返回数据
			List<ReceiptInfo> newDataList = new ArrayList<ReceiptInfo>();
			
			//用来存放区块数据
			List<List<ReceiptInfo>> alist = new ArrayList<List<ReceiptInfo>>();
			
			//返回数据按盖章时间排序
			if (requestParamMap.get("ReturnDataOrdinalType") != null
					&& requestParamMap.get("ReturnDataOrdinalType").toString().length() > 0
					&& "1".equals(requestParamMap.get("ReturnDataOrdinalType").toString())) {
				
				// 按盖章时间逆序排				
				for(int i=0; i<dataList.size();){		
										
					if (requestParamMap.get("RepetiveDataOrdinalType") != null
							&& requestParamMap.get("RepetiveDataOrdinalType").toString().length() > 0
							&& "1".equals(requestParamMap.get("RepetiveDataOrdinalType").toString())) {
						
						//取最大对象的索引
						int maxIndex = CompareUtil.getMaxIndexBySealTime(dataList);
						ReceiptInfo newData = dataList.get(maxIndex);
						
						//获取最大对象的编号
						String code = newData.getCode();
						
						newDataList.add(newData);
						dataList.remove(maxIndex);
						
						//继续查找同编码的记录,组成为一个区块
						for(int j=maxIndex; j<dataList.size();){						
							ReceiptInfo tempData = dataList.get(j);							
							if(tempData.getCode().equals(code)){
								newDataList.add(tempData);
								dataList.remove(j);								
							}else{
								//若要找的记录不是同一组的记录, 则退出
								break;
							}
						}
					} else {
						
						/**
						 * 如果重复数据按升序排序,而返回数据按降序排序,则以下解决方案的策略是：
						 * 排序规则为：如果有区块数据，则按区块组内的第一条数据进行区块间或者其它单一数据进行排序
						 * 解决办法是：取最小的数据，进而取最小数据的同一区间数据组成一个区块
						 */
						
						//取最小对象的索引
						int minIndex = CompareUtil.getMinIndexBySealTime(dataList);
						ReceiptInfo newData = dataList.get(minIndex);
						
						//获取最大对象的编号
						String code = newData.getCode();
						
						// 用来临时存放被查找出来的同一组数据
						List<ReceiptInfo> tempList = new ArrayList<ReceiptInfo>();
						tempList.add(newData);
						dataList.remove(minIndex);
						
						//继续查找同编码的记录,组成为一个区块
						for(int j=minIndex; j<dataList.size();){
							ReceiptInfo tempData = dataList.get(j);
							if(tempData.getCode().equals(code)){
								tempList.add(tempData);
								dataList.remove(j);								
							}else{
								//若要找的记录不是同一组的记录, 则退出
								break;
							}
						}
						
						alist.add(tempList);
					}
				}
				
				//如果重复数据按升序排序,而返回数据按降序排序,则以下为解决方案
				if (requestParamMap.get("RepetiveDataOrdinalType") != null
						&& requestParamMap.get("RepetiveDataOrdinalType").toString().length() > 0
						&& "0".equals(requestParamMap.get("RepetiveDataOrdinalType").toString())) {
					
					//以区块组的形式进行倒序
					for(int j=alist.size()-1; j>=0; j--){
						
						// 区块组内的数据记录保持原有的排序
						for(int m=0; m<alist.get(j).size(); m++){							
							newDataList.add(alist.get(j).get(m));							
						}						
					}
				}
			} else {
				
				// 按盖章时间升序排						
				for(int i=0; i<dataList.size();){
										
					//如果重复的数据已经过逆序排序的解决方案
					if (requestParamMap.get("RepetiveDataOrdinalType") != null
							&& requestParamMap.get("RepetiveDataOrdinalType").toString().length() > 0
							&& "1".equals(requestParamMap.get("RepetiveDataOrdinalType").toString())) {
						
						/**
						 * 如果重复数据按降序排序,而返回数据按升序排序,则以下解决方案的策略是：
						 * 排序规则为：如果有区块数据，则按区块组内的第一条数据进行区块间或者其它单一数据进行排序
						 * 解决办法是：取最大的数据，进而取最大数据的同一区间数据组成一个区块
						 */
						
						//取最大对象的索引
						int maxIndex = CompareUtil.getMaxIndexBySealTime(dataList);
						ReceiptInfo newData = dataList.get(maxIndex);
						
						//获取最大对象的编号
						String code = newData.getCode();
						
						// 用来临时存放被查找出来的同一组数据
						List<ReceiptInfo> tempList = new ArrayList<ReceiptInfo>();
						tempList.add(newData);
						dataList.remove(maxIndex);
						
						//继续查找同编码的记录,组成为一个区块
						for(int j=maxIndex; j<dataList.size();){
							ReceiptInfo tempData = dataList.get(j);
							if(tempData.getCode().equals(code)){
								tempList.add(tempData);
								dataList.remove(j);								
							}else{
								//若要找的记录不是同一组的记录, 则退出
								break;
							}
						}	
						
						alist.add(tempList);
						
					} else { // 重复数据已经过升序排序的解决方案
						
						//取最小对象的索引
						int minIndex = CompareUtil.getMinIndexBySealTime(dataList);
						ReceiptInfo newData = dataList.get(minIndex);
						
						//获取最大对象的编号
						String code = newData.getCode();
						
						newDataList.add(newData);
						dataList.remove(minIndex);
						
						//继续查找同编码的记录,组成为一个区块
						for(int j=minIndex; j<dataList.size();){
							ReceiptInfo tempData = dataList.get(j);
							if(tempData.getCode().equals(code)){
								newDataList.add(tempData);
								dataList.remove(j);								
							}else{
								//若要找的记录不是同一组的记录, 则退出
								break;
							}
						}
					}					
				}
				
				if (requestParamMap.get("RepetiveDataOrdinalType") != null
						&& requestParamMap.get("RepetiveDataOrdinalType").toString().length() > 0
						&& "1".equals(requestParamMap.get("RepetiveDataOrdinalType").toString())) {
					
					//以区块组的形式进行倒序
					for(int j=alist.size()-1; j>=0; j--){
						
						// 区块组内的数据记录保持原有的排序
						for(int m=0; m<alist.get(j).size(); m++){							
							newDataList.add(alist.get(j).get(m));							
						}						
					}
				}
			}
			
			/**
			System.out.println("--------------经排序后的返回数据------------------");			
			for(int i=0; i<newDataList.size(); i++){				
				System.out.println(newDataList.get(i).getCode()+"--"+newDataList.get(i).getSealTime());
			}
			System.out.println("------------------end---------------------");
			**/
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			
			// 创建 xml 文档中Header子元素节点Response信息对象
			HeaderResponseEntity hre = new HeaderResponseEntity("000000000", "交易成功",
					sdf.format(new Date()), "C57", sdf.format(new Date()));

			msg = Business.xmlDoc2String(headerParamMap, hre, newDataList, recordCount);
			/**
			 * 排序检查
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(int i=0; i<newDataList.size(); i++){
				ReceiptInfo r = newDataList.get(i);
				System.out.println((r.getSealTime()==null?"":sdf1.format(r.getSealTime()))+" ###### "+r.getCode()+" ###### "+sdf1.format(r.getTrndate()));
			}**/
			
			
			
			// msg = ResponseMsgBuilder.buildMSG("000", "成功",
			// receiptInfo.toXmlElement());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		return msg.replace("\n", "");
	}
	
	

}
