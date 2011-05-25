package com.yazo.application.thread;

import com.yazo.application.biz.BookBiz;
import com.yazo.model.BrowserCommand;
import com.yazo.model.ICommandListener;

public class ThreadJobLogin extends ThreadJob {
	private ICommandListener manager;
	public ThreadJobLogin(ICommandListener manager){
		this.manager = manager;
	}
	public void run(){
		BookBiz bp = new BookBiz();
		String r = bp.doLogin();
		if (r!=null) {
			manager.execute_command(BrowserCommand.AFTER_LOGIN, r);
		} else {
			manager.execute_command(BrowserCommand.LOGIN_ERROR, null);
		}
	}
}
