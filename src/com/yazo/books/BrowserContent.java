package com.yazo.books;

public class BrowserContent {
	public String content_type;
	public String content;
	public String content_value;
	
	public BrowserContent(String type, String content){
		this.content_type = type;
		this.content = content;
	}
	public BrowserContent(String type, String content, String val){
		this.content_type = type;
		this.content = content;
		this.content_value = val; 
	}
}
