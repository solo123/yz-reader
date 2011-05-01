package com.yazo.net;

public class MultiThreadDataObject {
	public String url;
	public int state;
	public Object controler, data;
	
	public MultiThreadDataObject(String url, Object controler){
		this.url = url;
		this.data = null;
		this.state = 0;
		this.controler = controler;
	}
}
