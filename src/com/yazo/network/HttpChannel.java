package com.yazo.network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.kxml2.io.KXmlParser;

import com.yazo.thread.ThreadPool;
import com.yazo.thread.WaitCallback;
import com.yazo.util.AppContext;
import com.yazo.util.Consts;
import com.yazo.util.GZIP;
import com.yazo.util.MemoryStream;
import com.yazo.util.User;





public class HttpChannel extends TCPChannel{
	
	private HttpConnection hc;
	private OutputStream out;
	private InputStream in;
	byte[] dataArray=null;
	private String url="";
	private String cEncoding;
	private int length;
	private String sCookie;
	private String method=HttpConnection.GET;
	private String headerAction = "register";
	private String resultCode="";
	final String proxy = "10.0.0.172:80"; 
	 final String shema = "http://"; 
	 //是否用cnwap    
	  boolean useProxy = false; 
	 String domain; 
	 String uri; 
	  
	    
	    private String[] splitUrl(String url) {   
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
	public HttpChannel(String ip, int port, int timeout) {
		super(ip, port, timeout);
	
	}
	public void setUrl(String url){
		this.url=url;
		 
	}
	public void setUseProxy(boolean useProxy){
		this.useProxy=useProxy;
		String[] s = this.splitUrl(url);   
	    domain = s[0];   
	    uri = s[1]; 
	}
	public void setAction(String action){
		this.headerAction=action;
	}


	public void send(byte[] input) throws AppException {
		
	}
	
	/***
	 * 阅读返回对象
	 */
	public KXmlParser receiveParser(InputStream inputstream,int receiveBufferSize) throws AppException {
//		Consts.test.append(receiveBufferSize+" code "+resultCode);
//		System.out.println("rsult"+resultCode);
		if(inputstream==null ||receiveBufferSize==0){
			return null;
		}
		if(!resultCode.equals("0") && !"2022".equals(resultCode) &&!resultCode.equals("")){
			return null;
		}
		in=inputstream;
		length=receiveBufferSize;
		QueryServerData data = new QueryServerData();
		WaitCallback callback = new WaitCallback() {
			

			public void execute(Object state) {
				QueryServerData qsd = (QueryServerData)state;
				KXmlParser parser = null;
				ByteArrayInputStream bIn = null;
				try {
			        if (length > 0) {
		                byte[] totalData = new byte[length];
		                int actual = 0;
		                int bytesread = 0;
		                while ((bytesread != length) && (actual != -1)) {
		                    actual = in.read(totalData, bytesread, length - bytesread);
		                    bytesread += actual;
		                }
		               
		                if (cEncoding != null && cEncoding.equals("gzip")) {
		                    byte[] gdata = GZIP.inflate(totalData);
		                    totalData = null;
		                    bIn = new ByteArrayInputStream(gdata);
		                } else {
		                    bIn = new ByteArrayInputStream(totalData);
		                }
		                parser = new KXmlParser();
		                parser.setFeature("http://xmlpull.org/v1/doc/features.html#relaxed", true);
		                parser.setInput(bIn, null);
		            }
			        qsd.Output=parser;
					qsd.IsCompleted = true;
				} catch (Exception e) {
					qsd.Exception = ErrorCodes.connectFailed(e.getMessage());
					qsd.Output=null;
				}
			}
		};

		waitTimeout(DEFAULT_RECEIVE_TIMEOUT, callback, data);
		if(data.Output!=null){
			return (KXmlParser)data.Output;
		}else {
			return null;
		}
	
	}
	public int getCode(){
		try {
			if(hc==null){
				return -200;
			}else{
				
				return hc.getResponseCode();
			}
		} catch (Exception e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
			return -1;
		}
	}
	
	
	public byte[] receive(InputStream inputstream, final int kb) throws AppException {
		//Consts.test.append("只下载4kb");
		try {
			int code=hc.getResponseCode();
			if(code!=200){
				return null;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(inputstream==null){
			return null;
		}
		if(resultCode.equals("2017")){
			return null;
		}
		if(resultCode.equals("2023")){
			return "".getBytes();
		}
		if(!resultCode.equals("0") && !"2022".equals(resultCode) &&!resultCode.equals("")){
			return null;
		}
		in=inputstream;
		QueryServerData data = new QueryServerData();
		WaitCallback callback = new WaitCallback() {
			public void execute(Object state) {
				QueryServerData qsd = (QueryServerData)state;
				try {
					byte[] buffer = new byte[1024*kb];
					MemoryStream stream = new MemoryStream();
					int count = 0;
					if ( (count = in.read(buffer)) > 0) {
						stream.write(buffer, 0, count);
					}
					qsd.Output=stream.toArray();
					qsd.IsCompleted = true;
				} catch (Exception e) {
					qsd.Exception = ErrorCodes.connectFailed(e.getMessage());
					qsd.Output=null;
				}
			}
		};

		waitTimeout(DEFAULT_RECEIVE_TIMEOUT, callback, data);
		if(data.Output!=null){
			return (byte[])data.Output;
		}else{
			return null;
		}
	}
	/**
	 * 普通接收数据
	 */
	public byte[] receive(InputStream inputstream) throws AppException {
//		Consts.test.append(" code "+resultCode);
		try {
			int code=hc.getResponseCode();
			if(code!=200){
				return null;
			}
		} catch (Exception e1) {
			// TODO 自动生成 catch 块
//			Consts.test.append("异常");
			e1.printStackTrace();
		}
		if(inputstream==null){
			return null;
		}
		if(resultCode.equals("2017")){
			return null;
		}
		if(resultCode.equals("2023")){
			return "".getBytes();
		}
//		System.out.println("rsult"+resultCode);
		if(!resultCode.equals("0") && !"2022".equals(resultCode) &&!resultCode.equals("")){
			return null;
		}
		in=inputstream;
		QueryServerData data = new QueryServerData();
		WaitCallback callback = new WaitCallback() {
			public void execute(Object state) {
				QueryServerData qsd = (QueryServerData)state;
				try {
					byte[] buffer = new byte[DEFAULT_CLIENT_RECEIVE_BUFFER_SIZE];
					MemoryStream stream = new MemoryStream();

					int count = 0;
					while ( (count = in.read(buffer)) > 0) {
						stream.write(buffer, 0, count);
						count++;
					}
//					dataArray= stream.toArray();
					qsd.Output=stream.toArray();
					qsd.IsCompleted = true;
				} catch (Exception e) {
					qsd.Exception = ErrorCodes.connectFailed(e.getMessage());
					qsd.Output=null;
				}
			}
		};

		waitTimeout(DEFAULT_RECEIVE_TIMEOUT, callback, data);
		if(data.Output!=null){
			return (byte[])data.Output;
		}else{
			return null;
		}
	}

	public void close() {
			try {
				if(hc!=null){					
					hc.close();
					hc=null;
				}
				if(out!=null){
					out.close();
					out=null;
				}
				if(in!=null){
					in.close();
					in=null;
				}
				dataArray=null;
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public void connect(int timeout) throws AppException {
		System.out.println("HttpChannel connect:"+url);
		QueryServerData data = new QueryServerData();
		WaitCallback callback = new WaitCallback() {
			public void execute(Object state) {
				QueryServerData qsd = (QueryServerData) state;
				try {
					if(!AppContext.getInstance().isCMNET()){
						setUseProxy(true);
						System.out.println("使用代理：http://" + proxy + uri);
						hc=(HttpConnection)Connector.open("http://" + proxy + uri, Connector.READ_WRITE, true);	
//						if(hc==null){
//							AppContext.getInstance().setIsCMNET(true);
//							hc=(HttpConnection)Connector.open(url, Connector.READ_WRITE, true);
//						}else{
							
							hc.setRequestProperty("X-Online-Host",domain);		
//						}
					}else{
						hc=(HttpConnection)Connector.open(url, Connector.READ_WRITE, true);
//						if(hc==null){
//							AppContext.getInstance().setIsCMNET(false);
//							setUseProxy(true);
//							hc=(HttpConnection)Connector.open("http://" + proxy + uri, Connector.READ_WRITE, true);						
//							hc.setRequestProperty("X-Online-Host",domain);	
//						}
					}
					hc.setRequestMethod(method);
					hc.setRequestProperty("Accept-Charset", "UTF-8");
					qsd.IsCompleted=true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		waitTimeout(timeout, callback, data);
	}
	public void setMethed(String methed){
		this.method=methed;
	}

	/**
	 * 扣费消息头
	 */
	public void setHttpstartGameActivate(){
		try{
			
//			hc.setRequestMethod(method);
			hc.setRequestProperty("Cache-Control", "no-store");
			hc.setRequestProperty("Connection", "Keep-Alive");
			hc.setRequestProperty("Proxy-Connection", "Keep-Alive");
			hc.setRequestProperty("Accept", "*/*");
			hc.setRequestProperty("Content-Type", "*/*");
			hc.setRequestProperty("Client-Agent",  Consts.strUserAgent);
		}catch(Exception e){
			
		}
	}
	
	public void setHttpRequestProperty(String xml){
		try{
			
//			hc.setRequestMethod(this.method);
			
			
			hc.setRequestProperty("Accept", "*/*");
			
			hc.setRequestProperty("Client-Agent", Consts.strUserAgent);
			
			hc.setRequestProperty("x-up-calling-line-id", "13706718460");
			
			hc.setRequestProperty("user-id", User.userId != null ? User.userId : "");
			
			hc.setRequestProperty("APIVersion", "1.1.0");
			
			if(sCookie!=null){
				hc.setRequestProperty("Cookie", sCookie);
			}
			
			hc.setRequestProperty("Encoding-Type", "gzip");// gzip
			
			hc.setRequestProperty("Action", headerAction);
			
			hc.setRequestProperty("ClientHash", "");
			
			hc.setRequestProperty("Version", Consts.strUserAgent);
			
			if (method.equals(HttpConnection.POST)) {
//				hc.setRequestProperty("Content-Type", "application/xml");
				hc.setRequestProperty("Content-Length", "" + xml.length());
			}
		}catch(Exception e){
			
		}
	}
	public void setHttpRequestProperty2(String xml){
		try{
			hc.setRequestMethod(this.method);
			hc.setRequestProperty("Cache-Control", "no-store");
			hc.setRequestProperty("Connection", "Keep-Alive");
			hc.setRequestProperty("Proxy-Connection", "Keep-Alive");
			hc.setRequestProperty("Accept", "*/*");
			hc.setRequestProperty("Content-Type", "*/*");
			if (method.equals(HttpConnection.POST)) {
//				hc.setRequestProperty("Content-Type", "application/xml");
				hc.setRequestProperty("Content-Length", "" + xml.length());
			}
		}catch(Exception e){
			
		}
	}
	public int getDataLength(){
		return length;
	}
	public void getHttpgetHeaderField(){
		try{
			   cEncoding = hc.getHeaderField("Encoding-Type");
			   
	           resultCode = hc.getHeaderField("Result-Code");	
	           Consts.test.append("Result-Code:"+resultCode+" \n");
	            resultCode = (resultCode == null ? "0" : resultCode);
	            
	           String  cLength = hc.getHeaderField("Content-Length");
	           length = Integer.parseInt(cLength.trim());
	           
	            String cook = hc.getHeaderField("Set-Cookie");
	            sCookie = cook==null?sCookie:cook;
	            
	            User.regCode = hc.getHeaderField("RegCode");
	            if(resultCode.equals("4003")){	            	
	            	Consts.HOSTURL=hc.getHeaderField("Request-URL");
	            }
	            
	            String location_path = hc.getHeaderField("Location");
	            location_path = location_path==null?"":location_path;
	            if(!"".equals(location_path)){
	            	Consts.LOCATION_PATH=location_path;
	            }
	            
		}catch(Exception e){
			
		}
	}
	public String getResultCode(){
		return resultCode;
	}
	public InputStream getInputStream() throws IOException {
		in=hc.openInputStream();
		return in;
	}

	public OutputStream getOutputStream() throws IOException {
		out=hc.openOutputStream();
		return out;
	}

	public int send(OutputStream connection, byte[] inData) throws AppException {
		dataArray=inData;
//		out=connection;
//		Consts.test.append("out"+out+" "+inData);
		if (connection == null || inData == null) {
			throw ErrorCodes.connectFailed("发送的数据为null");
		}
		
			QueryServerData data = new QueryServerData();
			WaitCallback callback = new WaitCallback() {
				public void execute(Object state) {
					QueryServerData qsd = (QueryServerData) state;
					try {
						
						out.write(dataArray);
//						Consts.test.append("code"+code);
//						out.flush();
						qsd.Output=dataArray.length+"";
						dataArray=null;
						qsd.IsCompleted = true;
					} catch (IOException e) {
						e.printStackTrace();
						qsd.Exception = ErrorCodes.connectFailed(e.getMessage());
					}
					catch(Exception ex){
						//System.out.println("null2");
						ex.printStackTrace();
					}
//					Consts.test.append(qsd.IsCompleted+" "+qsd.Output.toString());
				}
			};
			waitTimeout(DEFAULT_SEND_TIMEOUT, callback, data);
			if(data.Output!=null){
				return Integer.parseInt((String)data.Output);
			}else{
				return 0;
			}
	}
		
	

	private void waitTimeout(int timeout, WaitCallback callback, QueryServerData data) {
		try{
			ThreadPool.queueWorkItem(callback, data);
		}catch(Exception e)
		{
			System.out.println("exception");
		}
		
		int timeElapsed = 0;
		while (timeElapsed < timeout && !data.IsCompleted) {
			try {
				Thread.sleep(100);
				timeElapsed += 100;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (timeElapsed >= timeout) {
	
			System.out.println( "HttpChannel waitTimeOut:" + timeElapsed + " timeout");
			data.Exception = ErrorCodes.timeout();
			close();
			System.gc();
		 }
	}

}
