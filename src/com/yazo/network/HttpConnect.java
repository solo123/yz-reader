package com.yazo.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

public class HttpConnect {
	private HttpConnection connection;
	private int timeout = 30000;
	private int connectType;
	private int port = 0;
	private String ip = null;
	protected String url = null, urlDomain = null, urlPath=null;
	private String headAction = null;
	private String method = null;
	private int kb = 0;//下载kb数量
	private String proxy = null;
	private boolean useProxy = false;
	public int status = 0;
	private String[] header = null;
	private int responseLength = 0;
	public InputStream inStream = null;
	public String contentType = null;
	public String errorMessage = null;
	public String headerText = null;
	
	public HttpConnect() {
		this.url = null;
		this.method = null;
		this.headAction = null;
	}
	/*
	 * params: url, action, method
	 */
	public HttpConnect(String url,String action,String method){
		setUrl(url);
		this.method=method;
		this.headAction=action;
	}
	
	public HttpConnect(String url,String action,String method,int kb){
		setUrl(url);
		this.method=method;
		this.headAction=action;
		this.kb=kb;
	}
	public void setUrl(String url){
		String[]s = splitUrl(url);
		this.url = url;
		urlDomain = urlPath = null;
		if (s!=null) {
			urlDomain = s[0];
			urlPath = s[1];
		} else {
			errorMessage += "\nInvalid URL:" + url;
			urlDomain = urlPath = null;
		}
	}
	public void setHttpHeader(String[] header){
		this.header = header;
	}
	public String getHttpHeader(String key){
		if(connection==null) return null;
		
		try {
			return connection.getHeaderField(key);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	public void setCmccProxy(){
		useProxy = true;
		proxy = "10.0.0.172";
	}
	public void setNoProxy(){
		useProxy = false;
	}
	public void open(String url) {
		if (method==null) method = "GET";
		status = 0;
		setUrl(url);
		if (connection!=null)
			try {
				connection.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		try {
			String ref_url = this.url;
			if (useProxy) ref_url = "http://10.0.0.172:80/" + urlPath;
			connection = (HttpConnection)Connector.open(ref_url);
			connection.setRequestMethod(method);
			if(header!=null && header.length>0){
				int i = 0;
				while(i<header.length){
					connection.setRequestProperty(header[i], header[i+1]);
					i += 2;
				}
			}
			if (useProxy) connection.setRequestProperty( "X-Online-Host", urlDomain); 
			
			//TODO: check and bypass wml扣费页面
			status = connection.getResponseCode();
			if (status==HttpConnection.HTTP_OK){
				contentType = connection.getType();
				responseLength = (int)connection.getLength();
				inStream = connection.openInputStream();
			}
		} catch (SecurityException ex){
			status = -1;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void post(String url, String data) {
		method = "POST";
		status = 0;
		setUrl(url);
		if (connection!=null)
			try {
				connection.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		try {
			String ref_url = this.url;
			if (useProxy) ref_url = "http://10.0.0.172:80/" + urlPath;
			connection = (HttpConnection)Connector.open(ref_url);
			connection.setRequestMethod(method);
			if(header!=null && header.length>0){
				int i = 0;
				while(i<header.length){
					connection.setRequestProperty(header[i], header[i+1]);
					i += 2;
				}
			}
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			if (useProxy) connection.setRequestProperty( "X-Online-Host", urlDomain); 
			OutputStream oStream = connection.openOutputStream();
			oStream.write(data.getBytes("UTF-8"));
			
			//TODO: check and bypass wml扣费页面
			status = connection.getResponseCode();
			if (status==HttpConnection.HTTP_OK){
				contentType = connection.getType();
				responseLength = (int)connection.getLength();
				inStream = connection.openInputStream();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public int getTimeout() {
		return timeout;
	}
	public String getContent(){
		
		if (status != HttpConnection.HTTP_OK) return null;
		
		// Get the length and process the data
		String s = null;
		if(inStream!=null){
			try{
		        if (responseLength > 0) {
		            int actual = 0;
		            int bytesread = 0 ;
		            byte[] data = new byte[responseLength];
		            while ((bytesread != responseLength) && (actual != -1)) {
		               actual = inStream.read(data, bytesread, responseLength - bytesread);
		               bytesread += actual;
		            }
		            s = new String(data);
		        } else {
		        	byte[] data = new byte[8192];
		            int ch, i=0;
		            while ((ch = inStream.read()) != -1) {
		                data[i++] = (byte)ch;
		            }
		            s = new String(data,0,i);
		            
		            int idx = 1;
		            String key = "";
		            String value = "";
		            String content = "";
		            while ((value = connection.getHeaderField(idx)) != null) {
		              key = connection.getHeaderFieldKey(idx++);
		              content += key + ":" + value + "\n";
		            }
		            headerText = content;
	        }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return s;
	}
	
	public String getTextFromUrl(String url, boolean useProxy){
		this.useProxy = useProxy;
		this.method = "GET";
		header = new String[] {
				"Accept-Charset", "UTF-8",
				"Accept", "*/*"
				};
		open(url);
		return getContent();
	}
	public void close() {
		try{
			if(inStream!=null) inStream.close();
			if(connection!=null) connection.close();
		} catch (Exception e) {}
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

}
