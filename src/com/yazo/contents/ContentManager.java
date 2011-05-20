package com.yazo.contents;

import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import com.yazo.application.Configuration;
import com.yazo.application.biz.BookBiz;
import com.yazo.model.BrowserCommand;
import com.yazo.model.ICommandManager;

public class ContentManager{
	public String header;
	public PageContent content;
	public PageCache content_buffer;
	public Vector menu_contents;
	private ICommandManager command_manager;
	private String current_service = null, current_path = null;
	
	public ContentManager(ICommandManager manager){
		header = null;
		content = null;
		content_buffer = new PageCache();
		command_manager = manager;
		menu_contents = new Vector();
	}

	public void command_callback(int command, Object data) {
		switch(command){
		case BrowserCommand.PARSE_XML:
			break;
		}
	}
	public void setCurrentContent(Object data){
		if (data!=null){
			content = (PageContent)data;
			if (!content.load_from_cache) 
				content_buffer.put(content.action, content);
		}
	}
	public void loadContentFromUrl(String service, String path){
		current_service = service;
		current_path = path;
		Object buf = content_buffer.get(path);
		if (buf!=null){
			content = (PageContent)buf;
			content.load_from_cache = true;
			if (command_manager!=null) command_manager.command_callback(BrowserCommand.AFTER_LINECONTENT_LOADED, null);
			return;
		}
		
		if (command_manager!=null) command_manager.command_callback(BrowserCommand.LOADING_FROM_INTERNET, null);
		new Thread(){
			public void run() {
				BookBiz bp = new BookBiz();
				content = bp.getPageContentFromUrl(current_service, current_path);
				if (content!=null) {
					content_buffer.put(bp.path, content);
					command_manager.command_callback(BrowserCommand.AFTER_LINECONTENT_LOADED, null);
				} else {
					command_manager.command_callback(BrowserCommand.LOAD_ERROR, null);
				}
			};
		}.start();
		
	
	}
	
}
