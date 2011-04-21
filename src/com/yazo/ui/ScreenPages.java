package com.yazo.ui;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import com.yazo.books.*;

public class ScreenPages {
	int width, height;
	BrowserContent[] contents;
	Image[] screens;
	
	public ScreenPages(int width, int height){
		this.width = width;
		this.height = height;
		screens = new Image[20];
	}
	
	public void drawContents(BrowserContent[] contents){
		this.contents = contents;
		
		screens[0] = Image.createImage(width, height);
		Graphics g = screens[0].getGraphics();
		
		g.setColor(0x00FF00);
		int posy = 10;
		for(int i=0; i<contents.length; i++){
			posy += 16;
			BrowserContent bc = contents[i];
			if (bc.content_type == "text"){
				g.drawString(bc.content, 10, posy, Graphics.BASELINE|Graphics.LEFT);
			} else if (bc.content_type == "link"){
				g.drawString(bc.content, 10, posy, Graphics.BASELINE|Graphics.LEFT);
			} else if (bc.content_type == "line"){
				g.drawLine(10, posy, width, posy);
			}
		}
		g = null;
	}
	
	public Image getPage(int page){
		return screens[page];
	}
}
