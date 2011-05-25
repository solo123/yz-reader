package com.yazo.CMCC;

import com.yazo.model.IXmlParser;
import com.yazo.network.HttpConnect;
import com.yazo.util.Consts;
import com.yazo.util.User;

public class CmccWebSite {
	private String cookie = "";
	private String[] headers = null;
	private String service_url = null, user_agent = null;
	private String encoding, result_code, reg_code, content_length;
	public int http_status = 0;
	public CmccWebSite(String userAgent, String serviceUrl){
		user_agent = userAgent;
		service_url = serviceUrl;
		encoding = result_code = reg_code = content_length = null;
	}
	public Object post(IXmlParser parser, String action, String data){
		HttpConnect conn = new HttpConnect();
		conn.setCmccProxy();
		conn.setHttpHeader(CmccHeader(data.length()));
		conn.post(service_url+action, data);
		http_status = conn.status;
		Object r = null;
		if (parser!=null) r = parser.parse(conn.inStream);
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
	private String[] CmccHeader(int dataLength){
		String[] header = {
				"Accept", "*/*",
				"Client-Agent", user_agent,
				"x-up-calling-line-id", "13400981944",
				"user-id",  "",
				"APIVersion", "1.1.0",
				"Cookie", cookie,
				"Encoding-Type", "gzip",
				"Action", "POST",
				"ClientHash", "",
				"Version", user_agent,
				"Content-Length", "" + dataLength
		};
		return header;
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
