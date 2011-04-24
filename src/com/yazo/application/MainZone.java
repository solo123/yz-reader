package com.yazo.application;

import javax.microedition.lcdui.Graphics;

import com.yazo.books.BrowserContent;
import com.yazo.books.LineContent;
import com.yazo.ui.Zone;

public class MainZone extends Zone {
	private LineContent content;
	private int current_page, cursor, total_links;
	public String current_cmd;
	
	public MainZone(int width, int height) {
		super(width, height);
		cursor = 0;
		total_links = 0;
		current_cmd = null;
	}
	public void setContent(LineContent content){
		this.content = content;
		setCurrentPage(0);
	}
	public void setCurrentPage(int page){
		if (page>=0 && page<content.page_count) {
			cursor = 0;
			current_page = page;
			repaint();
		}
	}
	public void repaint(){
		g.setColor(bgcolor);
		g.fillRect(0, 0, width, height);
		g.setColor(color);
		int posy = 20;
		int lnk_cnt = 0;
		current_cmd = null;
		int st = content.page_pos[current_page];
		int ed = content.page_pos[current_page+1];
		for(int i=st; i<ed; i++){
			BrowserContent c = content.lines[i];
			if (c.content_type == "text") {
				g.drawString(c.content, 10, posy, Graphics.BASELINE|Graphics.LEFT);
			} else if (c.content_type == "link") {
				if (lnk_cnt == cursor){
					g.setColor(color);
					g.fillRect(10, posy-15, width-19, 19);
					g.setColor(bgcolor);
					g.drawString(c.content, 10, posy, Graphics.BASELINE|Graphics.LEFT);
					current_cmd = c.content_value;
				} else {
					g.setColor(color);
					g.drawString(c.content, 10, posy, Graphics.BASELINE|Graphics.LEFT);
				}
				g.setColor(0x999999);
				g.drawLine(10, posy+4, width-10, posy+4);
				lnk_cnt++;
			} else if (c.content_type == "line") {
				g.drawLine(10, posy, width, posy);
			}
			posy += 20;
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
		}
	}
	public void cursorDown(){
		if (cursor<total_links-1) {
			cursor++;
			repaint();
		}
	}
	public void onClicked(){
		System.out.println("Clicked:" + current_cmd);

	}
	
}
