package com.yazo.network;

import java.io.IOException;
import java.io.InputStream;

public class Connect {
	public static final int CONNECT_HTTP = 0;
	public static final int CONNECT_SOCKET = 1;
	
	private TCPChannel channel;
	private int timeout;
	private int connectType;
	private int port;
	private String ip;
	protected String url;
	private String headAction;
	private String methed;
	private int kb=0;//下载kb数量
	
	public Connect(String ip, int port) {
		this(CONNECT_SOCKET, ip, port);
	}
	/*
	 * params: url, action, method
	 */
	public Connect(String url,String action,String methed){
		this(CONNECT_HTTP,"",0);
		this.url=url;
		this.methed=methed;
		this.headAction=action;
	}
	
	public Connect(String url,String action,String methed,int kb){
		this(CONNECT_HTTP,"",0);
		this.url=url;
		this.methed=methed;
		this.headAction=action;
		this.kb=kb;
	}

	public Connect(int connect, String ip, int port) {
		connectType = connect;
		this.ip = ip;
		this.port = port;
		timeout = 3000;
	}
	public TCPChannel openChannel() {
		
		channel = null;
		switch (connectType) {
		case CONNECT_HTTP:
			channel = new HttpChannel(ip, port,timeout);
			break;
		case CONNECT_SOCKET:
			channel = new SocketChannel(ip, port,timeout);
			break;
		default:
			return null;
		}
		System.gc();
		return channel;
	}
	public int getTimeout() {
		return timeout;
	}
	/**
	 * 获取网络连接管道
	 * 
	 * @return	连接实例
	 */
	public TCPChannel getTcpChannel() {
		return channel;
	}
	public final byte[] queryServer(byte[] inData) throws AppException
	{   
		byte[] b=null;
		channel = openChannel();
		try {
			((HttpChannel)channel).setUrl(this.url);
			((HttpChannel)channel).setAction(this.headAction);
			((HttpChannel)channel).setMethed(this.methed);
			channel.connect(channel.timeout);
			if(this.methed.equals("GET")){
				if(this.headAction.equals("startGameActivate")){//					
				  ((HttpChannel)channel).setHttpstartGameActivate();
				}else if(!headAction.equals("")){
					((HttpChannel)channel).setHttpRequestProperty(null);
				}
				((HttpChannel)channel).getHttpgetHeaderField();
			}else{		
				channel.send(inData);
			}
			if(kb==0)
				b = channel.receive(channel.getInputStream());
			else
				b = channel.receive(channel.getInputStream(),kb);	
//			if(b!=null){
//				String alert=new String(b,"UTF-8");
//				if(alert.indexOf("onenterforward")!=-1){
//					//移动收费界面
//					if(alert.indexOf("wml")!=-1){
//						
//						alert=null;
//						return queryServer(inData);
//					}
//				}
//			}
		} catch (IOException e) {
			e.printStackTrace();
			//throw ErrorCodes.socketException("由于网络信号不好，连接已关闭�?);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			close();
		}
		return b;
	}
	public final InputStream queryServerForStream(){
		channel = openChannel();
		try{
			((HttpChannel)channel).setUrl(this.url);
			((HttpChannel)channel).setMethed(this.methed);
			channel.connect(channel.timeout);
//			int code=((HttpChannel)channel).getCode();
//			Consts.test=new StringBuffer();
		
			return channel.getInputStream();
		}catch(Exception e){
		}
		return null;
	}
	public boolean checkWapAlert(){
		
		return true;
	}
	public final Object queryServerForXML(String xml) throws AppException
	{   
		Object obj=null;
		channel = openChannel();
		try {
			((HttpChannel)channel).setUrl(this.url);
			((HttpChannel)channel).setMethed(this.methed);
			channel.connect(channel.timeout);
			((HttpChannel)channel).setAction(this.headAction);			
			((HttpChannel)channel).setHttpRequestProperty(xml);
			byte[] inData=xml.getBytes("utf-8");
			channel.send(channel.getOutputStream(), inData);
//			return test+"";
			((HttpChannel)channel).getHttpgetHeaderField();
			obj=channel.receiveParser(channel.getInputStream(), ((HttpChannel)channel).getDataLength());
			
		} catch (IOException e) {
			//throw ErrorCodes.socketException("由于网络信号不好，连接已关闭�?);
		}
		close();
		return obj;
	}
	
	public void close() {
		stopConnect(channel);
	}
	public void stopConnect(TCPChannel connect){
		if(connect!=null){
			connect.close();
		}
	}

}
