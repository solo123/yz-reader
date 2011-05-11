package com.yazo.protocol;


import com.yazo.net.Connect;

public class Register extends Connect{

	public Register(int connect, String ip, int port) {
		super(port, ip, port);
		// TODO 自动生成构�?函数存根
	}
	public Register(String url,String action){
		super(url,action,"POST");
	}
	/**
	 * 注册返回XML
	 * @param requestxml
	 * @return
	 */
	public Object getParser(String requestxml){
		Object obj=null;
		try {
			obj=queryServerForXML(requestxml);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	
}
