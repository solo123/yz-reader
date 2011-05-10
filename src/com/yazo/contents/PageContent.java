package com.yazo.contents;

import java.util.Vector;

import com.sun.perseus.model.Font;

public class PageContent {
	private Vector pages;
	private Vector contents;
	private int width, height, default_lineheight, current_content_height;
	private Font font;
	
	public PageContent(int width, int height, int default_lineheight, Font default_font){
		this.width = width;
		this.height = height;
		this.default_lineheight = default_lineheight;
		this.font = default_font;
		pages = new Vector();
		current_content_height = 100000;
	}
	public void addContent(BrowserContent content){
		if(current_con)
	}
	
	public void addLink(String arrow, String text, String desc, String url){
		LinkContent lc = new LinkContent(arrow, text, desc, url);
		if(current_content_height>0 && current_content_height + lc.height > height){
			
		}
	}

}
