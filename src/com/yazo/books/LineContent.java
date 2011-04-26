package com.yazo.books;

public class LineContent {
	public int line_char_width;  //每行字数
	public BrowserContent[] lines;
	public int[] page_pos;
	public int page_count;
	public int line_count;
	public String header;
	
	public LineContent(int width){
		line_char_width = width;
		lines = new BrowserContent[1000];
		line_count = 0;
		page_pos = new int[100];
		page_count = 0;
		header = null;
	}
	
	public void addLink(String arrow, String text, String desc, String url){
		lines[line_count++] = new LinkContent(arrow, text, desc, url);
	}
	public void addText(String text){
		if (text == null ) return;
		int pos = 0;
		int len = text.length();
		int end_idx = 0;
		while(pos<len && line_count<lines.length){
			if (len - pos > line_char_width) 
				end_idx = pos + line_char_width;
			else
				end_idx = len;  
			lines[line_count++] = new TextContent(text.substring(pos, end_idx));
			pos = end_idx;
		}
	}
	public void addText(String text, String color){
		addText(text);
	}
	public void addImage(String image_path, String padding){
		
	}
	public void markPages(int page_height){
		int h = 100000;
		page_count = 0;
		for(int i=0; i<line_count; i++){
			if (h + 20 > page_height-20){
				page_pos[page_count++] = i;
				h = 20;
			} else
				h += 20;
		}
		page_pos[page_count] = line_count;
	}
}
