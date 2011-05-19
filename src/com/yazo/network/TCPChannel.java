package com.yazo.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.kxml2.io.KXmlParser;


public abstract class TCPChannel {

	public static final int DEFAULT_CLIENT_RECEIVE_BUFFER_SIZE = 512;

	public static final int DEFAULT_SERVER_RECEIVE_BUFFER_SIZE = 8192;

	public static final int DEFAULT_RECEIVE_TIMEOUT = 30000;// 20秄1�7

	public static final int DEFAULT_SEND_TIMEOUT =30000;// 20秄1�7

	public static final int DEFAULT_CONNECT_TIMEOUT = 10000;// 20秄1�7
	public static final int DEFAULT_HEART_cONNECT_TIMEOUT=10000;//10秄1�7
	public static final int DEFAULT_TEST_TIMEOUT = 3000;//测试连接超时
	public static final int SPEADDEFAULT_TEST_TIMEOUT = 1000;//测试连接超时
	public abstract int send(OutputStream connection, byte[] inData) throws AppException;
	
	public abstract KXmlParser receiveParser(InputStream in,int receiveBufferSize) throws AppException;

//	public abstract byte[] queryServer(byte[] input, int receiveBufferSize, String ip, int port) throws AppException;
	public abstract void close();
	/**
	 * 接收响应数据
	 * @param in
	 * @param 
	 * @return
	 * @throws IOException
	 * @throws AppException 
	 */
	public abstract byte[] receive(InputStream in) throws AppException;
	
	public abstract byte[] receive(InputStream in,int kb) throws AppException;

	
	/**
	 *  发送数据
	 * @param out
	 * @param input
	 * @throws AppException 
	 */
	public abstract void send(byte[] input) throws AppException;
	public abstract OutputStream getOutputStream() throws IOException;
	
	public abstract InputStream getInputStream() throws IOException;
	
	public abstract void connect(int timeout) throws AppException;
	int timeout = 0;
	String ip = "";
	int port;
	public TCPChannel(String ip, int port, int timeout) {
		this.ip = ip;
		this.port = port;
		this.timeout = timeout;
	}

}