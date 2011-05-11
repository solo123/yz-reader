package com.yazo.contents;

import java.util.Vector;
import javax.microedition.lcdui.Font;

public class PageContent {
	private Vector pages;
	private Vector contents;
	private int width, height, default_lineheight, current_content_height, chars_per_line;
	private Font font;
	public String header, url;
	public Boolean load_from_cache;
	public Vector menus;
	public LinkContent rightKeyMenu = null;
	
	public PageContent(int width, int height, int default_lineheight, Font default_font){
		this.width = width;
		this.height = height;
		this.default_lineheight = default_lineheight;
		this.font = default_font;
		pages = new Vector();
		menus = new Vector();
		current_content_height = 100000;
		chars_per_line = (width - 20)/font.charWidth('国');
	}
	public void addContent(BrowserContent content){
		int h = content.height;
		if (h==0) h = default_lineheight;
		if(current_content_height>0 && current_content_height + h >= height){
			contents = new Vector();
			pages.addElement(contents);
			current_content_height = 0;
		}
		contents.addElement(content);
		current_content_height += h;
	}
	
	public void addLink(String arrow, String text, String desc, String url){
		addContent(new LinkContent(arrow, text, desc, url));
	}
	public int getTotalPages(){
		if (pages==null) return 0;
		else return pages.size();
	}
	public Vector getPage(int page){
		if(pages!=null && page<pages.size())
			return (Vector)pages.elementAt(page);
		else
			return null;
	}
	public void addText(String text){
		if (text == null || chars_per_line<1 ) return;
		int len = text.length();
		int pos_idx = 0;
		int end_idx = 0;
		while(pos_idx<len){
			end_idx = pos_idx + chars_per_line;
			if (end_idx > len) end_idx = len;  
			addContent(new TextContent(text.substring(pos_idx, end_idx)));
			pos_idx = end_idx;
		}
	}

}
