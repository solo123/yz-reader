package com.yazo.contents;

import com.yazo.model.BrowserCommand;
import com.yazo.model.CommandManagerObject;
import com.yazo.model.ICommandManager;
import com.yazo.net.ContentServerService;
import com.yazo.thread.ThreadPool;
import com.yazo.thread.WaitCallback;

public class ContentManager{
	public String header;
	public LineContent content;
	public PageCache content_buffer;
	private ICommandManager command_manager;
	
	public ContentManager(ICommandManager manager){
		header = null;
		content = null;
		content_buffer = new PageCache();
		command_manager = manager;
	}
	public void command_callback(int command, Object data) {
		switch(command){
		case BrowserCommand.PARSE_XML:
			break;
		}
	}
	public void loadLineContentFromUrl(String service, String url){
		Object buf = content_buffer.get(url);
		if (buf!=null){
			if (command_manager!=null) command_manager.command_callback(BrowserCommand.AFTER_LINECONTENT_LOADED, buf);
			return;
		}
		WaitCallback callback = new WaitCallback() {
			public void execute(Object data) {
				CommandManagerObject manager = (CommandManagerObject)data;
				LineContent lc = ContentServerService.getAndParseContent((String)manager.data1 + (String)manager.data2);
				if (lc!=null) {
					content_buffer.put((String)manager.data2, lc);
					manager.manager.command_callback(BrowserCommand.AFTER_LINECONTENT_LOADED, lc);
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
}
