package com.yazo.application.thread;

//import java.util.Vector;
import com.yazo.model.ICommandListener;

public class ThreadManager {
	//private Vector threadPool = new Vector();
	public void getPageContentThread(String service, String action, ICommandListener manager){
		ThreadJobPageContent tjp = new ThreadJobPageContent(service, action, manager);
		tjp.start();
		//threadPool.addElement(tjp);
	}
	
	public void loginThread(ICommandListener manager){
		ThreadJobLogin tjl = new ThreadJobLogin(manager);
		tjl.start();
	}
}
