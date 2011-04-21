package com.yazo.ui;

import com.yazo.books.*;
import javax.microedition.lcdui.*;

public class Browser extends Canvas implements CommandListener {
	BookManager book_manager;
	int width, height;
	
	public Browser(){
		book_manager = new BookManager();
		width = getWidth();
		height = getHeight();
	}

	public void commandAction(Command c, Displayable d) {
		// TODO Auto-generated method stub
		
	}

	protected void paint(Graphics g) {
		g.setColor(0xFF0000);
		g.fillRect(0,0,width,height);
		
		drawHeader(g, book_manager.header);
		drawContent(g, book_manager.contents);
	}
	private void drawHeader(Graphics g, String header_text){
		g.setColor(0x999999);
		g.drawString(header_text, 10, 20, Graphics.BASELINE|Graphics.LEFT);
	}
	private void drawContent(Graphics g, BrowserContent[] contents){
		g.setColor(0x00FF00);
		int posy = 100;
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
	}
}
