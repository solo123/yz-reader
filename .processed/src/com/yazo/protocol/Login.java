package com.yazo.protocol;

import org.kxml2.io.KXmlParser;

import com.yazo.net.Connect;

public class Login extends Connect{
	public Login(int connect, String ip, int port) {
		super(connect, ip, port);
	}
	public Login(String url,String action){
		super(url,action,"POST");
	}
	
	
	/**
	 * 登录 是否成功  鉴权
	 * @param requestxml
	 * @return
	 */
	public Object authenticate(String requestxml){
		KXmlParser obj=null;
		try {
			obj=(KXmlParser)queryServerForXML(requestxml);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
}
