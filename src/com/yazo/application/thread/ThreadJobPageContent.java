package com.yazo.application.thread;

import com.yazo.application.biz.BookBiz;
import com.yazo.contents.PageContent;
import com.yazo.model.BrowserCommand;
import com.yazo.model.ICommandManager;

public class ThreadJobPageContent extends ThreadJob {
	private String service, action;
	private ICommandManager manager;
	public ThreadJobPageContent(String service, String action, ICommandManager manager){
		this.service = service;
		this.action = action;
		this.manager = manager;
		this.name = "Page";
	}
	public void run(){
		BookBiz bp = new BookBiz();
		PageContent pc = bp.getPageContentFromUrl(service, action);
		if (pc!=null) {
			manager.command_callback(BrowserCommand.AFTER_LINECONTENT_LOADED, pc);
		} else {
			manager.command_callback(BrowserCommand.LOAD_ERROR, null);
		}
	}
}
