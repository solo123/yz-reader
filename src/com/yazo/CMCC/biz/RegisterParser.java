package com.yazo.CMCC.biz;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import com.yazo.model.IXmlParser;

public class RegisterParser implements IXmlParser {

	public Object parse(InputStream iStream, String[] args) {
		
		String user_id = null;
		KXmlParser parser = new KXmlParser();
		int len = args.length;
		try {
			parser.setInput(iStream, "utf-8");
			for (int i = 0; i < len; i++) {
//				System.out.println("user_id:"+args[i]);
				parser.nextTag();
				parser.require(XmlPullParser.START_TAG, null,
						args[i]);
			}
			user_id = parser.nextText();
			for (int j = len; j <= 0; j--) {
				parser.nextTag();
				parser.require(XmlPullParser.END_TAG, null,
						args[j]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user_id;
	}

	public String parse(byte[] iBytes,String[] args) {
		if (iBytes == null)
			return null;

		Object o = parse(new ByteArrayInputStream(iBytes), args);
		if (o == null)
			return null;
		else
			return (String) o;
	}

	public Object parse(InputStream iStream) {
		return null;
	}

}
