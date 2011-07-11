package com.yazo.application.thread;

import com.yazo.application.biz.BookBiz;
import com.yazo.contents.PageContent;
import com.yazo.model.BrowserCommand;
import com.yazo.model.ICommandListener;

public class ThreadJobPageContent extends ThreadJob {
	private String service, action;
	private ICommandListener manager;
	private Object lock = new Object();
	public ThreadJobPageContent(String service, String action, ICommandListener manager){
		this.service = service;
		this.action = action;
		this.manager = manager;
		this.name = "Page";
	}
	public void run(){
		synchronized (lock) {
			manager.execute_command(BrowserCommand.LOADING_FROM_INTERNET, null);
			BookBiz bp = new BookBiz();
			PageContent pc = bp.getPageContentFromUrl(service, action);
			if (pc!=null) {
				manager.execute_command(BrowserCommand.AFTER_LINECONTENT_LOADED, pc);
			} else {
				if (bp.network_connect_status==-1){
					manager.execute_command(BrowserCommand.NO_NETWORK, null);
				} else
					manager.execute_command(BrowserCommand.LOAD_ERROR, null);
			}
		}
	}
}
