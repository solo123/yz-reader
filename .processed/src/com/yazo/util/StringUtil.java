package com.yazo.util;

import java.util.Vector;

/**
 * 提供�?��常用字符串处理方�?
 * 
 * @author JoyCode workshop
 * 
 */
public class StringUtil {
	/**
	 * 切割str字符�?
	 * 
	 * @param str
	 * @param regex
	 * @return
	 */
	public static String[] split(String bufferstr, String regex) {
		if (bufferstr == null)
			return null;
		Vector split = new Vector();
		while (true) {
			int index = bufferstr.indexOf(regex);
			if (index == -1) {
				if (bufferstr != null && !bufferstr.equals(""))
					split.addElement(bufferstr);
				break;
			}
			split.addElement(bufferstr.substring(0, index));
			bufferstr = bufferstr.substring(index + 1, bufferstr.length());
		}
		String[] s = new String[split.size()];
		split.copyInto(s);
		split.removeAllElements();
		split = null;
		return s;
	}

	/**
	 * �?6进制字符串转换成字节数组
	 * 
	 * @param hex
	 * @return
	 */
	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) ((Byte
					.parseByte(hex.substring(pos, pos + 1), 16) << 4) | Byte
					.parseByte(hex.substring(pos + 1, pos + 2), 16));
		}
		return result;
	}

	/**
	 * 对url的切�?
	 * 
	 * @param url
	 * @return
	 */
	public static String[] splitUrl(String url) {
		String shema = "http://";
		String[] urls = new String[2];
		int shemaLen = shema.length();
		int posStart = url.toLowerCase().indexOf(shema);
		int posEnd;
		if (posStart == -1) {
			return null;
		}
		posEnd = url.indexOf("/", shemaLen);
		if (posEnd == -1) {
			urls[0] = url.substring(shemaLen, url.length());
			urls[1] = "/";
		} else {
			urls[0] = url.substring(shemaLen, posEnd);
			urls[1] = url.substring(posEnd);
		}
		return urls;
	}
}
