package com.yazo.application.biz;

import java.util.Hashtable;

public class Config {
	private Hashtable configs = new Hashtable();
	private static Config instance = null;
	private Config(){
	}
	public static Config getInstance(){
		if (instance==null) instance = new Config();
		return instance;
	}
	
	public void add(int key, String val){
		configs.put(new Integer(key), val);
	}
	public void add(int key, int val){
		configs.put(new Integer(key), new Integer(val));
	}
	public void add(int key, Object val){
		configs.put(new Integer(key), val);
	}
	public String getString(int key){
		Object o = configs.get(new Integer(key));
		if (o!=null && o instanceof String) return (String)o;
		else return null;
	}
	public int getInt(int key){
		Object o = configs.get(new Integer(key));
		if (o!=null && o instanceof Integer) return ((Integer)o).intValue();
		else return 0;
	}
	public Object getObject(int key){
		return configs.get(new Integer(key));
	}
	
}
