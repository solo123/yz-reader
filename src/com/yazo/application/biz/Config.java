package com.yazo.application.biz;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import com.yazo.tools.IniParser;

public class Config {
	private Hashtable configs = new Hashtable();
	private static Config instance = null;
	private Config(){}
	public static Config getInstance(){
		if (instance==null) instance = new Config();
		return instance;
	}
	
	public void add(int key, String val){
		if (val!=null)
			configs.put(new Integer(key), val);
		else
			configs.remove(new Integer(key));
	}
	public void add(int key, int val){
		configs.put(new Integer(key), new Integer(val));
	}
	public void add(int key, Object val){
		if (val!=null)
			configs.put(new Integer(key), val);
		else
			configs.remove(new Integer(key));
	}
	public String getString(int key){
		Object o = configs.get(new Integer(key));
		if (o!=null && o instanceof String) return (String)o;
		else return null;
	}
	/*
	 * 序列化配置信息，仅200>id>100
	 */
	public byte[] getBytes(){
		byte[] buf = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			for (Enumeration e = configs.keys(); e.hasMoreElements(); ) {
				Integer k = (Integer)e.nextElement();
				if (k.intValue()>100 && k.intValue()<200){
					String v = (String)configs.get(k);
					if (v!=null && v.length()>0){
						dos.writeInt(k.intValue());
						dos.writeUTF(v);
					}
				}
			}
			buf = bos.toByteArray();
			dos.flush();
			bos.reset();
			dos.close();
			bos.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return buf;
	}
	/*
	 * 反序列化配置信息
	 */
	public void loadBytes(byte[] buf){
		if(buf==null || buf.length<1) return;
		try{
			ByteArrayInputStream bis = new ByteArrayInputStream(buf);
			DataInputStream dis = new DataInputStream(bis);
			while(dis.available()>0){
				Integer k = new Integer(dis.readInt());
				String v = dis.readUTF();
				configs.put(k,v);
			}
			bis.close();
			dis.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	/*
	 * Load config
	 */
	public void loadString(String str){
		for(IniParser ipr = new IniParser(str); ipr.hasMoreElements();){
			ipr.next();
			if (ipr.key!=null && ipr.value!=null){
				try{
					if (ipr.value.startsWith("i.")){
						int val = Integer.parseInt(ipr.value.substring(2));
						add(Integer.parseInt(ipr.key), new Integer(val));
					} else {
						add(Integer.parseInt(ipr.key), ipr.value);
					}
				} catch (Exception e){}
			}
		}
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
