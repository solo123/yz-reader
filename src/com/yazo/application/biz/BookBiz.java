package com.yazo.application.biz;

import javax.microedition.io.HttpConnection;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import com.yazo.application.Configuration;
import com.yazo.contents.LinkContent;
import com.yazo.contents.PageContent;
import com.yazo.mobile.MobileSysData;
import com.yazo.network.HttpConnect;

public class BookBiz {
	public String url, path;
	public PageContent getPageContentFromUrl(String service, String action) {
		url = service + action;
		this.path = action;
		HttpConnect conn = new HttpConnect();
		PageContent c = null;
		
		conn.setNoProxy();
		conn.open(url);
		try {
			if (conn.status == HttpConnection.HTTP_OK){
				KXmlParser parser = new KXmlParser();
				c = new PageContent(Configuration.SCREEN_WIDTH, Configuration.BROWSER_HEIGHT, Configuration.LINE_HEIGHT, Configuration.DEFAULT_FONT);
				parser.setInput(conn.inStream, "utf-8");
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
								String arrow, text, desc, aurl;
								arrow = text = desc = aurl = null;
								while(parser.nextTag()!=XmlPullParser.END_TAG){
									parser.require(XmlPullParser.START_TAG, null, null);
									String n = parser.getName();
									String t = parser.nextText();
									if (n.equals("arrow"))	arrow = t;
									else if (n.equals("text")) text = t;
									else if (n.equals("desc")) desc = t;
									else if (n.equals("url")) aurl = t;
									parser.require(XmlPullParser.END_TAG, null, n);
								}
								c.addLink(arrow, text, desc, aurl);
							}else if(nn.equals("text")){
								String t = parser.nextText();
								c.addText(t);
							}
							parser.require(XmlPullParser.END_TAG, null, nn);
						}
					} else if (name.equals("menu")){
						while(parser.nextTag()!=XmlPullParser.END_TAG){
							parser.require(XmlPullParser.START_TAG, null,null);
							String nn = parser.getName();
							if (nn.equals("link")){
								String text=null, aurl=null;
								while(parser.nextTag()!=XmlPullParser.END_TAG){
									parser.require(XmlPullParser.START_TAG, null, null);
									String n = parser.getName();
									String t = parser.nextText();
									if (n.equals("text")) text = t;
									else if (n.equals("url")) aurl = t;
									parser.require(XmlPullParser.END_TAG, null, n);
								}
								c.menus.addElement(new LinkContent(text, aurl));
							}
						}
					} else if (name.equals("rightkey")){
						String text=null, aurl=null;
						while(parser.nextTag()!=XmlPullParser.END_TAG){
							parser.require(XmlPullParser.START_TAG, null, null);
							String n = parser.getName();
							String t = parser.nextText();
							if (n.equals("text")) text = t;
							else if (n.equals("url")) aurl = t;
							parser.require(XmlPullParser.END_TAG, null, n);
						}
						c.rightKeyMenu = new LinkContent(text, aurl);
					}
					parser.require(XmlPullParser.END_TAG, null, name);
				}
				parser.require(XmlPullParser.END_TAG, null, "page");
			}
		} catch (Exception e) {
			e.printStackTrace();
			c = null;
		}
		if (c!=null){
			c.url = url;
			c.load_from_cache = false;
			c.action = action;
		}
		conn.close();
		conn = null;
		return c;
	}
	
	public String doLogin(){
		MobileSysData mb = new MobileSysData();
		HttpConnect conn = new HttpConnect();
		conn.setNoProxy();
		String data = "a=1&b=2&c=3&d=4";
		conn.post(Configuration.SERVICE_SERVER + "/reader/sync/login", data);
		String r = conn.getContent();
		if (conn.status==200)
			System.out.println("login ok");
		System.out.println("login:" + r);
		
		conn.close();
		conn = null;
		return r;
	}
}
