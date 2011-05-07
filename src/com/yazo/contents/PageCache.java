package com.yazo.contents;

import java.util.Hashtable;

public class PageCache {
	private Hashtable buffer = new Hashtable();
	public PageCache(){
	}
	
	public void put(String key, Object data) {
		buffer.put(key, data);
	}
	public Object get(String key){
		return buffer.get(key);
	}
}
