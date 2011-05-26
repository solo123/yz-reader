package com.yazo.contents;

import com.yazo.application.biz.MobileInfo;
import com.yazo.model.ServiceData;

public class ContentService {
	private String serviceUrl;
	
	public ContentService(String serviceUrl){
		this.serviceUrl = serviceUrl;
	}

	public ServiceData login(MobileInfo data){
		ServiceData sd = new ServiceData();
		sd.BUSINESS = "business";
		return sd;
	}

}
