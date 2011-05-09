package com.yazo.util;

public class AppContext {
	
	static AppContext instance;
	boolean iscmnet=false;

	public AppContext(){
		
	}
	
	public static AppContext getInstance(){
		if(instance==null){
			instance=new AppContext();
		}
		return instance;
	}
	public void setIsCMNET(boolean iscmnet){
		this.iscmnet=iscmnet;
	}
	public boolean isCMNET(){
		return iscmnet;
	}

}
