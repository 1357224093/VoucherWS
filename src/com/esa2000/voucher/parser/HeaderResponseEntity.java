package com.esa2000.voucher.parser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class HeaderResponseEntity {

	
	@XmlElement
	private String ReturnCode;	
	@XmlElement
	private String ReturnMessage;
	@XmlElement
	private String ResponseTime;
	@XmlElement
	private String ProviderChannelId;
	@XmlElement
	private String ProviderReference;
	
	
	public HeaderResponseEntity() {
		super();
	}
	public HeaderResponseEntity(String returnCode, String returnMessage,
			String responseTime, String providerChannelId,
			String providerReference) {
		super();
		ReturnCode = returnCode;
		ReturnMessage = returnMessage;
		ResponseTime = responseTime;
		ProviderChannelId = providerChannelId;
		ProviderReference = providerReference;
	}
	public String getReturnCode() {
		return ReturnCode;
	}
	public void setReturnCode(String returnCode) {
		ReturnCode = returnCode;
	}
	public String getReturnMessage() {
		return ReturnMessage;
	}
	public void setReturnMessage(String returnMessage) {
		ReturnMessage = returnMessage;
	}
	public String getResponseTime() {
		return ResponseTime;
	}
	public void setResponseTime(String responseTime) {
		ResponseTime = responseTime;
	}
	public String getProviderChannelId() {
		return ProviderChannelId;
	}
	public void setProviderChannelId(String providerChannelId) {
		ProviderChannelId = providerChannelId;
	}
	public String getProviderReference() {
		return ProviderReference;
	}
	public void setProviderReference(String providerReference) {
		ProviderReference = providerReference;
	}

	
	
}
