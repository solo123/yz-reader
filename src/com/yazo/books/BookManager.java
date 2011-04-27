package com.yazo.books;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;



public class BookManager {

	
	
	public String header;
	public LineContent content;
	public int line_chars;
	
	public BookManager(){
		header = null;
		content = null;
		line_chars = 0;
	}
	public int getPage(String page_name){
		System.out.println("getPage:" + page_name);
		try {
			content = getWebPage(page_name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (content!=null) {
			header = content.header;
			return 1;
		}
		return 0;
	}
	public LineContent getWebPage(String page_name) throws IOException, XmlPullParserException{
		HttpConnection conn = (HttpConnection)Connector.open  ("http://bk-b.info/reader/pages/" + page_name, Connector.READ );
		conn.setRequestProperty("Accept-Charset", "UTF-8");
		KXmlParser parser = new KXmlParser();
		LineContent c = new LineContent(line_chars);
		parser.setInput(conn.openInputStream(), "utf-8");
		parser.nextTag();
		parser.require(XmlPullParser.START_TAG, null, "page");
		while (parser.nextTag()!=XmlPullParser.END_TAG){
			parser.require(XmlPullParser.START_TAG, null, null);
			String name = parser.getName();
			if (name.equals("header")){
				c.header = parser.nextText();
			} else if (name.equals("content")){
				while(parser.nextTag()!=XmlPullParser.END_TAG){
					parser.require(XmlPullParser.START_TAG, null, null);
					String nn = parser.getName();
					if (nn.equals("link")){
						String arrow, text, desc, url;
						arrow = text = desc = url = null;
						while(parser.nextTag()!=XmlPullParser.END_TAG){
							parser.require(XmlPullParser.START_TAG, null, null);
							String n = parser.getName();
							String t = parser.nextText();
							if (n.equals("arrow"))	arrow = t;
							else if (n.equals("text")) text = t;
							else if (n.equals("desc")) desc = t;
							else if (n.equals("url")) url = t;
							parser.require(XmlPullParser.END_TAG, null, n);
						}
						c.addLink(arrow, text, desc, url);
					}else if(nn.equals("text")){
						c.addText(parser.nextText());
					}
					parser.require(XmlPullParser.END_TAG, null, nn);
				}
			}
			parser.require(XmlPullParser.END_TAG, null, name);
		}
		parser.require(XmlPullParser.END_TAG, null, "page");
		return c;
	}
	
	
	
	

	
}
