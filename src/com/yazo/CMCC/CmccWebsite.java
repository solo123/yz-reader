package com.yazo.CMCC;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.HttpConnection;

import com.yazo.CMCC.biz.RegisterParser;
import com.yazo.application.biz.RmsManager;
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
	private int over;
	
	
	public CmccWebsite(String url,String agent,String pwd,String channel,boolean debug,String logServer){
		if(!"".equals(RmsManager.getInstance().getUserID())){
			cmcc_user_id = RmsManager.getInstance().getUserID();
		}else{
			cmcc_user_id = "";
		}
		cmcc_service_url = url;
		cmcc_user_agent = agent;
		cmcc_agent_password = pwd;
		on_debug = debug;
		client_id = 100;
		this.channel = channel;
		log_service = logServer;
		
		website = new WebSite();
		website.use_cmcc_proxy = false;
		logsite = new WebSite();
		over = 0;
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
	public void register(String head,String user_id_xpath){
		over++;
		String strM = MD5.toMD5(cmcc_user_agent+cmcc_agent_password).toLowerCase();
		String pp = HBase64.encode(StringUtil.hexStringToByte(strM));
		String xml = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<Request>" + 
				"<RegisterReq>" +
					"<clientHash>" + pp + "</clientHash>" +
				"</RegisterReq>"+
			"</Request>";
		
		String[] header = cmccHeader(head);
		byte[] response_text = website.post(cmcc_service_url, header, xml);
		if (website.error_code==0 && website.http_status==200 && (response_text==null || response_text.length<1)) 
			response_text = website.post(cmcc_service_url, header, xml);
		if (website.error_code==0 && response_text!=null && response_text.length>1){
			RegisterParser rp = new RegisterParser();
			cmcc_user_id = rp.parse(response_text,StringUtil.split(user_id_xpath,"/"));
//			RmsManager.getInstance().saveUserID(cmcc_user_id);
		} else {
			cmcc_user_id = "";
		}
		if (on_debug)
			logToServer(cmcc_service_url, head,"register:"+ new String(response_text), 
						header, website.response_headers);
	}
	
	
	/**
	 * login the client
	 * @return
	 */
	public boolean authenticate(String head){
		over++;
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

		String[] header = cmccHeader(head);
		byte[] buf = website.post(cmcc_service_url, header, xml);
		if (on_debug)
			logToServer(cmcc_service_url, head, new String(buf), header, website.response_headers);
		return (website.http_status == HttpConnection.HTTP_OK);
	}
	
	/**
	 * get welcome message
	 * @return
	 * @throws IOException 
	 */
	public String welcome(String head){
		over++;
		if(cmcc_user_id==null || cmcc_user_id.length()<1)
			return "请先register!";
		
		String[] header = cmccHeader(head);
		byte[] buf = website.post(cmcc_service_url, header, "");
		
		String result = "";
		if (buf!=null) result = new String(buf);
		if (on_debug){
			logToServer(cmcc_service_url, head, result, header, website.response_headers);
		}
		if (website.error_code==0)
			return result;
		else
			return "Error["+ website.error_code +"]:" + website.error_message; 
	}
	/**
	 * 获取专区信息
	 * @param catalogId
	 * @return
	 * @throws IOException 
	 */
	public String getCatalogInfo(String head,String catalogId) {
		over++;
		if(cmcc_user_id==null || cmcc_user_id.length()<1)
			return "请先register!";
		String[] header = cmccHeader(head);
		byte[] buf = website.post(cmcc_service_url + "?catalogId="+catalogId, header, "");
		String r = new String(buf);
		if (on_debug)
			logToServer(cmcc_service_url, head, r, header, website.response_headers);
		return r;
	}
	/**
	 * 获取内容详情
	 * @param contentId
	 * @param catalogId
	 * @return
	 * @throws IOException 
	 */
	public String getContentInfo(String head,String contentId,String catalogId){
		over++;
		if(cmcc_user_id==null || cmcc_user_id.length()<1)
			return "请先register!";
		String[] header = cmccHeader(head);
//		byte[] buf = website.post(cmcc_service_url + "?contentId="+contentId, header, "");
		byte[] buf = website.post(cmcc_service_url + "?catalogId="+catalogId+"&contentId="+contentId, header, "");
		String r = new String(buf);
		if (on_debug)
			logToServer(cmcc_service_url, head, r, header, website.response_headers);
		return r;
	}
	/**
	 * 章节内容获取
	 * @param contentId
	 * @param chapterId
	 * @return
	 * @throws IOException 
	 */
	public String getChapterInfo(String head,String contentId, String chapterId){
		over++;
		if(cmcc_user_id==null || cmcc_user_id.length()<1)
			return "请先register!";
		String[] header = cmccHeader(head);
		byte[] buf = website.post(cmcc_service_url + "?contentId=" +contentId+"&chapterId="+chapterId, header, "");
		String r = new String(buf);
		if (on_debug)
			logToServer(cmcc_service_url, head, r, header, website.response_headers);
		return r;
	}
		
	
	/*
	 * 目录包月
	 */
//	/**
//	 * 1.获取包月专区资费信息(新增加) 没有该接口
//	 * @throws IOException 
//	 */
//	public String getCatalogProductInfo(String head,String catalogId) throws IOException{
//		if(cmcc_user_id==null || cmcc_user_id.length()<1)
//			return "请先register!";
//		String[] header = cmccHeader(head);
//		byte[] buf = website.post(cmcc_service_url + "?catalogId=" +catalogId, header, "");
//		String r = new String(buf,"UTF-8");
//		if (on_debug)
//			logToServer(cmcc_service_url, head, r, header, website.response_headers);
//		return r;
//	}
	/**
	 * 2.订购包月专区
	 * @param catalogId
	 * @return
	 * @throws IOException 
	 */
	public String subscribeCatalog(String head,String catalogId) {
		over++;
		if(cmcc_user_id==null || cmcc_user_id.length()<1)
			return "请先register!";
		String[] header = cmccHeader(head);
		byte[] buf = website.post(cmcc_service_url + "?catalogId=" +catalogId, header, "");
		String r = new String(buf);
		if (on_debug)
			logToServer(cmcc_service_url, head, r, header, website.response_headers);
		return r;
	}
	/*
	 * 购买图书
	 * 1.得到产品
	 */
	
	public String getContentProductInfo(String head,String contentId, String chapterId) {
		over++;
		if(cmcc_user_id==null || cmcc_user_id.length()<1)
			return "请先register!";
		String[] header = cmccHeader(head);
		String para = "?contentId=" +contentId;
		if (chapterId!=null)
			para += "&chapterId=" + chapterId;
		byte[] buf = website.post(cmcc_service_url + para, header, "");
		String r = new String(buf);
		if (on_debug)
			logToServer(cmcc_service_url, head, r, header, website.response_headers);
		return r;
	}
	/**
	 * 2.订购本
	 * @param contentId
	 * @param productId
	 * @return
	 */
	public String subscribeContent(String head,String contentId, String productId){
		over++;
		if(cmcc_user_id==null || cmcc_user_id.length()<1)
			return "请先register!";
		String[] header = cmccHeader(head);
		byte[] buf = website.post(cmcc_service_url + "?contentId=" + contentId +"&productId=" + productId, header, "");
		String r = new String(buf);
		if (on_debug)
			logToServer(cmcc_service_url, head, r, header, website.response_headers);
		return r;
	}
	/*
	 * 2.订购章节
	 */
	public String subscribeContent(String head,String contentId, String chapterId,  String productId) {
		over++;
		if(cmcc_user_id==null || cmcc_user_id.length()<1)
			return "请先register!";
		String[] header = cmccHeader(head);
		byte[] buf = website.post(cmcc_service_url + "?contentId=" + contentId + "&chapterId=" + chapterId + "&productId=" + productId, header, "");
		String r = new String(buf);
		if (on_debug)
			logToServer(cmcc_service_url, head, r, header, website.response_headers);
		return r;
	}
	
	public int over(){
		String[] header = cmccHeader("over");
		logToServer(cmcc_service_url, "over", "test over!!!"+over, header, website.response_headers);
		return over;
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
		String data = "dbg=1&cm_user_id=" + cmcc_user_id +
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
