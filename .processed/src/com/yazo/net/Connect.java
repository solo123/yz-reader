package com.yazo.net;

import java.io.IOException;

public class Connect {
	public static final int CONNECT_HTTP = 0;

	private TCPChannel channel;
	private int timeout;
	private int connectType;
	private int port;
	private String ip;
	protected String url;
	private String headAction;
	private String methed;

	public Connect(String url, String action, String methed) {
		this(CONNECT_HTTP, "", 0);
		this.url = url;
		this.methed = methed;
		this.headAction = action;
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
			channel = new HttpChannel(ip, port, timeout);
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
	 * @return 连接实例
	 */
	public TCPChannel getTcpChannel() {
		return channel;
	}

	/**
	 * 普�?查询服务�?
	 * 
	 * @param inData
	 * @return
	 * @throws Exception
	 */
	public final byte[] queryServer(byte[] inData)  {
		byte[] b = null;
		channel = openChannel();
		try {
			((HttpChannel) channel).setUrl(this.url);
			((HttpChannel) channel).setAction(this.headAction);
			((HttpChannel) channel).setMethed(this.methed);
			channel.connect(channel.timeout);
			if (this.methed.equals("GET")) {
				if (!headAction.equals("")) {
					((HttpChannel) channel).setHttpRequestProperty(null);
				}
				((HttpChannel) channel).getHttpgetHeaderField();
			} else {
				channel.send(inData);
			}
			b = channel.receive(channel.getInputStream());
			if (b != null) {
				String alert = new String(b, "UTF-8");
				if (alert.indexOf("onenterforward") != -1) {
					// 移动收费界面
					if (alert.indexOf("wml") != -1) {

						alert = null;
						return queryServer(inData);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return b;
	}
	
	/**
	 * 通过xml查询服务�?
	 * 
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public final Object queryServerForXML(String xml) throws Exception {
		System.out.println("xml-rul:" + url + ", methed:"+ this.methed + ", headAction:" + this.headAction);
		System.out.println("xml:" + xml);
		Object obj = null;
		channel = openChannel();
		try {
			((HttpChannel) channel).setUrl(this.url);
			((HttpChannel) channel).setMethed(this.methed);
			((HttpChannel) channel).setAction(this.headAction);
			channel.connect(channel.timeout);
			((HttpChannel) channel).setHttpRequestProperty(xml);
			byte[] inData = xml.getBytes("utf-8");
			channel.send(channel.getOutputStream(), inData);
			((HttpChannel) channel).getHttpgetHeaderField();
			obj = channel.receiveParser(channel.getInputStream(),
					((HttpChannel) channel).getDataLength());

		} catch (IOException e) {
		}
		close();
		return obj;
	}

	/**
	 * 关闭通道
	 */
	public void close() {
		stopConnect(channel);
	}

	/**
	 * 关闭通道链接
	 * 
	 * @param connect
	 */
	public void stopConnect(TCPChannel connect) {
		if (connect != null) {
			connect.close();
		}
	}

}
