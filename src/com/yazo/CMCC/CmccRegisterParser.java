package com.yazo.CMCC;

import com.yazo.model.IXmlParser;
import java.io.InputStream;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;


public class CmccRegisterParser implements IXmlParser  {
	/*
	 * <xml>
	 * <userinfo>userid</userinfo>
	 */
	public Object parse(InputStream iStream) {
		String userid = null;
		try {
			KXmlParser parser = new KXmlParser();
			parser.setInput(iStream, "utf-8");
			parser.nextTag();
			parser.require(XmlPullParser.START_TAG, null, "userinfo");
			userid = parser.nextText();
			parser.require(XmlPullParser.END_TAG, null, "userinfo");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userid;
	}
}
