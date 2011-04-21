package com.yazo.books;

public class BookManager {
	public String header = "abc";
	public BrowserContent[] contents;
	
	public BookManager(){
		header = "header 文章标题 01";
		contents = new BrowserContent[3];
		contents[0] = new BrowserContent("text", "this is some text，这是一些文字。");
		contents[1] = new BrowserContent("link", "a link,一个连接", "1189.jpg");
		contents[2] = new BrowserContent("line", null);
	}
	public BrowserContent[] getHomePage(){
		BrowserContent[] contents = new BrowserContent[11];
		contents[0] = new BrowserContent("text", "this is some text，这是一些文字。");
		contents[1] = new BrowserContent("link", "a link,一个连接", "1189.jpg");
		contents[2] = new BrowserContent("line", null);
		contents[3] = new BrowserContent("link", "a link,一个连接", "1189.jpg");
		contents[4] = new BrowserContent("line", null);
		contents[5] = new BrowserContent("link", "a link,一个连接", "1189.jpg");
		contents[6] = new BrowserContent("line", null);
		contents[7] = new BrowserContent("link", "a link,一个连接", "1189.jpg");
		contents[8] = new BrowserContent("line", null);
		contents[9] = new BrowserContent("text", "this is some short text，这是一些短文字。");
		contents[10] = new BrowserContent("text", "this is some long text，这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。这是一些长文字。");
		return contents;
	}
	
}
