package com.yazo.contents;

public class LinkContent extends BrowserContent {
	public String desc,url;
	public String arrow_style;
	public int short_key = -1;
	
	public LinkContent(String content, String url){
		this.content_type = "link";
		this.content = content;
		this.desc = null;
		this.arrow_style = null;
		this.url = url;
		this.short_key = getShortKey();
	}
	public LinkContent(String content, String desc, String url){
		this.content_type = "link";
		this.content = content;
		this.desc = desc;
		this.url = url;
		this.arrow_style = null;
		this.short_key = getShortKey();
	}
	public LinkContent(String arrow_style, String content, String desc, String url){
		this.arrow_style = arrow_style;
		this.content_type = "link";
		this.content = content;
		this.desc = desc;
		this.url = url;
		this.short_key = getShortKey();
	}
	private int getShortKey(){
		if (content==null || content.length()<2) return -1;
		if (content.charAt(1)== '.'){
			char c = content.charAt(0);
			int k = (int)c - 48;
			if (k>=0 && k<=9) return k;
		}
		return -1;
	}
}
