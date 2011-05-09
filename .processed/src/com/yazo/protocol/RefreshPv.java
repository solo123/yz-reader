package com.yazo.protocol;

import org.kxml2.io.KXmlParser;
import org.kxml2.io.ParserXml;

import com.yazo.net.Connect;
/**
 * pv服务 
 */
public class RefreshPv extends Connect{
	
	
	public RefreshPv(int connect, String ip, int port) {
		super(connect, ip, port);
	}
	public RefreshPv(String url,String action,String methed){
		super(url,action,methed);
	}
	
	/**
	 * 
	 * @param parameic
	 * @return
	 */
	public String doRefreshPv(String parameic){
		KXmlParser xp = null;
		String obj =""; 
		try{
			xp= (KXmlParser)this.queryServerForXML(parameic);
			obj = ParserXml.getCatalogInfo(xp);
		}catch(Exception e){
			e.printStackTrace();
		}
		return obj;
		
	}
	public Object doRefreshPv1(String parameic){
		KXmlParser xp = null;
		try{
			xp= (KXmlParser)this.queryServerForXML(parameic);
//			obj = ParserXml.getCatalogInfoArray(xp);
		}catch(Exception e){
			
		}
		return xp;
		
	}

}
