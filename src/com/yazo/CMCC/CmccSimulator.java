package com.yazo.CMCC;

import java.util.Vector;

import javax.microedition.io.HttpConnection;

import com.yazo.application.biz.Config;
import com.yazo.model.ConfigKeys;
import com.yazo.util.HBase64;
import com.yazo.util.MD5;
import com.yazo.util.ServiceData;
import com.yazo.util.StringUtil;

public class CmccSimulator {
	public static ServiceData serviceData;
	static Vector progressIDFree = null;
	static Vector progressIDCharge = null;
	public static int total = 0;
	public static int tempInt = 0;
	private Config config = Config.getInstance();
	
	private CmccWebSite website;
	private boolean canceled = false;

	public void doProcessOnCmcc(){
		// #ifdef DBG
		System.out.println("Start Cmcc Simulate.");
		// #endif
		
		website = new CmccWebSite(
				config.getString(ConfigKeys.CMCC_USER_AGENT),
				config.getString(ConfigKeys.CMCC_SERVICE)
		);
		
		String userid = register(
				config.getString(ConfigKeys.CMCC_USER_AGENT),
				config.getString(ConfigKeys.CMCC_CLIENT_PASSWORD)
		);
		if (canceled || userid==null) return;  // cannot regist
		sleep(5000);
		if (canceled) return;
		
		Boolean auth = authenticate(
				config.getString(ConfigKeys.CMCC_USER_AGENT), 
				userid, 
				config.getString(ConfigKeys.CMCC_CLIENT_PASSWORD), 
				config.getString(ConfigKeys.CMCC_CHANNEL)
		);
		if (canceled || auth!=Boolean.TRUE) return;  // cannot authenticate
		sleep(5000);
		if (canceled) return;
		
		website.get("getClientWelcomeInfo", "");

		// TODO: 安装服务器传回的访问序列，依次访问免费或收费页面
		//  if !catalogId && contentid, 1)getContentInfo?contentId=xxx 2)getChapterInfo?contentId=xxx&chapterId=xxx
		//  if catalogId && contentid,  1)getCatalogInfo?catalogId=xxx 2)getContentInfo, 3)getChapterInfo
		
		
		// TODO: 模拟扣费章节流程
		//
		//
//				doSubscribeChapter(index);
//				doSubscribeChapterPV(index);

		// 包月目录
		// get subscribeCatalog?catalogId=xxx
		
		// 购买图书
		// get getContentProductInfo?contentId=xxx
		// get subscribeContent?contentId=xxx&productId=xxx
		
		// 购买章节
		// get getContentProductInfo?contentId=xxx&chapterId=xxx
		// get subscribeContent?contentId=xxx&chapterId=xxx&productId=xxx
	}
	
	
	/*
	 * config: CMCC_USER_AGENT, CMCC_CLIENT_PASSWORD
	 * return userid
	 */
	public String register(String agent, String password){
		// #ifdef DBG
		System.out.println("register");
		// #endif
		String strM = MD5.toMD5(agent+password).toLowerCase();
		String pp = HBase64.encode(StringUtil.hexStringToByte(strM));
		String xml = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<Request>" + 
				"<RegisterReq>" +
					"<clientHash>" + pp + "</clientHash>" +
				"</RegisterReq>"+
			"</Request>";
		
		Object o = website.post(new CmccRegisterParser(), "register", xml);
		if (o==null) return null;
		else return (String)o;
	}
	
	public Boolean authenticate(String agent, String userid, String password, String channel) {
		String strM = MD5.toMD5(agent+userid+password).toLowerCase();
		String pp = HBase64.encode(StringUtil.hexStringToByte(strM));
		String xml = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<Request>" + 
				"<AuthenticateReq>" +
					"<clientHash>" + pp + "</clientHash>" +
					"<channel>" + channel + "</channel>" +
				"</AuthenticateReq>"+
			"</Request>";

		website.post(null, "authenticate2", xml);
		if (website.http_status == HttpConnection.HTTP_OK) return Boolean.TRUE;
		else return Boolean.FALSE;
	}

	private void sleep(int millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

}
