package com.yazo.CMCC;

import javax.microedition.io.HttpConnection;

import com.yazo.application.biz.Config;
import com.yazo.model.ConfigKeys;
import com.yazo.model.IXmlParser;
import com.yazo.network.HttpConnect;
import com.yazo.util.Consts;
import com.yazo.util.HtmlEscape;
import com.yazo.util.User;

public class CmccWebSite {
	private String cookie = "";
	private String service_url = null, user_agent = null;
	private String encoding, result_code, reg_code, content_length, location_path;
	private Config config = Config.getInstance();
	public boolean byCmwap = false;
	public int http_status = 0;
	
	public CmccWebSite(String userAgent, String serviceUrl, boolean useCmwap){
		user_agent = userAgent;
		service_url = serviceUrl;
		encoding = result_code = reg_code = content_length = null;
		byCmwap = useCmwap;
	}
	public String post(String headerAction, String data){
		HttpConnect conn = new HttpConnect();
		if (byCmwap)
			conn.setCmccProxy();
		else
			conn.setNoProxy();
		conn.setHttpHeader(CmccHeader(headerAction, data.length()));
		String ck = cookie;
		conn.post(service_url, data);
		String s = conn.getContent();
		http_status = conn.status;
		getHttpHeaderFields(conn);
		if (config.getInt(ConfigKeys.CMCC_DEBUG)==1){
			String log_txt = "status=" + conn.status +
				"\nCookie(before):" + ck +
				"\nCookie(after):" + cookie +
				"\nResponse:\n[" + s + "]" +
				"\nResponse header:\n[" + conn.headerText + "]";
			System.out.println("log:" + log_txt);
			log(config.getInt(ConfigKeys.YZ_CLIENT_ID), headerAction + ":" + byCmwap, log_txt);
		}
		
		conn.close();
		conn = null;
		return s;
	}
	public void log(int user_id, String operate, String result){
		String log_txt = "dbg=1&cm_user_id=" + user_id +
			"&operate=" + operate +
			"&result=" + HtmlEscape.encode(result,null);
		HttpConnect conn = new HttpConnect();
		conn.setNoProxy();
		conn.setHttpHeader(CmccHeader("", result.length()));
		conn.post(config.getString(ConfigKeys.CONTENT_SERVER)+"/reader/logs/addop", log_txt);
		conn.close();
		conn = null;
	}
	
	
	
	public Object post(IXmlParser parser, String action, String data){
		HttpConnect conn = new HttpConnect();
		conn.setCmccProxy();
		conn.setHttpHeader(CmccHeader(data.length()));
		conn.post(service_url+action, data);
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
		conn.open(service_url+action+params);
		conn.close();
		conn = null;
		return null;
	}
	public void loadContent(String action, String params){
		if(service_url==null) return;
		
		HttpConnect conn = new HttpConnect();
		conn.setCmccProxy();
		conn.setHttpHeader(CmccHeader(0));
		conn.open(service_url+action+params);
		conn.close();
		conn = null;
	}
	private String[] CmccHeader(String action, int dataLength){
		String[] header = {
				"Accept-Charset", "UTF-8",
				"Accept", "*/*",
				"Client-Agent", user_agent,
//				"X-SOURCE-ID", "cmwap", 
//				"VIA", "WTP/1.1 GDGZ-PS-GW003-WAP03.gd.chinamobile.com (Nokia WAP Gateway 4.0/CD3/4.1.29)", 
//				"user-agent", "NokiaN73-1/4.0747.31.1.1 Series60/3.0 Profile/MIDP-2.0 Configuration/CLDC-1.1",
				"x-up-calling-line-id", "13400981944",
				"user-id",  "",
				"APIVersion", "1.1.0",
				"Cookie", cookie,
				"Encoding-Type", "gzip",
				"Action", action,
				"ClientHash", "",
//				"Connection", "Keep-Alive",
//				"Proxy-Connection", "Keep-Alive",				
				"Version", user_agent,
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
			encoding = conn.getHttpHeader("Encoding-Type");
			result_code = conn.getHttpHeader("Result-Code");
			if(result_code==null) result_code = "0";
			if (result_code.equals("4003")) {  // redirect??
				service_url = conn.getHttpHeader("Request-URL");
			}
			content_length = conn.getHttpHeader("Content-Length");
			String c = conn.getHttpHeader("Set-Cookie");
			if (c!=null) cookie = c;
			reg_code = conn.getHttpHeader("RegCode");
			
		    location_path = conn.getHttpHeader("Location");
			location_path = location_path==null?"":location_path;
//			if(!"".equals(location_path)){
//				Consts.LOCATION_PATH=location_path;
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
