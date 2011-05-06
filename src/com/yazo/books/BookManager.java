package com.yazo.books;

import java.io.IOException;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.yazo.application.Configuration;
import com.yazo.net.ContentServerService;
import com.yazo.thread.ThreadPool;
import com.yazo.thread.WaitCallback;
import com.yazo.tools.CallbackData;
import com.yazo.tools.ThreadCallback;



public class BookManager {
	public String header;
	public LineContent content;
	public int line_chars;
	private Hashtable content_buffer;
	
	public BookManager(){
		header = null;
		content = null;
		line_chars = 0;
		content_buffer = new Hashtable();
		Resize();
	}
	public void Resize(){
		line_chars = (Configuration.SCREEN_WIDTH - 20)/Configuration.FONT_WIDTH;
	}
	public LineContent threadGetPage(String service, String page_name, ThreadCallback callback_object){
		Object buf = content_buffer.get(page_name);
		if (buf!=null)
			return (LineContent)buf;
		else {
			CallbackData data = new CallbackData();
			data.callback_object = callback_object;
			data.data1 = service;
			data.data2 = page_name;
			data.command = "LoadingFromInternet";
			callback_object.thread_callback(data);
			WaitCallback callback = new WaitCallback() {
				public void execute(Object data) {
					CallbackData cdata = (CallbackData)data;
					LineContent lc = null;
					lc = ContentServerService.getAndParseContent((String)cdata.data1 + (String)cdata.data2, line_chars);
					if (lc!=null) {
						content_buffer.put((String)cdata.data2, lc);
						cdata.command = "LoadContent";
						cdata.data1 = lc;
						cdata.callback_object.thread_callback(cdata);
					} else {
						cdata.command = "LoadError";
						cdata.callback_object.thread_callback(cdata);
					}
				}
			};
			try {
				ThreadPool.queueWorkItem(callback, data);
			} catch (Exception e) {
				System.out.println("waitTimeOut error:" + e.getMessage());
			}	
			return null;
		}
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
		content_buffer.put(page_name, c);
		return c;
	}
	
	
	
	

	
}
