package com.yazo.CMCC.biz;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import com.yazo.model.IXmlParser;

public class RegisterParser implements IXmlParser {

	public Object parse(InputStream iStream) {
		String user_id = null;
		KXmlParser parser = new KXmlParser();
		try{
			parser.setInput(iStream, "utf-8");
			parser.nextTag();
			parser.require(XmlPullParser.START_TAG, null, "Response");
			parser.nextTag();
			parser.require(XmlPullParser.START_TAG, null, "RegisterRsp");
			parser.nextTag();
			parser.require(XmlPullParser.START_TAG, null, "UserInfo");
			parser.nextTag();
			parser.require(XmlPullParser.START_TAG, null, "userID");
			user_id = parser.nextText();
			parser.nextTag();
			parser.require(XmlPullParser.END_TAG, null, "userID");
			parser.nextTag();
			parser.require(XmlPullParser.END_TAG, null, "UserInfo");
			parser.nextTag();
			parser.require(XmlPullParser.END_TAG, null, "RegisterRsp");
			parser.nextTag();
			parser.require(XmlPullParser.END_TAG, null, "Response");
		} catch (Exception e){
			e.printStackTrace();
		}
		return user_id;
	}
	public String parse(byte[] iBytes){
		if (iBytes==null) return null;
		
		Object o = parse(new ByteArrayInputStream(iBytes));
		if (o==null) return null;
		else return (String)o;
	}
	

}
