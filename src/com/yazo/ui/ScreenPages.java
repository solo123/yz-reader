package com.yazo.ui;

import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import com.yazo.books.*;
import com.yazo.util.Utils;

public class ScreenPages {
	int width, height;
	BrowserContent[] contents;
	Image[] screens;
	int lines;
	int pages;
	static int posy = 0;

	public ScreenPages(int width, int height) {
		this.width = width;
		this.height = height;
		screens = new Image[20];
		pages = 0;
		lines = 0;
	}

	public void drawContents(BrowserContent[] contents) {
		this.contents = contents;
		// screens[0]=Image.createImage(width, height);
		//
		// Image ime = Image.createImage(width,height);
		// Graphics g = ime.getGraphics();

		screens[pages] = Image.createImage(width, height);
		Graphics g = screens[pages].getGraphics();
		g.setColor(0x00FF00);
		for (int i = 0; i < contents.length; i++) {
			BrowserContent bc = contents[i];
			Font f = Utils.f;
			if (bc.content_type == "text") {
				int string_width = f.stringWidth(bc.content);// 一个content的长度
				if (width >= string_width) {
					lines = lines + 1;
					g.drawString(bc.content, 10, posy, Graphics.BASELINE
							| Graphics.LEFT);
					if(lines ==17){
						this.setPage(g,screens[pages]);
					}
				} else {
					Vector v = new Vector();
					v = Utils.splitStr(f, bc.content, width);
					for (int j = 0; j < v.size(); j++) {
						lines = lines + 1;
						g.drawString((String) v.elementAt(j), 10, posy,
								Graphics.BASELINE | Graphics.LEFT);
						if(lines ==17){
							this.setPage(g,screens[pages]);
						}
						posy += 16;
					}
				}
			} else if (bc.content_type == "link") {
				lines = lines + 1;
				g.drawString(bc.content, 10, posy, Graphics.BASELINE
						| Graphics.LEFT);
				if(lines ==17){
					this.setPage(g,screens[pages]);
				}
			} else if (bc.content_type == "line") {
				lines = lines + 1;
				g.drawLine(10, posy, width, posy);
				if(lines ==17){
					this.setPage(g,screens[pages]);
				}
			}
		}
		screens[pages]=screens[pages];
		g = null;
	}

	public Image getPage(int page) {
		return screens[page];
	}

	public int getpages() {
		return pages += 1;
	}

	public void setPage(Graphics g,Image ima) {
		// screens[pages-1]=screens[current_image];
		// screens[current_image]=null;
		g.drawString("上一页", 10, posy += 16, Graphics.BOTTOM | Graphics.LEFT);
		g.drawString("下一页", 10, posy += 16, Graphics.BOTTOM | Graphics.RIGHT);
		screens[this.getpages()-1]=ima;
		ima=null;
		lines =1;
		
	}
}
