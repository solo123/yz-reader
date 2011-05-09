package com.yazo.CMCC.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.kxml2.io.KXmlParser;

import com.yazo.util.AppContext;
import com.yazo.util.Consts;
import com.yazo.util.GZIP;
import com.yazo.util.MemoryStream;
import com.yazo.util.StringUtil;
import com.yazo.util.User;

public class Channel {
	private HttpConnection hc;
	private InputStream is;
	private OutputStream os;
	private String url;
	private String action;
	private String method = HttpConnection.GET;

	private String cEncoding;
	private String resultCode;
	private int length;
	private String sCookie;

	private String domain;
	private String uri;

	public Channel(String url, String action, String method) {
		this.url = url;
		this.action = action;
		this.method = method;
	}

	/**
	 * 普通查询服务器
	 * 
	 * @param inData
	 * @return
	 * @throws Exception
	 */
	public final byte[] queryServer() {
		byte[] result = null;
		try {
			connect();
			if (method.equals("GET")) {
				if (!action.equals("")) {
					setHttpRequestProperty(null);
				}
				getHttpgetHeaderField();
			}
			result = receive(hc.openInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return result;
	}

	public final Object queryServerForXML(String xml) {
		Object obj = null;
		try {
			connect();
			setHttpRequestProperty(xml);// 设置提交的头信息
			byte[] inData = xml.getBytes("UTF-8");
			send(hc.openOutputStream(), inData);// 发送数据
			getHttpgetHeaderField();// 获得返回头信息
			obj = receiveParser(hc.openInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return obj;
	}

	public void connect() throws Exception {
		System.out.println("connect:"+url);
		try {
			if (!AppContext.getInstance().isCMNET()) {
				useProxy();
				hc = (HttpConnection) Connector.open("http://10.0.0.172:80"
						+ uri, Connector.READ_WRITE, true);
				hc.setRequestProperty("X-Online-Host", domain);
			} else {
				hc = (HttpConnection) Connector.open(url, Connector.READ_WRITE,
						true);
			}
			hc.setRequestMethod(method);
			hc.setRequestProperty("Accept-Charset", "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送数据
	 * 
	 * @param connection
	 * @param inData
	 * @throws Exception
	 */
	public void send(OutputStream connection, byte[] inData) throws Exception {
		os = connection;
		if (os == null || inData == null) {
			throw new Exception("发送的数据为null");
		}
		os.write(inData);
	}

	/**
	 * 普通接收数据
	 */
	public byte[] receive(InputStream inputstream) throws Exception {
		byte[] result = null;
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
		is = inputstream;
		try {
			byte[] buffer = new byte[512];
			MemoryStream stream = new MemoryStream();
			int count = 0;
			while ((count = is.read(buffer)) > 0) {
				stream.write(buffer, 0, count);
				count++;
			}
			result = stream.toArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获得返回数据
	 * 
	 * @param inputstream
	 * @param receiveBufferSize
	 * @return
	 * @throws Exception
	 */
	public KXmlParser receiveParser(InputStream inputstream) throws Exception {

		is = inputstream;

		KXmlParser parser = null;
		ByteArrayInputStream bin = null;
		if (inputstream == null) {
			return null;
		}
		if (!resultCode.equals("0") && !"2022".equals(resultCode)
				&& !resultCode.equals("")) {
			return null;
		}
		if (length > 0) {
			byte[] totalData = new byte[length];
			int actual = 0;
			int bytesread = 0;
			while ((bytesread != length) && (actual != -1)) {
				actual = is.read(totalData, bytesread, length - bytesread);
				bytesread += actual;
			}
			if (cEncoding != null && cEncoding.equals("gzip")) {
				byte[] gdata = GZIP.inflate(totalData);
				totalData = null;
				bin = new ByteArrayInputStream(gdata);
			} else {
				bin = new ByteArrayInputStream(totalData);
			}
			parser = new KXmlParser();
			parser.setFeature(
					"http://xmlpull.org/v1/doc/features.html#relaxed", true);
			parser.setInput(bin, null);
		}

		return parser;
	}

	/**
	 * 设置发送头信息
	 */
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

			hc.setRequestProperty("Action", action);

			hc.setRequestProperty("ClientHash", "");

			hc.setRequestProperty("Version", Consts.strUserAgent);

			if (method.equals(HttpConnection.POST)) {
				hc.setRequestProperty("Content-Length", "" + xml.length());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 使用代理
	 */
	public void useProxy() {
		String[] s = StringUtil.splitUrl(url);
		domain = s[0];
		uri = s[1];
	}

	/**
	 * 获取头信息
	 */
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

	/**
	 * 关闭流
	 */
	public void close() {
		try {
			if (hc != null) {
				hc.close();
				hc = null;
			}
			if (os != null) {
				os.close();
				os = null;
			}
			if (is != null) {
				is.close();
				is = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
