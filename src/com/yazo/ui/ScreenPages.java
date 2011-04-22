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
		
	}

	public void drawContents(BrowserContent[] contents) {
		this.contents = contents;
//		screens[0]=Image.createImage(width, height);
//		
		// Image ime = Image.createImage(width,height);
		// Graphics g = ime.getGraphics();
		
			current=Image.createImage(width, height);
			Graphics g = current.getGraphics();
			g.setColor(0x00FF00);
		for (int i = 0; i < contents.length; i++) {
			
			BrowserContent bc = contents[i];
			Font f = Utils.f;
			if (bc.content_type == "text") {
				int string_width = f.stringWidth(bc.content);// 一个content的长度
				if (width >= string_width) {
					
					lines=lines+1;
					g.drawString(bc.content, 10, posy, Graphics.BASELINE
							| Graphics.LEFT);
					if (lines == 17) {
						current=this.setPage(g,current);
						g=null;
						g=current.getGraphics();
						lines=0;
					}
				} else {
					Vector v = new Vector();
					v = Utils.splitStr(f, bc.content, width);
					for (int j = 0; j < v.size(); j++) {
						lines=lines+1;
						g.drawString((String) v.elementAt(j), 10, posy,	Graphics.BASELINE | Graphics.LEFT);
						if (lines == 17) {
							current=this.setPage(g,current);
							g=null;
							g=current.getGraphics();
							lines=0;
						}
						posy += 16;
					}
				}
			} else if (bc.content_type == "link") {
				lines=lines+1;
				g.drawString(bc.content, 10, posy, Graphics.BASELINE
						| Graphics.LEFT);
				if (lines ==17) {
					current=this.setPage(g,current);
					g=null;
					g=current.getGraphics();
					lines=0;
				}
			} else if (bc.content_type == "line") {
				lines=lines+1;
				g.drawLine(10, posy, width, posy);
				if (lines == 17) {
					current=this.setPage(g,current);
					g=null;
					g=current.getGraphics();
					lines=0;
				}
			}
			posy += 16;
			
		}
		g = null;
		}

	public Image getPage(int page) {
		return screens[page];
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
