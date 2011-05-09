package com.yazo.protocol;

import com.yazo.net.Connect;

public class WelcomeInfo extends Connect{
	public WelcomeInfo(int connect, String ip, int port) {
		super(connect, ip, port);
	}
	public WelcomeInfo(String url,String action,String methed){
		super(url,action,methed);
	}
	/**
	 * 欢迎界面返回XML
	 * @param requestxml
	 * @return
	 */
	public Object getClientWelcomeInfo(String requestxml){
		Object obj=null;
		try {
			obj=this.queryServerForXML("");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
}
