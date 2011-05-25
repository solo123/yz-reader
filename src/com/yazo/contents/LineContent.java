package com.yazo.contents;

import java.util.Vector;

import com.yazo.application.biz.Config;
import com.yazo.model.ConfigKeys;

public class LineContent {
	public BrowserContent[] lines;
	public int[] page_pos;
	public int page_count;
	public int line_count;
	public String header, back_url;
	public String url;
	public Boolean load_from_cache;
	private int line_height, view_height;
	private int chars_per_line;
	private Config config = Config.getInstance();
	
	public LineContent(){
		this.view_height = config.getInt(ConfigKeys.BROWSER_HEIGHT);
		this.line_height = config.getInt(ConfigKeys.LINE_HEIGHT); 
		chars_per_line = (config.getInt(ConfigKeys.SCREEN_WIDTH) - 20)/config.getInt(ConfigKeys.FONT_WIDTH);
		line_count = 0;
		page_pos = new int[100];
		page_count = 0;
		header = null;
		url = null;
		load_from_cache = Boolean.FALSE;
	}
	
	public void addLink(String arrow, String text, String desc, String url){
		lines[line_count++] = new LinkContent(arrow, text, desc, url);
	}
	public void addText(String text){
		if (text == null ) return;
		text = text.replace('\n', ' ');
		text = text.replace('\r', ' ');
		int len = text.length();
		int pos_idx = 0;
		int end_idx = 0;
		while(pos_idx<len && line_count<lines.length){
			end_idx = pos_idx + chars_per_line;
			if (end_idx > len) end_idx = len;  
			lines[line_count++] = new TextContent(text.substring(pos_idx, end_idx));
			pos_idx = end_idx;
		}
	}
	public void addText(String text, String color){
		addText(text);
	}
	public void addImage(String image_path, String padding){
	}
	
	public void markPages(){		
		int h = 100000;
		page_count = 0;
		for(int i=0; i<line_count; i++){
			int th = lines[i].height;
			if (th==0) th = line_height;
			
			h += th;
			if (h > view_height){
				page_pos[page_count++] = i;
				h =  th;
			} 
		}
		page_pos[page_count] = line_count;
	}
}
