package com.yazo.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.kxml2.io.KXmlParser;


public abstract class TCPChannel {

	public static final int DEFAULT_CLIENT_RECEIVE_BUFFER_SIZE = 512;

	public static final int DEFAULT_RECEIVE_TIMEOUT = 30000;

	public static final int DEFAULT_SEND_TIMEOUT =30000;


	int timeout = 0;
	String ip = "";
	int port;
	/**
	 * 用输出流进行发�?数据
	 * @param connection
	 * @param inData
	 * @return
	 * @throws Exception
	 */
	public abstract int send(OutputStream connection, byte[] inData) throws Exception;
	/**
	 * 接受响应数据为KXmlParser
	 * @param in 输入�?
	 * @param receiveBufferSize 每次接受字节�?
	 * @return
	 * @throws Exception
	 */
	public abstract KXmlParser receiveParser(InputStream in,int receiveBufferSize) throws Exception;

	/**
	 * 关闭渠道
	 */
	public abstract void close();
	/**
	 * 接收响应数据
	 * @param in
	 * @param 
	 * @return
	 * @throws IOException
	 * @throws Exception 
	 */
	public abstract byte[] receive(InputStream in) throws Exception;
	
	/**
	 *  发�?数据
	 * @param out
	 * @param input
	 * @throws Exception 
	 */
	public abstract void send(byte[] input) throws Exception;
	
	/**
	 * 获得输入�?
	 * @return
	 * @throws IOException
	 */
	public abstract OutputStream getOutputStream() throws IOException;
	/**
	 * 获得输出�?
	 * @return
	 * @throws IOException
	 */
	public abstract InputStream getInputStream() throws IOException;
	/**
	 * 进行链接服务�?
	 * @param timeout
	 * @throws Exception
	 */
	public abstract void connect(int timeout) throws Exception;
	
	public TCPChannel(String ip, int port, int timeout) {
		this.ip = ip;
		this.port = port;
		this.timeout = timeout;
	}

}
