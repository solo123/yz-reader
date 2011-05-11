package com.yazo.protocol;

import com.yazo.net.Connect;

public class YaZhouChannel extends Connect{

	public YaZhouChannel(int connect, String ip, int port) {
		super(connect, ip, port);
	}
	public YaZhouChannel(String url,String action,String methed){
		super(url,action,methed);
	}
	
	public Object getYaZhou(String parameic){
		Object obj=null;
		byte[] output=null;
		try{
			output=this.queryServer(parameic.getBytes());
			if(output!=null){
				obj=new String(output,"UTF-8");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return obj;
	}
}
