package com.yazo.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.kxml2.io.KXmlParser;

import com.yazo.application.MainMIDlet;
import com.yazo.thread.ThreadPool;
import com.yazo.thread.WaitCallback;
import com.yazo.util.AppContext;
import com.yazo.util.Consts;
import com.yazo.util.GZIP;
import com.yazo.util.MemoryStream;
import com.yazo.util.StringUtil;
import com.yazo.util.User;

public class HttpChannel extends TCPChannel {

	private HttpConnection hc;
	private OutputStream out;
	private InputStream in;
	byte[] dataArray = null;
	private String url = "";
	private String cEncoding;
	private int length;
	private String sCookie;
	private String method = HttpConnection.GET;
	private String headerAction = "register";
	private String resultCode = "";
	final String proxy = "10.0.0.172:80";
	
	// 是否用cnwap
	boolean useProxy = false;
	String domain;
	String uri;

	

	public HttpChannel(String ip, int port, int timeout) {
		super(ip, port, timeout);

	}

	public void setUrl(String url) {
		this.url = url;

	}

	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
		String[] s = StringUtil.splitUrl(url);
		domain = s[0];
		uri = s[1];
	}

	public void setAction(String action) {
		this.headerAction = action;
	}

	public void send(byte[] input) throws Exception {

	}

	/***
	 * 阅读返回对象
	 */
	public KXmlParser receiveParser(InputStream inputstream,
			int receiveBufferSize) throws Exception {
		if (inputstream == null || receiveBufferSize == 0) {
			return null;
		}
		if (!resultCode.equals("0") && !"2022".equals(resultCode)
				&& !resultCode.equals("")) {
			return null;
		}
		in = inputstream;
		length = receiveBufferSize;
		QueryServerData data = new QueryServerData();
		WaitCallback callback = new WaitCallback() {

			public void execute(Object state) {
				QueryServerData qsd = (QueryServerData) state;
				KXmlParser parser = null;
				ByteArrayInputStream bIn = null;
				try {
					if (length > 0) {
						byte[] totalData = new byte[length];
						int actual = 0;
						int bytesread = 0;
						while ((bytesread != length) && (actual != -1)) {
							actual = in.read(totalData, bytesread, length
									- bytesread);
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
						parser.setFeature(
								"http://xmlpull.org/v1/doc/features.html#relaxed",
								true);
						parser.setInput(bIn, null);
					}
					qsd.Output = parser;
					qsd.IsCompleted = true;
				} catch (Exception e) {
					e.printStackTrace();
					qsd.Output = null;
				}
			}
		};

		waitTimeout(DEFAULT_RECEIVE_TIMEOUT, callback, data);
		if (data.Output != null) {
			return (KXmlParser) data.Output;
		} else {
			return null;
		}

	}

	public int getCode() {
		try {
			if (hc == null) {
				return -200;
			} else {

				return hc.getResponseCode();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}


	/**
	 * 普通接收数据
	 */
	public byte[] receive(InputStream inputstream) throws Exception {
		try {
			int code = hc.getResponseCode();
			if (code != 200) {
				return null;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (inputstream == null) {
			return null;
		}
		if (resultCode.equals("2017")) {
			return null;
		}
		if (resultCode.equals("2023")) {
			return "".getBytes();
		}
		if (!resultCode.equals("0") && !"2022".equals(resultCode)
				&& !resultCode.equals("")) {
			return null;
		}
		in = inputstream;
		QueryServerData data = new QueryServerData();
		WaitCallback callback = new WaitCallback() {
			public void execute(Object state) {
				QueryServerData qsd = (QueryServerData) state;
				try {
					byte[] buffer = new byte[DEFAULT_CLIENT_RECEIVE_BUFFER_SIZE];
					MemoryStream stream = new MemoryStream();

					int count = 0;
					while ((count = in.read(buffer)) > 0) {
						stream.write(buffer, 0, count);
						count++;
					}
					qsd.Output = stream.toArray();
					qsd.IsCompleted = true;
				} catch (Exception e) {
					e.printStackTrace();
					qsd.Output = null;
				}
			}
		};

		waitTimeout(DEFAULT_RECEIVE_TIMEOUT, callback, data);
		if (data.Output != null) {
			return (byte[]) data.Output;
		} else {
			return null;
		}
	}

	public void close() {
		try {
			if (hc != null) {
				hc.close();
				hc = null;
			}
			if (out != null) {
				out.close();
				out = null;
			}
			if (in != null) {
				in.close();
				in = null;
			}
			dataArray = null;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void connect(int timeout) throws Exception {
		System.out.println("connect:" + url);
		
		QueryServerData data = new QueryServerData();
		WaitCallback callback = new WaitCallback() {
			public void execute(Object state) {
				QueryServerData qsd = (QueryServerData) state;
				try {
					if (!AppContext.getInstance().isCMNET()) {
						setUseProxy(true);
						hc = (HttpConnection) Connector.open("http://" + proxy
								+ uri, Connector.READ_WRITE, true);
						hc.setRequestProperty("X-Online-Host", domain);
					} else {
						hc = (HttpConnection) Connector.open(url,
								Connector.READ_WRITE, true);
					}
					hc.setRequestMethod(method);
					hc.setRequestProperty("Accept-Charset", "UTF-8");
					qsd.IsCompleted = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		waitTimeout(timeout, callback, data);
	}

	public void setMethed(String methed) {
		this.method = methed;
	}

	public void setHttpRequestProperty(String xml) {
		try {

			hc.setRequestProperty("Accept", "*/*");

			hc.setRequestProperty("Client-Agent", Consts.strUserAgent);

			hc.setRequestProperty("x-up-calling-line-id", "13400981944");

			hc.setRequestProperty("user-id", User.userId != null ? User.userId
					: "");

			hc.setRequestProperty("APIVersion", "1.1.0");

			if (sCookie != null) {
				hc.setRequestProperty("Cookie", sCookie);
			}

			hc.setRequestProperty("Encoding-Type", "gzip");// gzip

			hc.setRequestProperty("Action", headerAction);

			hc.setRequestProperty("ClientHash", "");

			hc.setRequestProperty("Version", Consts.strUserAgent);

			if (method.equals(HttpConnection.POST)) {
				hc.setRequestProperty("Content-Length", "" + xml.length());
			}
		} catch (Exception e) {

		}
	}

	public int getDataLength() {
		return length;
	}

	public void getHttpgetHeaderField() {
		try {
			cEncoding = hc.getHeaderField("Encoding-Type");

			resultCode = hc.getHeaderField("Result-Code");

			resultCode = (resultCode == null ? "0" : resultCode);

			String cLength = hc.getHeaderField("Content-Length");
			length = Integer.parseInt(cLength.trim());

			String cook = hc.getHeaderField("Set-Cookie");
			sCookie = cook == null ? sCookie : cook;

			User.regCode = hc.getHeaderField("RegCode");
			if (resultCode.equals("4003")) {
				Consts.HOSTURL = hc.getHeaderField("Request-URL");
			}

		} catch (Exception e) {

		}
	}

	public String getResultCode() {
		return resultCode;
	}

	public InputStream getInputStream() throws IOException {
		in = hc.openInputStream();
		return in;
	}

	public OutputStream getOutputStream() throws IOException {
		out = hc.openOutputStream();
		return out;
	}

	public int send(OutputStream connection, byte[] inData) throws Exception {
		dataArray = inData;
		if (connection == null || inData == null) {
			throw new Exception("发送的数据为null");
		}

		QueryServerData data = new QueryServerData();
		WaitCallback callback = new WaitCallback() {
			public void execute(Object state) {
				QueryServerData qsd = (QueryServerData) state;
				try {

					out.write(dataArray);
					qsd.Output = dataArray.length + "";
					dataArray = null;
					qsd.IsCompleted = true;
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		waitTimeout(DEFAULT_SEND_TIMEOUT, callback, data);
		if (data.Output != null) {
			return Integer.parseInt((String) data.Output);
		} else {
			return 0;
		}
	}

	private void waitTimeout(int timeout, WaitCallback callback,
			QueryServerData data) {
		try {
			ThreadPool.queueWorkItem(callback, data);
		} catch (Exception e) {
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
			close();
			System.gc();
		}
	}

}
