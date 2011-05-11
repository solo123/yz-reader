package com.yazo.contents;

import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import com.yazo.application.Configuration;
import com.yazo.model.BrowserCommand;
import com.yazo.model.CommandManagerObject;
import com.yazo.model.ICommandManager;
import com.yazo.thread.ThreadPool;
import com.yazo.thread.WaitCallback;

public class ContentManager{
	public String header;
	public PageContent content;
	public PageCache content_buffer;
	public Vector menu_contents;
	private ICommandManager command_manager;
	
	public ContentManager(ICommandManager manager){
		header = null;
		content = null;
		content_buffer = new PageCache();
		command_manager = manager;
		menu_contents = new Vector();
		loadMenuContent();
	}
	private void loadMenuContent(){
		menu_contents.addElement(new LinkContent("1. 搜索图书", "CMD_SEARCH"));
		menu_contents.addElement(new LinkContent("2. 系统设置", "config_page"));
		menu_contents.addElement(new LinkContent("3. 用户帮助", "help_page"));
		menu_contents.addElement(new LinkContent("4. 软件升级", "upgrade_page"));
		menu_contents.addElement(new LinkContent("5. 返回首页", "home"));
	}
	public void command_callback(int command, Object data) {
		switch(command){
		case BrowserCommand.PARSE_XML:
			break;
		}
	}
	public void loadContentFromUrl(String service, String url){
		Object buf = content_buffer.get(url);
		if (buf!=null){
			if (command_manager!=null) command_manager.command_callback(BrowserCommand.AFTER_LINECONTENT_LOADED, buf);
			return;
		}
		WaitCallback callback = new WaitCallback() {
			public void execute(Object data) {
				CommandManagerObject manager = (CommandManagerObject)data;
				content = getAndParseContent((String)manager.data1 + (String)manager.data2);
				if (content!=null) {
					content_buffer.put((String)manager.data2, content);
					manager.manager.command_callback(BrowserCommand.AFTER_LINECONTENT_LOADED, null);
				} else {
					manager.manager.command_callback(BrowserCommand.LOAD_ERROR, null);
				}
			}
		};
		
		CommandManagerObject data = new CommandManagerObject();
		data.manager = command_manager;
		data.data1 = service;
		data.data2 = url;
		command_manager.command_callback(BrowserCommand.LOADING_FROM_INTERNET, null);
		try {
			ThreadPool.queueWorkItem(callback, data);
		} catch (Exception e) {
			System.out.println("waitTimeOut error:" + e.getMessage());
		}	
	}
	public PageContent getAndParseContent(String url) {
		HttpConnection conn;
		PageContent c = null;
		try {
			conn = (HttpConnection)Connector.open(url, Connector.READ );
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			KXmlParser parser = new KXmlParser();
			c = new PageContent(Configuration.SCREEN_WIDTH, Configuration.BROWSER_HEIGHT, Configuration.LINE_HEIGHT, Configuration.DEFAULT_FONT);
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
							c.addText(parser.nextText());
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
		} catch (Exception e) {
			e.printStackTrace();
			c = null;
		}
		if (c!=null){
			c.url = url;
			c.load_from_cache = Boolean.FALSE;
		}
		return c;
	}
}
