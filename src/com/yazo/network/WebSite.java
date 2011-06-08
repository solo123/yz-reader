package com.yazo.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.io.Connector;
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
 * 网站操作
 * 
 * 参数：
 * （必须）site_url
 * （可选）use_cmcc_proxy
 */
public class WebSite {
	public boolean use_cmcc_proxy;
	public int http_status;
	public int error_code;
	public String error_message;
	public Hashtable response_headers;
	private static final String cmcc_proxy = "http://10.0.0.172:80/";
	private String cookie;
	private String response_encoding;
	private int response_content_length;
	
	public WebSite(){
		use_cmcc_proxy = false;
		http_status = 0;
		cookie = "";
		response_encoding = null;
		response_content_length = 0;
		error_code = 0;
		error_message = null;
		response_headers = null;
	}
	
	public byte[] post(String url, String[] header, String data){
		byte[] inData = null;
		error_code = 0;
		String url_domain = null, url_path = null;
		if(use_cmcc_proxy){
			String[] ss = splitUrl(url);
			if (ss==null || ss[0]==null || ss[1]==null){
				error_code = 100;
				http_status = -1;
				error_message = "非法url:" + url;
				return null;
			}
			url_domain = ss[0];
			url_path = ss[1];
		}
		
		HttpConnection conn = null;
		try{
			if(use_cmcc_proxy){
				conn = (HttpConnection)Connector.open(cmcc_proxy + url_path);
				conn.setRequestProperty("X-Online-Host", url_domain);
			}else
				conn = (HttpConnection)Connector.open(url);
			conn.setRequestMethod(HttpConnection.POST);
		} catch (Exception e){
			// #ifdef DBG
			e.printStackTrace();
			// #endif
		}
		if (conn==null){
			error_code = 102;
			error_message = "打开URL失败！" + url;
			return null;
		}
		
		try{
			if(header!=null && header.length>0){
				int i = 0;
				while(i<header.length){
					conn.setRequestProperty(header[i], header[i+1]);
					i += 2;
				}
			}
			conn.setRequestProperty("Content-Length", "" + data.length());
		} catch (Exception e){
			error_code = 103;
			error_message = "设置http header出错。";
			try {
				conn.close();
			} catch (IOException e1) {}
			conn = null;
			return null;
		}
		
		try{
		OutputStream oStream = conn.openOutputStream();
		oStream.write(data.getBytes("UTF-8"));
		oStream.flush();
		//oStream.close();
		} catch (Exception e){
			error_code = 104;
			error_message = "写入post data出错。";
			try {
				conn.close();
			} catch (IOException e1) {}
			conn = null;
			return null;
		}
		
		//TODO: check and bypass wml扣费页面
		
		
		try {
			http_status = conn.getResponseCode();
		} catch (IOException e) {
			error_code = 105;
			http_status = -1;
			error_message = "无法取得response code.";
			try {
				conn.close();
			} catch (IOException e1) {}
			return null;
		}
		
		if (http_status==HttpConnection.HTTP_OK){
			try{
	            // read response header
				int h_idx = 1;
	            String h_key = "";
	            String h_val = "";
	            response_headers = new Hashtable();
	            while ((h_val = conn.getHeaderField(h_idx)) != null) {
	            	h_key = conn.getHeaderFieldKey(h_idx++);
	            	response_headers.put(h_key.toLowerCase(), h_val);
	            }
	            cookie = getHttpHeader(conn, "Cookie");
	            response_encoding = getHttpHeader(conn, "Encoding-Type");
	            String l = getHttpHeader(conn, "Content-Length");
	            if (l!=null){
	            	try{
	            		response_content_length = Integer.parseInt(l);
	            	} catch (Exception e){
	            		e.printStackTrace();
	            		response_content_length = 0;
	            	}
	            }
	            
	            // read data
	            inData = getContent(conn.openInputStream());
			} catch (Exception e){
				error_code = 106;
				error_message = "无法读取response信息。";
				try {
					conn.close();
				} catch (IOException e1) {}
				return null;
			}
		}
		
		try{
			conn.close();
		} catch (Exception e){}
		conn = null;
		return inData;
	}
	public String getHttpHeader(HttpConnection conn, String key){
		if(conn==null) return null;
		
		try {
			return conn.getHeaderField(key);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
public byte[] getContent(InputStream inStream){
		byte[] s = null;
		byte[] result = null;
		if(inStream!=null){
			try{
		        if (response_content_length > 0) {
		            int actual = 0;
		            int bytesread = 0 ;
		            s = new byte[response_content_length];
		            while ((bytesread != response_content_length) && (actual != -1)) {
		               actual = inStream.read(s, bytesread, response_content_length - bytesread);
		               bytesread += actual;
		            }
		        } else {
		        	byte[] data = new byte[8192];
		            int ch, i=0;
		            while ((ch = inStream.read()) != -1 && i<8192) {
		                data[i++] = (byte)ch;
		            }
		            s = new byte[i];
		            for(int j=0; j<i; j++) s[j] = data[j];
		        }
	        
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (response_encoding!=null && response_encoding.equals("gzip")){
			try {
				result = GZIP.inflate(s);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else result = s;
		return result;
	}
	
	private String[] splitUrl(String url) {
		String shema = "http://";
        String[] urls = new String[2];   
        int shemaLen = shema.length();   
        int posStart = url.toLowerCase().indexOf(shema);   
        int posEnd;   
        if (posStart == -1) {   
          return null; //throw new Exception( "no http schema" );    
        }   
        posEnd = url.indexOf("/", shemaLen);   
        if (posEnd == -1) {   
          urls[0] = url.substring(shemaLen, url.length());   
          urls[1] = "/";   
        } else {   
          urls[0] = url.substring(shemaLen, posEnd);   
          urls[1] = url.substring(posEnd);   
        }   
        return urls;   
      }   
	
	public String getErrorMessage(){
		if (error_code>0){
			return "Error["+ error_code +"]:["+ (error_message!=null ? error_message : "") +"]";
		} else
			return "ok";
	}
	
}
