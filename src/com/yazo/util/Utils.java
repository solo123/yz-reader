package com.yazo.util;

import java.util.Vector;

import javax.microedition.lcdui.Font;

public class Utils {
	public static Font f = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
	public static Vector splitStr(Font f,String str,int length){
		System.out.println("调用换行");
		if(str==null){
			return null;
		}
		Vector result = new Vector();
		System.out.println("传入的字符串为："+str);
		char[] tempChars = str.toCharArray();
		int lengthPX = 0;
		StringBuffer b = new StringBuffer();
		for(int i= 0;i<tempChars.length;i++){
			lengthPX = lengthPX+f.charWidth(tempChars[i]);
			if(lengthPX>length||tempChars[i]=='\n'){
				result.addElement(b.toString());
				b = new StringBuffer();
				lengthPX = f.charWidth(tempChars[i]);
				if(tempChars[i]!='\n'){
					b.append(tempChars[i]);
				}
			}else{
				b.append(tempChars[i]);
			}
		}
		if(lengthPX>0){
			result.addElement(b.toString());
		}
		//打印出所有的字符
		for(int i = 0;i<result.size();i++){
			System.out.println("result========"+(String)result.elementAt(i));
		}
		return result;
	}
}
