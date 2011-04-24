package com.yazo.util;

import java.util.Vector;

import javax.microedition.lcdui.Font;

public class Utils {
	public static Vector splitStr(Font f,String str,int length){
		if(str==null){
			return null;
		}
		Vector result = new Vector();
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
		return result;
	}
}
