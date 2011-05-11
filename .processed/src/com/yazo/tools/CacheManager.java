package com.yazo.tools;

import java.util.Hashtable;

public class CacheManager {
	private Hashtable buffer;
	public CacheManager(){
		buffer = new Hashtable();
	}
	public void put(String key, Object val){
		buffer.put(key, val);
	}
	public Object get(String key){
		return buffer.get(key);
	}
}
