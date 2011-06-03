package com.yazo.CMCC;

import java.io.IOException;

import javax.microedition.io.HttpConnection;

import com.yazo.application.biz.Config;
import com.yazo.model.ConfigKeys;
import com.yazo.model.IXmlParser;
import com.yazo.network.HttpConnect;
import com.yazo.util.Consts;
import com.yazo.util.GZIP;
import com.yazo.util.HtmlEscape;
import com.yazo.util.User;

/*
 * CMCC网站操作
 * 
 * 参数：
 * （必须）cmcc_service_url: eg, http://211.140.17.83/cmread/portalapi
 * （必须）cmcc_user_agent
 * （必须）cmcc_agent_password
 * （可选）cmcc_user_id
 * （可选）use_cmcc_proxy
 * （可选）on_debug /相关参数：log_service, client_id
 * （输出）http_status 
 */
public class CmccWebSite {
	public String cmcc_service_url, cmcc_user_agent, cmcc_agent_password, cmcc_user_id;
	public boolean on_debug;
	public String log_service;
	public int client_id;
	public boolean use_cmcc_proxy;
	public int http_status;
	
	private String cookie;
	private String response_encoding, response_result_code, reg_code, response_content_length, response_location_path;
	private Config config;
	
	public CmccWebSite(){
		cmcc_service_url = cmcc_user_agent = cmcc_agent_password = cmcc_user_id = null;
		on_debug = false;
		log_service = null;
		client_id = 0;
		use_cmcc_proxy = false;
		http_status = 0;
		
		cookie = "";
		response_encoding = response_result_code = reg_code = response_content_length = response_location_path = null;
		config = Config.getInstance();
	}
	
	public byte[] post(String headerAction, String data){
		HttpConnect conn = new HttpConnect();
		if (use_cmcc_proxy)
			conn.setCmccProxy();
		else
			conn.setNoProxy();
		conn.setHttpHeader(CmccHeader(headerAction, data.length()));
		String ck = cookie;
		conn.post(cmcc_service_url, data);
		byte[] res = conn.getContent();
		byte[] cont = null;
		http_status = conn.status;
		getHttpHeaderFields(conn);
		if (response_encoding!=null && response_encoding.equals("gzip")){
			try {
				cont = GZIP.inflate(res);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (config.getInt(ConfigKeys.CMCC_DEBUG)==1){
			String log_txt = "status=" + conn.status +
				"\nCookie(before):" + ck +
				"\nCookie(after):" + cookie +
				"\nResponse header:\n[" + conn.headerText + "]" +
				"\nResponse:\n[" + new String(cont) + "]";
			log(headerAction + (use_cmcc_proxy? "(Cmwap)" : ""), log_txt);
		}
		
		conn.close();
		conn = null;
		return cont;
	}
	public void log(String operate, String result){
		String log_txt = "dbg=1&cm_user_id=" + client_id +
			"&operate=" + operate + "|url=[" + cmcc_service_url + "]" +
//			"&url=" + service_url +
			"&result=" + HtmlEscape.encode(result,null);
		HttpConnect conn = new HttpConnect();
		conn.setNoProxy();
		conn.setHttpHeader(new String[]{
				"Accept", "*/*",
				"Content-Type", "application/x-www-form-urlencoded",
				"Content-Length", "" + log_txt.length()
		});
		conn.post(config.getString(ConfigKeys.CONTENT_SERVER)+"/reader/logs/addop", log_txt);
		conn.close();
		conn = null;
	}
	
	
	
	public Object post(IXmlParser parser, String action, String data){
		HttpConnect conn = new HttpConnect();
		conn.setCmccProxy();
		conn.setHttpHeader(CmccHeader(data.length()));
		conn.post(cmcc_service_url+action, data);
		http_status = conn.status;
		Object r = null;
		if (parser!=null && conn.status==HttpConnection.HTTP_OK){
			r = parser.parse(conn.inStream);
		}
		conn.close();
		conn = null;
		return r;
	}

	public String get(String action, String params){
		HttpConnect conn = new HttpConnect();
		conn.setCmccProxy();
		conn.setHttpHeader(CmccHeader(0));
		conn.open(cmcc_service_url+action+params);
		conn.close();
		conn = null;
		return null;
	}
	public void loadContent(String action, String params){
		if(cmcc_service_url==null) return;
		
		HttpConnect conn = new HttpConnect();
		conn.setCmccProxy();
		conn.setHttpHeader(CmccHeader(0));
		conn.open(cmcc_service_url+action+params);
		conn.close();
		conn = null;
	}
	private String[] CmccHeader(String action, int dataLength){
		String[] header = {
				"Accept-Charset", "UTF-8",
				"Accept", "*/*",
				"Client-Agent", cmcc_user_agent,
//				"X-SOURCE-ID", "cmwap", 
//				"VIA", "WTP/1.1 GDGZ-PS-GW003-WAP03.gd.chinamobile.com (Nokia WAP Gateway 4.0/CD3/4.1.29)", 
//				"user-agent", "NokiaN73-1/4.0747.31.1.1 Series60/3.0 Profile/MIDP-2.0 Configuration/CLDC-1.1",
//				"x-up-calling-line-id", "13400981944",
				"user-id",  "",
				"APIVersion", "1.1.0",
				"Cookie", cookie,
				"Encoding-Type", "gzip",
				"Action", action,
				"ClientHash", "",
//				"Connection", "Keep-Alive",
//				"Proxy-Connection", "Keep-Alive",				
				"Version", cmcc_user_agent,
				"Content-Type", "application/xml",
				"Content-Length", "" + dataLength
		};
		return header;
	}
	private String[] CmccHeader(int dataLength){
		return CmccHeader("", dataLength);
	}
	/**
	 * 获取头信息
	 */
	public void getHttpHeaderFields(HttpConnect conn) {
		if(conn==null || conn.status!=200) return;
		try {
			response_encoding = conn.getHttpHeader("Encoding-Type");
			response_result_code = conn.getHttpHeader("Result-Code");
			if(response_result_code==null) response_result_code = "0";
			if (response_result_code.equals("4003")) {  // redirect??
				cmcc_service_url = conn.getHttpHeader("Request-URL");
			}
			response_content_length = conn.getHttpHeader("Content-Length");
			String c = conn.getHttpHeader("Set-Cookie");
			if (c!=null) cookie = c;
			reg_code = conn.getHttpHeader("RegCode");
			
		    response_location_path = conn.getHttpHeader("Location");
			response_location_path = response_location_path==null?"":response_location_path;
//			if(!"".equals(location_path)){
//				Consts.LOCATION_PATH=location_path;
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
