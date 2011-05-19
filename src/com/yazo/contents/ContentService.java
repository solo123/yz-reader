package com.yazo.contents;

import com.yazo.mobile.MobileSysData;
import com.yazo.model.ServiceData;

public class ContentService {
	private String serviceUrl;
	
	public ContentService(String serviceUrl){
		this.serviceUrl = serviceUrl;
	}

	public ServiceData login(MobileSysData data){
		ServiceData sd = new ServiceData();
		sd.BUSINESS = "business";
		return sd;
	}

}
