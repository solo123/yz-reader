package com.yazo.util;


public class AppContext {
	
	static AppContext instance;
	boolean iscmnet=false;
	public static boolean isFirst=true;
	/***********最近阅读历史*************/
	public final static int HISTORYREAD=-1;
	/***********分类 0********************/
	public final static int BOOKTYPE=0;
	/************新书1*******************/
	public final static int NEWBOOK=1;
	/*************热点书2******************/
	public final static int HOTBOOK=2;
	/**************某分类下所有书3*****************/
	public final static int BOOKTYPEID=3;
	/************书简介4*******************/
	public final static int BOOKINTRO=4;
	/**************书所有章节5*****************/
	public final static int BOOKCHAPTER=5;
	/***********搜索6********************/
	public final static int BOOKSEARCH=6;
	/************获取书内容7*******************/
	public final static int BOOKCONTEXT=7;
	/**********畅销排行***********************/
	public final static int MOSTBOOK=8;
	/*************重磅好书************************/
	public final static int GOODBOOK=9;
	/*************精品回顾************************/
	public final static int CLASSICBOOK=10;
	
	/*************编辑推荐************************/
	public final static int TUIJIANCBOOK=11;
	
	public final static int UPPAGE=12;
	public final static int NEXTPAGE=13;
	/****
	 * true Ϊsocket  false Ϊhttp
	 */
	boolean isHTTPMode=false;
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
	public void setHTTPMode(boolean ishttp){
		isHTTPMode=ishttp;
	}
	public boolean isHTTPMode(){
		return this.isHTTPMode;
	}

}
