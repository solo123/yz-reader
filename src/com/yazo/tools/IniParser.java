package com.yazo.tools;

public class IniParser {
	public String key, value;
	private String parseText;
	private int idx, length;
	public IniParser(String string) {
		parseText = string;
		key = value = null;
		idx = 0;
		length = string.length();
	}

	public boolean hasMoreElements() {
		return (idx<length-2);
	}

	public void next() {
		key = value = null;
		while(idx<length && (parseText.charAt(idx)=='\n' || parseText.charAt(idx)=='\r')) idx++;
		int st = idx;
		int ed = parseText.indexOf('\n', st);
		if (ed<0) ed = length;
		int er = parseText.indexOf('\r', st);
		if (er>=0 && er<ed) ed = er;
		int sp = parseText.indexOf('=', st);
		if(sp>0 && sp<ed){
			key = parseText.substring(idx, sp);
			value = parseText.substring(sp+1, ed);
		}
		idx = ed+1;
	}

}
