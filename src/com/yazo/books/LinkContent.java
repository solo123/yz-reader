package com.yazo.books;

public class LinkContent extends BrowserContent {
	public String desc,url;
	public String arrow_style;
	public LinkContent(String content, String desc, String url){
		this.content_type = "link";
		this.content = content;
		this.desc = desc;
		this.url = url;
		arrow_style = null;
	}
	public LinkContent(String arrow_style, String content, String desc, String url){
		this.arrow_style = arrow_style;
		this.content_type = "link";
		this.content = content;
		this.desc = desc;
		this.url = url;
	}

}
