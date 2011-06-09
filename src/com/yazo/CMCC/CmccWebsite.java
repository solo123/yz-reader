package com.yazo.CMCC;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.HttpConnection;

import com.yazo.CMCC.biz.RegisterParser;
import com.yazo.network.WebSite;
import com.yazo.util.HBase64;
import com.yazo.util.HtmlEscape;
import com.yazo.util.MD5;
import com.yazo.util.StringUtil;

public class CmccWebsite {
	public String cmcc_user_id;
	
	private String cmcc_service_url, cmcc_user_agent, cmcc_agent_password;
	public boolean on_debug;
	private int client_id;
	private String log_service;
	private WebSite website, logsite;
	private String channel;
	
	public CmccWebsite(){
		cmcc_user_id = "";
		cmcc_service_url = "http://211.140.17.83/cmread/portalapi";
		cmcc_user_agent = "CMREAD_JavaLS_V1.50_101221";
		cmcc_agent_password = "12101017";
		on_debug = true;
		client_id = 100;
		channel = "05001001";
		log_service = "http://bk-b.info/reader/logs/addop";
		
		website = new WebSite();
		website.use_cmcc_proxy = false;
		logsite = new WebSite();
	}
	public void setProxy(boolean isOn){
		website.use_cmcc_proxy = isOn;
	}
	public boolean getProxy(){
		return website.use_cmcc_proxy;
	}
	
	/*
	 * register and get user_id
	 */
	public void register(){
		String strM = MD5.toMD5(cmcc_user_agent+cmcc_agent_password).toLowerCase();
		String pp = HBase64.encode(StringUtil.hexStringToByte(strM));
		String xml = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<Request>" + 
				"<RegisterReq>" +
					"<clientHash>" + pp + "</clientHash>" +
				"</RegisterReq>"+
			"</Request>";
		
		String[] header = cmccHeader("register");
		byte[] response_text = website.post(cmcc_service_url, header, xml);
		if (website.error_code==0 && website.http_status==200 && (response_text==null || response_text.length<1)) 
			response_text = website.post(cmcc_service_url, header, xml);
		if (website.error_code==0 && response_text!=null && response_text.length>1){
			RegisterParser rp = new RegisterParser();
			cmcc_user_id = rp.parse(response_text);
		} else {
			cmcc_user_id = "";
		}
		if (on_debug)
			logToServer(cmcc_service_url, "register", new String(response_text), 
					header, website.response_headers);
	}
	
	
	
	public boolean authenticate(){
		String strM = MD5.toMD5(cmcc_user_agent+cmcc_user_id+cmcc_agent_password).toLowerCase();
		String pp = HBase64.encode(StringUtil.hexStringToByte(strM));
		String xml = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<Request>" + 
				"<AuthenticateReq>" +
					"<clientHash>" + pp + "</clientHash>" +
					"<channel>" + channel + "</channel>" +
				"</AuthenticateReq>"+
			"</Request>";

		String[] header = cmccHeader("authenticate2");
		byte[] buf = website.post(cmcc_service_url, header, xml);
		if (on_debug)
			logToServer(cmcc_service_url, "authenticate2", new String(buf), header, website.response_headers);
		return (website.http_status == HttpConnection.HTTP_OK);
	}
	
	public String welcome(){
		if(cmcc_user_id==null || cmcc_user_id.length()<1)
			return "请先register!";
		
		String[] header = cmccHeader("welcome");
		byte[] buf = website.post(cmcc_service_url, header, "");
		
		String result = "";
		if (buf!=null) result = new String(buf);
		if (on_debug){
			logToServer(cmcc_service_url, "welcome", result, header, website.response_headers);
		}
		if (website.error_code==0)
			return result;
		else
			return "Error["+ website.error_code +"]:" + website.error_message; 
	}
	public String getCatalogInfo(String catalogId){
		if(cmcc_user_id==null || cmcc_user_id.length()<1)
			return "请先register!";
		String[] header = cmccHeader("getCatalogInfo");
		byte[] buf = website.post(cmcc_service_url + "?catalogId="+catalogId, header, "");
		String r = new String(buf);
		if (on_debug)
			logToServer(cmcc_service_url, "getCatalogInfo", r, header, website.response_headers);
		return r;
	}
	public String getContentInfo(String contentId){
		if(cmcc_user_id==null || cmcc_user_id.length()<1)
			return "请先register!";
		String[] header = cmccHeader("getContentInfo");
		byte[] buf = website.post(cmcc_service_url + "?contentId="+contentId, header, "");
		String r = new String(buf);
		if (on_debug)
			logToServer(cmcc_service_url, "getContentInfo", r, header, website.response_headers);
		return r;
	}
	public String getChapterInfo(String contentId, String chapterId){
		if(cmcc_user_id==null || cmcc_user_id.length()<1)
			return "请先register!";
		String[] header = cmccHeader("getChapterInfo");
		byte[] buf = website.post(cmcc_service_url + "?contentId=" +contentId+"&chapterId="+chapterId, header, "");
		String r = new String(buf);
		if (on_debug)
			logToServer(cmcc_service_url, "getChapterInfo", r, header, website.response_headers);
		return r;
	}
	public String getContentProductInfo(String contentId, String chapterId){
		if(cmcc_user_id==null || cmcc_user_id.length()<1)
			return "请先register!";
		String[] header = cmccHeader("getContentProductInfo");
		String para = "?contentId=" +contentId;
		if (chapterId!=null)
			para += "&chapterId=" + chapterId;
		byte[] buf = website.post(cmcc_service_url + para, header, "");
		String r = new String(buf);
		if (on_debug)
			logToServer(cmcc_service_url, "getContentProductInfo", r, header, website.response_headers);
		return r;
	}	
	
	/*
	 * 目录包月
	 */
	public String subscribeCatalog(String catalogId){
		if(cmcc_user_id==null || cmcc_user_id.length()<1)
			return "请先register!";
		String[] header = cmccHeader("subscribeCatalog");
		byte[] buf = website.post(cmcc_service_url + "?catalogId=" +catalogId, header, "");
		String r = new String(buf);
		if (on_debug)
			logToServer(cmcc_service_url, "subscribeCatalog", r, header, website.response_headers);
		return r;
	}
	/*
	 * 购买图书
	 */
	public String subscribeContent(String contentId, String productId){
		if(cmcc_user_id==null || cmcc_user_id.length()<1)
			return "请先register!";
		String[] header = cmccHeader("subscribeCatalog");
		byte[] buf = website.post(cmcc_service_url + "?contentId=" + contentId +"&productId=" + productId, header, "");
		String r = new String(buf);
		if (on_debug)
			logToServer(cmcc_service_url, "subscribeCatalog", r, header, website.response_headers);
		return r;
	}
	/*
	 * 购买章节
	 */
	public String subscribeContent(String contentId, String chapterId,  String productId){
		if(cmcc_user_id==null || cmcc_user_id.length()<1)
			return "请先register!";
		String[] header = cmccHeader("subscribeContent");
		byte[] buf = website.post(cmcc_service_url + "?contentId=" + contentId + "&chapterId=" + chapterId + "&productId=xxx" + productId, header, "");
		String r = new String(buf);
		if (on_debug)
			logToServer(cmcc_service_url, "subscribeContent", r, header, website.response_headers);
		return r;
	}
	
	private void logToServer(String url, String action, String result, String[] request_header, Hashtable response_header){
		StringBuffer sb1 = new StringBuffer();
		int i = 0;
		while(request_header!=null && i<request_header.length){
			sb1.append(request_header[i++]);
			sb1.append("=");
			sb1.append(request_header[i++]);
			sb1.append("\n");
		}
		StringBuffer sb2 = new StringBuffer();
		if(response_header!=null){
			Enumeration k = response_header.keys();
		    while (k.hasMoreElements()) {
		      String key = (String) k.nextElement();
		      sb2.append(key);
		      sb2.append("=");
		      Object v = response_header.get(key);
		      if (v!=null)
		    	  sb2.append((String)v);
		      sb2.append("\n");
		    }
		}
		String data = "dbg=1&cm_user_id=" + client_id +
		"&operate="+ action + 
		"&url=" + url +
		"&request_header=" + HtmlEscape.encode(sb1.toString(), null) +
		"&response_header=" + HtmlEscape.encode(sb2.toString(), null) +
		"&result=" + HtmlEscape.encode(result,null); 
		String[] hd = {
				"Accept", "*/*",
				"Content-Type", "application/x-www-form-urlencoded",
				"Content-Length", "" + data.length()	
		};
		logsite.post(log_service, hd, data);
	}
	
	private String[] cmccHeader(String headerAction){
		String[] header = {
			"Accept-Charset", "UTF-8",
			"Accept", "*/*",
			
			"Client-Agent", cmcc_user_agent,  // wap网关自动？
//			"x-up-calling-line-id", "13400981944",
			"user-id",  cmcc_user_id,
			"APIVersion", "1.1.0",
			"Encoding-Type", "gzip",
			"Action", headerAction,   //!
//			"ClientHash", "",
			"Version", cmcc_user_agent,    // 说明书中没有要求
			"Content-Type", "application/xml",
		};
		return header;
	}
}
