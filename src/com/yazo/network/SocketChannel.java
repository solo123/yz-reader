package com.yazo.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.SocketConnection;

import org.kxml2.io.KXmlParser;

import com.yazo.thread.ThreadPool;
import com.yazo.thread.WaitCallback;
import com.yazo.util.AppContext;
import com.yazo.util.Consts;
import com.yazo.util.MemoryStream;


public class SocketChannel extends TCPChannel {
	public SocketChannel(String ip, int port, int timeout) {
		super(ip, port, timeout);
	}

	public UUID SessionID;

	public String ServerIP;

	public int ServerPort;
	
	private OutputStream out;
	private InputStream in;
	private SocketConnection server;
	private HttpConnection hc;
	public String serverUrl=null;
	private String requestUrl=null;
	private String serverName=null;
	
 	public boolean stop;
	
	boolean returnValue=false;
	public static int camerastate=0;//0为普逄1�7   1为拍照上伄1�7  
	private int imagesendtime=60000;

	
    public void setOutput(OutputStream out)
    {
    	this.out=out;
    }
    
	
	public void getConnection( String ip,  int port) {
		QueryServerData data = new QueryServerData();
		WaitCallback callback = new WaitCallback() {
			public void execute(Object state) {
				QueryServerData qsd = (QueryServerData) state;
				if(AppContext.getInstance().isHTTPMode()){
					try{
						serverUrl=ServerIP+":"+ServerPort;
						if (AppContext.getInstance().isCMNET()) {
							try{
								byte[]b = BitConverter.getBytes(getData().length+4);
								int length=getData().length+b.length;
								hc = (HttpConnection) Connector.open("http://"+serverUrl,Connector.READ_WRITE, true);
								hc.setRequestMethod(HttpConnection.POST);
								hc.setRequestProperty("User-Agent","Profile/MIDP-2.0 Configuration/CLDC-1.0");
								hc.setRequestProperty("Content-length",length+"");
								hc.setRequestProperty("Accept", "*/*");
								out = hc.openOutputStream();
								System.out.println("serverUrl:====>"+serverUrl);							
								System.out.println("Content-length:====>"+length);
							}catch(Exception e)
							{
								System.out.print("http null3");
								System.out.print(e.getMessage()+"ex");
								httpClose();
								stop=true;
							}
							
							try{
								send2(getData());
								int code = hc.getResponseCode();
								if(!stop&&(code== HttpConnection.HTTP_OK)){	
									in = hc.openInputStream();																				
									clearData();
								}
								System.out.print("http+++++++++++++++++++++++");
							}catch(Exception e)
							{
								
								System.out.println(e.getMessage());
								System.out.println("http error");
							}
							
						}else
						{
							try{
								
								byte[]b = BitConverter.getBytes(getData().length+4);
								int length=getData().length+b.length;
									hc = (HttpConnection) Connector.open("http://10.0.0.172:80",Connector.READ_WRITE,true);
									hc.setRequestProperty("X-Online-Host",serverUrl);
									hc.setRequestMethod(HttpConnection.POST);
									hc.setRequestProperty("User-Agent","Profile/MIDP-2.0 Configuration/CLDC-1.0");
									hc.setRequestProperty("Connection", "Keep-Alive");
									hc.setRequestProperty("Content-length",length+"");
									hc.setRequestProperty("Accept", "*/*");
									out = hc.openOutputStream();
			
							}catch(Exception e)
							{
								httpClose();
								System.out.println("http null1");
								System.out.println("http ex"+e.getMessage());
								stop=true;
							}
							try{
								send2(getData());
							}catch(Exception e)
							{
								e.printStackTrace();
							}
							
							try{							
								int code = hc.getResponseCode();
			
								if((code== HttpConnection.HTTP_OK)){								
									in = hc.openInputStream();
									clearData();
								}
							}catch(Exception e)
							{
								System.out.println(e.getMessage());
								System.out.println("http error1");
							}
					
						}					
						if(stop)
							qsd.IsCompleted = false;
						else
						qsd.IsCompleted = true;
										
						}catch(Exception e)
						{
							System.out.println("ex "+e.getMessage());
							qsd.Exception = ErrorCodes.connectFailed(e.getMessage());
							httpClose();
							System.out.println("http null2");	
						}
				}else{
					
					try {
						if (AppContext.getInstance().isCMNET()) {
							try{							
								server = (SocketConnection) Connector.open("socket://"+ ServerIP + ":" + ServerPort,Connector.READ_WRITE,true);
							}catch(Exception e)
							{
								System.out.print("null3");
								close(true);
								stop=true;
							}	
							
							if(!stop){								
								out = server.openOutputStream();
								in = server.openInputStream();
							}
							
						} else {
							
							try{
								server = (SocketConnection) Connector.open("socket://10.0.0.172:80",Connector.READ_WRITE,true);
							}catch(Exception e)
							{
								close(true);
								System.out.println("null1");
								System.out.println("ex"+e.getMessage());
								stop=true;
							}
							String s = "CONNECT " + ServerIP + ":" + ServerPort + " HTTP/1.1\r\nHost:" + ServerIP + ":" + ServerPort + "\r\nProxy-Connection:Keep-Alive\r\n\r\n";
							if(!stop){							
								out = server.openOutputStream();
								in = server.openInputStream();
								out.write(s.getBytes());
								out.flush();
								byte[] data = new byte[2048];
								for(int i=0;i<2048;i++)
								{
									data[i]=(byte)in.read();
									//System.out.println(data[i]);
									if(data[i]==10&&data[i-1]==13&&data[i-2]==10&&data[i-3]==13)
										break;
								}
							}
						}
						if(stop){
							
							qsd.IsCompleted = false;
						}
						else{
							
							qsd.IsCompleted = true;
						}
						
						
					} catch (Exception e) {
						System.out.println("ex "+e.getMessage());
						qsd.Exception = ErrorCodes.connectFailed(e.getMessage());
						close(true);
						System.out.println("null2");			
					}
				}
			}
		};
			waitTimeout(DEFAULT_CONNECT_TIMEOUT, callback, data);
	}
	
	
	public void getHttpConnect()
	{
		QueryServerData data = new QueryServerData();		
		WaitCallback callback = new WaitCallback() {
			public void execute(Object state) {
				QueryServerData qsd = (QueryServerData) state;
				
				try{
					if (AppContext.getInstance().isCMNET()) {
						try{
							hc = (HttpConnection) Connector.open(serverUrl,Connector.READ, false);
							hc.setRequestMethod(HttpConnection.GET);
							hc.setRequestProperty("User-Agent","Profile/MIDP-2.0 Configuration/CLDC-1.0");
							hc.setRequestProperty("Accept", "*/*");
							System.out.print("http");
						}catch(Exception e)
						{
							System.out.print("http null3");
							httpClose();
							stop=true;
						}
						try{
							int code = hc.getResponseCode();
							if(!stop&&(code== HttpConnection.HTTP_OK)){								
								in = hc.openInputStream();
							}			
						}catch(Exception e)
						{
							System.out.println(e.getMessage());
							System.out.println("http error");
						}
					}else
					{
						try{
								parseServerName(serverUrl);
								hc = (HttpConnection) Connector.open("http://10.0.0.172:80"+requestUrl,Connector.READ, false);
								hc.setRequestMethod(HttpConnection.GET);
								 hc.setRequestProperty("X-Online-Host", serverName);								 
								 hc.setRequestProperty("User-Agent","Profile/MIDP-2.0 Configuration/CLDC-1.0");
								hc.setRequestProperty("Accept", "*/*");
						}catch(Exception e)
						{
							httpClose();
							System.out.println("http null1");
							System.out.println("http ex"+e.getMessage());
							stop=true;
						}
						try{							
							int code = hc.getResponseCode();
							if(!stop&&(code== HttpConnection.HTTP_OK)){								
								in = hc.openInputStream();
							}
						}catch(Exception e)
						{
							System.out.println(e.getMessage());
							System.out.println("http error1");
						}
				
					}					
					if(stop)
						qsd.IsCompleted = false;
					else
					qsd.IsCompleted = true;
									
				}catch(Exception e)
				{
					System.out.println("ex "+e.getMessage());
					qsd.Exception = ErrorCodes.connectFailed(e.getMessage());
					httpClose();
					System.out.println("http null2");	
				}
			}
		};
		waitTimeout(DEFAULT_CONNECT_TIMEOUT, callback, data);
	}
	
	public byte[] httpReceive() throws Exception
	{
		if(in==null)
		{
			return null;
		}
		byte[] receive =null;
				try {
					byte[] buffer = new byte[DEFAULT_CLIENT_RECEIVE_BUFFER_SIZE];
					MemoryStream stream = new MemoryStream();
					int count = 0;
					while ((count = in.read(buffer)) > 0) {
						stream.write(buffer, 0, count);
					}
					receive= stream.toArray();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return receive;
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
	
			System.out.println( timeElapsed + " timeout");
			data.Exception = ErrorCodes.timeout();
			if(server!=null)
			this.close(false);	
			else if(hc!=null)
				httpClose();
			System.gc();
		 }
	}
	
	public void alert(){
	}
	
	
	public byte[] queryServer(byte[] input) throws AppException {
		if(AppContext.getInstance().isHTTPMode()){
			setData(input);
		}
		getConnection(ServerIP, ServerPort);	
		if(!stop)
		{
			if(!AppContext.getInstance().isHTTPMode()){
				send(input);
			}			
			byte[] receive =null;
			try{
			 receive = receive2(DEFAULT_CLIENT_RECEIVE_BUFFER_SIZE);
			}catch(Exception e)
			{
				close(true);
				System.out.println("dddd");
				return null;
			}
			if(ServerPort!=Consts.HEARTBEAT_PORT)
			{					
				close(false);
//				System.out.println("bbbbbbb");
				return receive;
			}
			
		}
		//stop=false;
		return null;
	}
	
	public byte[] httpQueryServer(byte[] input) throws AppException 
	{
		setData(input);
         try{			
		      getConnection(ServerIP, ServerPort);
				byte[] receive =null;
				try{
					receive = receive();
					System.out.println("reveive data========"+receive);
				}catch(Exception e)
				{
					httpClose();
					return null;
				}
				
				return receive;
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		finally{
			httpClose();
		}
		//return null;
	}
	
	public byte[] queryServer(byte[] input, int receiveBufferSize) throws AppException {
		getConnection(ServerIP, ServerPort);
		send(input);
		byte[] receive =null;
		try{
			 receive = receive(receiveBufferSize);
		}catch(Exception e)
		{
			close(true);
			
			return null;
		}
		
		if(ServerPort!=Consts.HEARTBEAT_PORT)
		{	//如果不是心跳信息			
			close(false);
			return receive;
		}
		return null;
	}
	
	public byte[] receive() throws AppException
	{
		return receive(DEFAULT_CLIENT_RECEIVE_BUFFER_SIZE);
	}

	public byte[] receive( int receiveBufferSize) throws AppException {
		if(in==null)
		{
			return null;
		}
		QueryServerData data = new QueryServerData();
		WaitCallback callback = new WaitCallback() {
			public void execute(Object state) {
				QueryServerData qsd = (QueryServerData)state;
				try {
					byte[] buffer = new byte[DEFAULT_CLIENT_RECEIVE_BUFFER_SIZE];
					MemoryStream stream = new MemoryStream();
					byte[] bufTotalLength = new byte[4];
					int count = in.read(bufTotalLength);

					if (count != bufTotalLength.length)
						return;

					int total = BitConverter.toInt32(bufTotalLength);
					int temp = count;
					while (temp < total && (count = in.read(buffer)) > 0) {
						stream.write(buffer, 0, count);
						temp += count;
					}
					byte[] receive = stream.toArray();
					qsd.IsCompleted = true;
				} catch (Exception e) {
					qsd.Exception = ErrorCodes.connectFailed(e.getMessage());
					qsd.Output=null;
				}
			}
		};

		waitTimeout(DEFAULT_RECEIVE_TIMEOUT, callback, data);
		
		if(data.Output!=null){
			byte[] b = (byte[]) data.Output;
			return b;
		}else{
			return null;
		}
	}

	public byte[] receive2() throws AppException
	{
		return receive2(DEFAULT_CLIENT_RECEIVE_BUFFER_SIZE);
	}

	public byte[] receive2( int receiveBufferSize) throws AppException {
		if(in==null)
		{
			return null;
		}
		
		QueryServerData qsd = new QueryServerData();
		try {
			byte[] bufTotalLength = new byte[4];
     
			for(int i=0;i<4;i++)
			{
				if(stop)
					break;	
				bufTotalLength[i]=(byte)in.read();							
			}
			

			int total = BitConverter.toInt32(bufTotalLength);
			int temp = 0;
			byte[] receive = new byte[total-4];
			for(int i=0;i<(total-4);i++)
			{
				if(stop)
					break;
				temp++;
				byte tempbyte=(byte)in.read();				
				receive[i]=tempbyte;
				
			}
			qsd.IsCompleted = true;
		} catch (Exception e) {
			qsd.Exception = ErrorCodes.connectFailed(e.getMessage());
			return (byte[]) qsd.Output;
		}
		return (byte[]) qsd.Output;
	}

public byte [] data=null;
	
	public byte[] getData()
	{
		return data;
	}
	
	public void clearData()
	{
		data=null;
	}
	public void setData(byte[] inputData)
	{
		
		data=inputData;
	}
	public void send(byte[] inputData) throws AppException {		
		setData(inputData);
//		System.out.println();
		if (out != null && inputData != null) {
			QueryServerData data = new QueryServerData();
			WaitCallback callback = new WaitCallback() {
				public void execute(Object state) {
					QueryServerData qsd = (QueryServerData) state;
					try {
						byte[]b = BitConverter.getBytes(getData().length+4);
						out.write(b);
						out.write(getData());
						qsd.IsCompleted = true;
						out.flush();
						clearData();
					} catch (IOException e) {
						e.printStackTrace();
						qsd.Exception = ErrorCodes.connectFailed(e.getMessage());
					}
					catch(Exception ex){
						//System.out.println("null2");
						ex.printStackTrace();
					}
				}
			};
			waitTimeout(DEFAULT_SEND_TIMEOUT, callback, data);
		}
	}
	
	public void send2(byte[] inputData) throws IOException {
		 byte[] input = inputData;		 
		byte[]b = BitConverter.getBytes(input.length+4);
		out.write(b);
		out.write(input);
		out.flush();
	}
	
	public void close(boolean flag)
	{
		try {
			if(out!=null)
			{
				if(flag)
				{
					out.write("*#*\n".getBytes());
					out.flush();
				}				
				out.close();			
			}
			if(in!=null)
			in.close();
			if(server!=null)
			server.close();
			if(hc!=null)
				hc.close();
			out = null;
			in = null;
			server = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public void httpClose()
	{
		try{
					if(out!=null)
					out.close();
					if(in!=null)
					in.close();
					if(hc!=null)
						hc.close();
					in=null;
					out=null;
					hc=null;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public boolean isnet=false;
	public int conntime=0;
	private void parseServerName(String s)
	{
		String url=null;
		int num=0;
		if((num=s.indexOf("//"))>0)
			url=s.substring(num+2,s.length());
		if((num=url.indexOf("/"))>0)
		{
			
			requestUrl=url.substring(num, url.length());
			serverName=url.substring(0, num);
		}
	}

	public void setIn(InputStream in) {
		this.in = in;
	}

    
	private  boolean isOkconnect=false;
	private  boolean iscompleted=false;
	public boolean connect(boolean Isnet)
    {
		    	this.isnet=Isnet;
		    	iscompleted=false;
						try {
							    tryConnect(isnet);
					        	isOkconnect=true;
					        } catch (Throwable e) {
					        	isOkconnect=false;
					        	System.out.println(e.toString());
					        } finally {
					        	closeConnection();
					        }
					        iscompleted=true;  
				return isOkconnect;
		    
	}
	private DataInputStream httpdis = null;
    private DataOutputStream httpdos = null;
		   /**
		     * 尝试连接网络.
		     * @param originalUrl
		     *  @param isProxy   true表示cmwap连接false表示cmnet连接
		     * @throws Throwable
		     */
		    private void tryConnect(boolean isnet) throws Throwable {
		    	
		    	String originalUrl="http://wap.baidu.com:80";
		    	String hostName, servlet, url;
		        if (originalUrl.toLowerCase().indexOf("http://") < 0) {
		            originalUrl = "http://" + originalUrl;
		        }
		        int pos1 = "http://".length();
		        int pos2 = originalUrl.indexOf('/', pos1);
		        if (pos2 < 0) {
		            hostName = originalUrl.substring(pos1);
		            servlet = "";
		        } else {
		            hostName = originalUrl.substring(pos1, pos2);
		            servlet = originalUrl.substring(pos2);
		        }
		        int pos3 = hostName.indexOf(":");

		        if (pos3 < 0) {
		            hostName += ":80";
		        }
		       if (!isnet) {
							int code=200;
							int cmwapCode=300;
							try{
								hc = (HttpConnection) Connector.open("http://10.0.0.172:80",Connector.READ_WRITE,true);
								
								hc.setRequestProperty("X-Online-Host","wap.baidu.com:80");
								hc.setRequestMethod(HttpConnection.GET);									
								hc.setRequestProperty("User-Agent","Profile/MIDP-2.0 Configuration/CLDC-1.0");
             //                   hc.setRequestProperty("Connection", "Keep-Alive");
								//hc.setRequestProperty("Content-length",length+"");
								hc.setRequestProperty("Accept", "*/*");
								
								out=hc.openOutputStream();								
								out.write("helloworld".getBytes());
								out.flush();
								
							    code=hc.getResponseCode();
							}catch(ConnectionNotFoundException e)
		                    {
								throw new Exception("e99");
		                   
		                    }catch(Exception e)
		                    {
		                    	 if("e99".equals(e.toString().trim()))
		                    	 {
		                    		 throw new Exception("song");
		                    	 }
		                    	 else if("java.io.IOException: GET and HEAD requests can't include an entity body".trim().equals(e.toString().trim()))
		                    	 {
			                    	 closeConnection();
									 try{
										
										 hc = (HttpConnection) Connector.open("http://"+hostName + servlet,Connector.READ_WRITE,true);
									     hc.setRequestMethod(HttpConnection.POST);
										 hc.setRequestProperty("X-Online-Host", "wap.baidu.com:80");
										 hc.setRequestProperty("User-Agent", "Profile/MIDP-2.0 Configuration/CLDC-1.0");
										 hc.setRequestProperty("Accept", "*/*");
										
										 out=hc.openOutputStream();
										 out.write("hello".getBytes());
										 out.flush();
										 cmwapCode=hc.getResponseCode(); //cmwap 用cmnet连接正常  说明这个是cmnet  返回false＄1�7
										 
									 }catch(Exception ex){
										 //cmwap 用cmnet连接不正帄1�7  说明这个是cmwap  返回true＄1�7
									 }
		                    	 }else
		                    	 {
		                    		 throw new Exception("song");
		                    	 }
		                    	
		                    	
		                    }
							if(cmwapCode==200) //cmwap 用cmnet连接正常  说明这个是cmnet  返回false＄1�7 
							{
								throw new Exception("HttpConnection Cannot be set null");
							}
							
		                    if(code!=200)
		                    {
		                    	throw new Exception("HttpConnection Cannot be set null");
		                    }
								
		        } else {  //cmnet
		           
		        	url = "socket://" +Consts.SERVER_IP+":"+Consts.SERVER_PORT; 
		            server = (SocketConnection) Connector.open(url,Connector.READ_WRITE,true);
		            if (server != null) {
			            httpdis=server.openDataInputStream();
		    			httpdos=server.openDataOutputStream();
			        } else {
			            throw new Exception("SocketConnection Cannot be set null");
			        }
		       	 
		        }
		    }
		    private void closeConnection(){
		        if (httpdis != null) {
		            try {
		                httpdis.close();
		                httpdis = null;
		            } catch (Throwable e) {
		                e.printStackTrace();
		            }
		        }
		        if (httpdos != null) {
		            try {
		                httpdos.close();
		                httpdos = null;
		            } catch (Throwable e) {
		                e.printStackTrace();
		            }
		        }
		        if(out!=null)
		        {
		        	try
		        	{
		        		out.close();
		        		out=null;
		        	}catch(Throwable e)
		        	{
		        		e.printStackTrace();
		        	}
		        }
		        if(hc!=null)
		        {
		        	try {
		        		hc.close();
		        		hc = null;
		            } catch (Throwable e) {
		                e.printStackTrace();
		            }
		        }
		        if (server != null) {
		            try {
		            	server.close();
		            	server = null;
		            } catch (Throwable e) {
		                e.printStackTrace();
		            }
		        }
		    }
			public byte[] httpQueryMap()
			{
		        
				try{			
					getHttpConnect();	
					if(!stop)
					{
						//send(null);
						byte[] receive =null;
						try{
							receive = map_receive();
							if(receive!=null)
							System.out.println("image data========"+receive);
						}catch(Exception e)
						{
							httpClose();
							return null;
						}
						
						return receive;
						//}
						
					}
					
				}catch(Exception e){
					e.printStackTrace();
					return null;
				}
				finally{
					httpClose();
				}
				return null;
			}
			
			public byte[] map_receive()
			{
				if(in==null)
				{
					
					return null;
				}
				QueryServerData data = new QueryServerData();
				WaitCallback callback = new WaitCallback() {
					public void execute(Object state) {
						QueryServerData qsd = (QueryServerData)state;
						try {
							byte[] receive =null;
							byte[] buffer = new byte[DEFAULT_CLIENT_RECEIVE_BUFFER_SIZE];
							MemoryStream stream = new MemoryStream();
							int count = 0;
							while ((count = in.read(buffer)) > 0) {
								stream.write(buffer, 0, count);
							}
							receive= stream.toArray();
							qsd.IsCompleted = true;
						} catch (Exception e) {
							qsd.Exception = ErrorCodes.connectFailed(e.getMessage());
							qsd.Output=null;
						}
					}
				};

				waitTimeout(DEFAULT_RECEIVE_TIMEOUT, callback, data);
				
				//if(data.Exception != null)
				//	throw data.Exception;
				if(data.Output!=null){
					byte[] b = (byte[]) data.Output;
					return b;
				}else{
					return null;
				}
			}


			public KXmlParser receiveParser(InputStream in,int receiveBufferSize) throws AppException {
				return null;
			}


			public byte[] receive(InputStream in) throws AppException {
				return null;
			}


			public byte[] receive(InputStream in, int kb) throws AppException {
				return null;
			}


			public void close() {
				// TODO 自动生成方法存根
				
			}


			public void connect(int timeout) throws AppException {
				// TODO 自动生成方法存根
				
			}


			public InputStream getInputStream() throws IOException {
				// TODO 自动生成方法存根
				return null;
			}


			public OutputStream getOutputStream() throws IOException {
				// TODO 自动生成方法存根
				return null;
			}


			public int send(OutputStream connection, byte[] inData) throws AppException {
				// TODO 自动生成方法存根
				return 0;
			}

			
}
