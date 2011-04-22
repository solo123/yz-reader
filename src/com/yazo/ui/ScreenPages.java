package com.yazo.ui;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import com.yazo.books.*;
import com.yazo.util.Utils;

public class ScreenPages {
	int width, height;
	LineContent content;
	int[] pager_points;
	public int current_page;

	Image[] screens ;
	 int lines = 1;
	static int pages = 0;
	static int posy = 0;
	static int current_image = 0;
	Image current;
	Image current1;
	public ScreenPages(int width, int height) {
		this.width = width;
		this.height = height;
		screens = new Image[20];
		pager_points = new int[100];
		current_page = 0;
	}
	public void LoadContent(LineContent content){
		this.content = content;
	}
	public Image getCurrentPage(){
		return getPage(current_page);
	}
	public Image getPage(int page) {
		Image img = Image.createImage(width, height);
		Graphics g = img.getGraphics();
		g.setColor(0x000000);
		if (page<0 || page>content.page_count) return img;
		
		int posy = 30;
		int st = content.page_pos[page];
		int ed = content.page_pos[page+1];
		for(int i=st; i<ed; i++){
			Font f = Utils.f;
			BrowserContent c = content.lines[i];
			if (c.content_type == "text") {
				g.drawString(c.content, 10, posy, Graphics.BASELINE|Graphics.LEFT);
			} else if (c.content_type == "link") {
				g.drawString(c.content, 10, posy, Graphics.BASELINE|Graphics.LEFT);
			} else if (c.content_type == "line") {
				g.drawLine(10, posy, width, posy);
			}
			posy += 20;
		}
		g = null;
		return img;
	}


	public Image setPage(Graphics g,Image current) {
//		screens[pages-1]=screens[current_image];
//		screens[current_image]=null;
		g.drawString("上一页", 10, posy+=16, Graphics.BOTTOM|Graphics.LEFT);
		g.drawString("下一页",10,posy+=16,Graphics.BOTTOM|Graphics.RIGHT);
		pages+=1;
		screens[pages-1]=current;
		current1 = Image.createImage(width,height);
		return current1;
	}
}
