package com.yazo.model;

import java.util.Hashtable;

public class KeyValuePair {
	private Hashtable hash = new Hashtable();
	public KeyValuePair(String data){
		if (data!=null) {
			int p = 0;
			int len = data.length();
			while(p<len){
				int eq = data.indexOf('=', p);
				if (eq<0) break;
				String key = data.substring(p, eq);
				int ld = data.indexOf('\n', eq);
				if(ld<0) ld=len;
				String val = data.substring(eq,ld);
				hash.put(key, val);
				p = ld;
			}
		}
	}
	public String getValue(String key){
		Object v = hash.get(key);
		if (v==null)
			return null;
		else
			return (String)v;
	}
}
