package com.yazo.CMCC;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.yazo.util.LoginMsg;

public class GetCMCCMsg {
	public byte[] getFileByte(String path) {
		InputStream in = this.getClass().getResourceAsStream(path);
		int c;
		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		try {
			while ((c = in.read()) != -1) {
				bais.write(c);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bais.toByteArray();
	}
	
	public LoginMsg getcmccMsg() throws XmlPullParserException, IOException {
		LoginMsg loginMsg = null;
		ByteArrayInputStream bin = new ByteArrayInputStream(
				getFileByte("/login.xml"));
		InputStreamReader in = new InputStreamReader(bin);
		KXmlParser xmlpull = new KXmlParser();
		xmlpull.setInput(in);
		int eventCode = xmlpull.getEventType();

		while (eventCode != XmlPullParser.END_DOCUMENT) {
			switch (eventCode) {
			case XmlPullParser.START_DOCUMENT: {
				loginMsg = new LoginMsg();
				break;
			}
			case XmlPullParser.START_TAG: {
				if("url".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setUrl(xmlpull.nextText());
				} else if("agent".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setAgent(xmlpull.nextText());
				} else if("password".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setPassword(xmlpull.nextText());
				} else if("on_dubug".equalsIgnoreCase(xmlpull.getName())){
					
					loginMsg.setDebug((xmlpull.nextText()).equals("true"));
				} else if("channel".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setChannel(xmlpull.nextText());
				} else if("log_service".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setLogServer(xmlpull.nextText());
				} else if("type".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setType(xmlpull.nextText());
				} else if("number".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setNumber(xmlpull.nextText());
				} else if("power_switch".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setPowerSwitch(xmlpull.nextText());
				} else if("MSG1".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setMessage(xmlpull.nextText());
				} else if("MSG2".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setRegister(xmlpull.nextText());
				} else if("MSG3".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setAuthenticate(xmlpull.nextText());
				} else if("MSG4".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setGetClientWelcomeInfo(xmlpull.nextText());
				} else if("MSG5".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setGetCatalogInfo(xmlpull.nextText());
				} else if("MSG6".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setGetContentInfo(xmlpull.nextText());
				} else if("MSG7".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setGetChapterInfo(xmlpull.nextText());
				} else if("MSG8".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setGetCatalogProductInfo(xmlpull.nextText());
				} else if("MSG9".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setSubscribeCatalog(xmlpull.nextText());
				} else if("MSG10".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setGetContentProductInfo(xmlpull.nextText());
				} else if("MSG11".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setSubscribeContent(xmlpull.nextText());
				} else if("MSG12".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setGetUserId(xmlpull.nextText());
				}
				break;
			}

			
			}
			eventCode = xmlpull.next();// 没有结束xml文件就推到下个进行解析
		}
		return loginMsg;
	}
	
	/**
	 * 通过服务器获得数据
	 * @param b
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public LoginMsg getcmccMsg(byte[] b) throws XmlPullParserException, IOException {
		LoginMsg loginMsg = null;
		ByteArrayInputStream bin = new ByteArrayInputStream(b);
		InputStreamReader in = new InputStreamReader(bin);
		KXmlParser xmlpull = new KXmlParser();
		xmlpull.setInput(in);
		int eventCode = xmlpull.getEventType();

		while (eventCode != XmlPullParser.END_DOCUMENT) {
			switch (eventCode) {
			case XmlPullParser.START_DOCUMENT: {
				loginMsg = new LoginMsg();
				break;
			}
			case XmlPullParser.START_TAG: {
				if("url".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setUrl(xmlpull.nextText());
				} else if("agent".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setAgent(xmlpull.nextText());
				} else if("password".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setPassword(xmlpull.nextText());
				} else if("on_dubug".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setDebug((xmlpull.nextText()).equals("true"));
				} else if("channel".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setChannel(xmlpull.nextText());
				} else if("log_service".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setLogServer(xmlpull.nextText());
				} else if("type".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setType(xmlpull.nextText());
				} else if("number".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setNumber(xmlpull.nextText());
				} else if("power_switch".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setPowerSwitch(xmlpull.nextText());
				} else if("MSG1".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setMessage(xmlpull.nextText());
				} else if("MSG2".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setRegister(xmlpull.nextText());
				} else if("MSG3".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setAuthenticate(xmlpull.nextText());
				} else if("MSG4".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setGetClientWelcomeInfo(xmlpull.nextText());
				} else if("MSG5".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setGetCatalogInfo(xmlpull.nextText());
				} else if("MSG6".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setGetContentInfo(xmlpull.nextText());
				} else if("MSG7".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setGetChapterInfo(xmlpull.nextText());
				} else if("MSG8".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setGetCatalogProductInfo(xmlpull.nextText());
				} else if("MSG9".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setSubscribeCatalog(xmlpull.nextText());
				} else if("MSG10".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setGetContentProductInfo(xmlpull.nextText());
				} else if("MSG11".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setSubscribeContent(xmlpull.nextText());
				} else if("MSG12".equalsIgnoreCase(xmlpull.getName())){
					loginMsg.setGetUserId(xmlpull.nextText());
				}
				break;
			}

			
			}
			eventCode = xmlpull.next();// 没有结束xml文件就推到下个进行解析
		}
		return loginMsg;
	}
}
