package com.yazo.application.thread;

import com.yazo.application.biz.BookBiz;
import com.yazo.model.BrowserCommand;
import com.yazo.model.ICommandManager;

public class ThreadJobLogin extends ThreadJob {
	private ICommandManager manager;
	public ThreadJobLogin(ICommandManager manager){
		this.manager = manager;
	}
	public void run(){
		BookBiz bp = new BookBiz();
		String r = bp.doLogin();
		if (r!=null) {
			manager.command_callback(BrowserCommand.AFTER_LOGIN, r);
		} else {
			manager.command_callback(BrowserCommand.LOGIN_ERROR, null);
		}
	}
}
