package com.yazo.application.thread;

//import java.util.Vector;
import com.yazo.model.ICommandManager;

public class ThreadManager {
	//private Vector threadPool = new Vector();
	public void getPageContentThread(String service, String action, ICommandManager manager){
		ThreadJobPageContent tjp = new ThreadJobPageContent(service, action, manager);
		tjp.start();
		//threadPool.addElement(tjp);
	}
	
	public void loginThread(ICommandManager manager){
		ThreadJobLogin tjl = new ThreadJobLogin(manager);
		tjl.start();
	}
}
