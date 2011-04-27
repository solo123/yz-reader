package com.yazo.util;

/**
 * 对文件的处理
 * @author hzbiao
 *
 */
public class FileUtil {
	/**
	 * 获得xml文档的标签内的值
	 * @param xml 用于解析的xml文档
	 * @param arg	标签名
	 * @return
	 */
	public static String getArgValue(String xml,String arg) {
		String v = "";
		try{
			
			if (xml != null && !xml.equals("")) {
				v = xml.substring(xml.indexOf("<"+arg+">")+arg.length()+2, xml.indexOf("</"+arg+">"));
			}
		}catch(Exception e){
			return null;
			
		}
		return v;
	}
}
