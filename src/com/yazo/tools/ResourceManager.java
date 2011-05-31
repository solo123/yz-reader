package com.yazo.tools;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class ResourceManager {
	public String getTextFromRes(String resName,boolean isUTF8){
		String str="";
		  InputStream in = null;
	        DataInputStream dis = null;
	        byte[] data = null;
	        in=this.getClass().getResourceAsStream(resName);
	        dis = new DataInputStream(in);
	        try {
	        	data=new byte[dis.available()];
	        	dis.read(data);				
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try{
					dis.close();
					in.close();
					
				}catch(Exception e){
					
				}
			}
			if(isUTF8){
				
				try {
					str=new String(data,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}else{
				str=new String(data);
			}
			data=null;
			return str;
	}
}
