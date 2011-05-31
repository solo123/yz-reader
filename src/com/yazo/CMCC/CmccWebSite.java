package com.yazo.CMCC;

import javax.microedition.io.HttpConnection;

import com.yazo.application.biz.Config;
import com.yazo.model.ConfigKeys;
import com.yazo.model.IXmlParser;
import com.yazo.network.HttpConnect;

public class CmccWebSite {
	private String cookie = "";
	private String service_url = null, user_agent = null;
	private String encoding, result_code, reg_code, content_length;
	private Config config = Config.getInstance();
	public int http_status = 0;
	
	public CmccWebSite(String userAgent, String serviceUrl){
		user_agent = userAgent;
		service_url = serviceUrl;
		encoding = result_code = reg_code = content_length = null;
	}
	public String post(String action, String data, boolean byCmwap){
		HttpConnect conn = new HttpConnect();
		if (byCmwap)
			conn.setCmccProxy();
		else
			conn.setNoProxy();
		conn.setHttpHeader(CmccHeader(action, data.length()));
		conn.post(service_url, data);
		http_status = conn.status;
		System.out.println("status="+http_status);
		String s = conn.getContent();
		
		System.out.println("Response:\n["+ s +"]");
		System.out.println("Response header:\n["+ conn.headerText +"]");
		
		conn.close();
		conn = null;
		return s;
	}
	public String log(String action, String result){
		HttpConnect conn = new HttpConnect();
		conn.setNoProxy();
		conn.setHttpHeader(CmccHeader(action, result.length()));
		conn.post(config.getString(ConfigKeys.CONTENT_SERVER)+"/reader/post", result);
		http_status = conn.status;
		System.out.println("status="+http_status);
		String s = conn.getContent();
		
		System.out.println("Response:\n["+ s +"]");
		System.out.println("Response header:\n["+ conn.headerText +"]");
		
		conn.close();
		conn = null;
		return s;
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
				"Accept", "*/*",
				"Client-Agent", user_agent,
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
				"Version", user_agent,
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
	public void getHttpHeaderField(HttpConnect conn) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
