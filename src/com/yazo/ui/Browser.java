package com.yazo.ui;

import com.yazo.books.*;
import javax.microedition.lcdui.*;

public class Browser extends Canvas implements CommandListener {
	BookManager book_manager;
	ScreenPages pages;
	String header_text;
	int width, height, header_height, current_page;
	
	public Browser(){
		width = getWidth();
		height = getHeight();
		header_height = 20;
		current_page =0;
		header_text = null;
		book_manager = new BookManager();
		pages = new ScreenPages(width, height-header_height);
		getStartPageContent();
	}
	public void commandAction(Command c, Displayable d) {
		// TODO Auto-generated method stub
	}

	protected void paint(Graphics g) {
		g.setColor(0xFF0000);
		g.fillRect(0,0,width,height);
		
		drawHeader(g, book_manager.header);
		Image img = pages.getPage(current_page);
		if (img != null )
			System.out.println("Ö´ÐÐÁË£¿");
			g.drawImage(pages.getPage(current_page), 0, header_height, Graphics.TOP|Graphics.LEFT);
	}
	public void keyReleased(int keyCode) {
		System.out.println(" KEY:" + keyCode);
		repaint();
	}
	
	private void getStartPageContent(){
		pages.drawContents(book_manager.getHomePage());
		header_text = "ÄÚÈÝ Home";
		current_page =0;
	}
	private void drawHeader(Graphics g, String header_text){
		g.setColor(0x999999);
		g.drawString(header_text, 10, header_height - 2, Graphics.BASELINE|Graphics.LEFT);
	}
}
