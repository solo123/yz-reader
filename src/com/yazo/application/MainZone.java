package com.yazo.application;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.yazo.books.BrowserContent;
import com.yazo.books.LineContent;
import com.yazo.books.LinkContent;
import com.yazo.ui.Zone;

public class MainZone extends Zone {
	private LineContent content;
	public int current_page;
	private int cursor, total_links;
	public String current_cmd,next_cmd;
	public int catalog_bg, text_bg;
	public Browser browser;
	private Image arrow1;
	public int line_height;
	private int font_height, line_space, line_top_padding, line_bottom_padding;
	
	public MainZone(int width, int height) {
		super(width, height);
		cursor = 0;
		total_links = 0;
		next_cmd = null;
		current_cmd = null;
		catalog_bg = 0xfdface;
		text_bg = 0xaaaaaa;
		browser = null;
		arrow1 = null;
		setFontHeight(20);
		try {
			arrow1 = Image.createImage("/arrow-blue.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void setFontHeight(int fontHeight){
		font_height = fontHeight;
		line_space =  fontHeight/4;
		line_height = font_height + line_space;
		line_top_padding = line_space/2;
		line_bottom_padding = line_space - line_top_padding;
	}
	public void setBrowser(Browser browser){
		this.browser = browser;
	}
	public void setContent(LineContent content){
		this.content = content;
		setCurrentPage(0);
	}
	public void setCurrentPage(int page){
		if (page>=0 && page<content.page_count) {
			cursor = 0;
			current_page = page;
			if (browser!=null) browser.setPageText("" + (current_page+1) + " / " + content.page_count);
			repaint();
		}
	}
	public void repaint(){
		g.setColor(bgcolor);
		g.fillRect(0, 0, width, height);
		g.setColor(color);
		int posy = line_top_padding;
		int lnk_cnt = 0;
		next_cmd = null;
		int st = content.page_pos[current_page];
		int ed = content.page_pos[current_page+1];
		for(int i=st; i<ed; i++){
			BrowserContent c = content.lines[i];
			if (c.content_type == "text") {
				g.setColor(color);
				g.drawString(c.content, 10, posy, Graphics.TOP|Graphics.LEFT);
			} else if (c.content_type == "link") {
				LinkContent lc = (LinkContent)c;
				if (lnk_cnt == cursor){
					g.setColor(color);
					g.fillRect(10, posy-line_top_padding, width-19, line_height-1);
					if(arrow1!=null) g.drawImage(arrow1, 12, posy+font_height/2, Graphics.VCENTER|Graphics.LEFT);
					g.setColor(bgcolor);
					g.drawString(lc.content, 20, posy, Graphics.TOP|Graphics.LEFT);
					next_cmd = lc.url;
				} else {
					g.setColor(color);
					g.drawString(c.content, 20, posy, Graphics.TOP|Graphics.LEFT);
					if(arrow1!=null) g.drawImage(arrow1, 12, posy+6, Graphics.TOP|Graphics.LEFT);
				}
				g.setColor(0x999999);
				g.drawLine(10, posy+line_height-line_bottom_padding, width-10, posy+line_height-line_bottom_padding);
				lnk_cnt++;
			} else if (c.content_type == "line") {
				g.drawLine(10, posy, width, posy);
			}
			posy += line_height;
		}
		total_links = lnk_cnt;
	}
	public void nextPage(){
		setCurrentPage(current_page+1);
	}
	public void prevPage(){
		setCurrentPage(current_page-1);
	}
	public void cursorUp(){
		if (cursor>0) {
			cursor--;
			repaint();
		} else{
			prevPage();
			cursor=total_links-1;
			repaint();
		}
		
	}
	public void cursorDown(){
		if (cursor<total_links-1) {
			cursor++;
			repaint();
		} else nextPage();
	}
	public void onClicked(){
	}
	
}
