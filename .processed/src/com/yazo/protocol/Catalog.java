package com.yazo.protocol;

import com.yazo.net.Connect;

public class Catalog extends Connect{
	
	
	public Catalog(int connect, String ip, int port) {
		super(connect, ip, port);
	}
	public Catalog(String url,String action,String methed){
		super(url,action,methed);
	}
	
	/**
	 * 包月扣费
	 * @param parameic
	 * @return
	 */
	public boolean subscribeCatalog(String parameic){
		byte[] output=null;
		try{
			output=this.queryServer(parameic.getBytes());
		}catch(Exception e){
			
		}
		return output==null?false:true;
		
	}

}
