package com.yazo.ui;

import com.yazo.books.*;
import javax.microedition.lcdui.*;

public class Browser extends Canvas implements CommandListener {
	BookManager book_manager;
	ScreenPages pages;
	KeyMenus menus;
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
		menus = new KeyMenus(width, 20);
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
			System.out.println("ִ���ˣ�");
			g.drawImage(pages.getPage(current_page), 0, header_height, Graphics.TOP|Graphics.LEFT);
		
		for(int i=0; i<menus.image_count; i++){
			g.drawImage(menus.images[i], 0 + menus.posx[i], height - 20 + menus.posy[i], Graphics.TOP|Graphics.LEFT);
		}
		
	}
	public void keyReleased(int keyCode) {
		int action = getGameAction(keyCode);
		System.out.println(" action:" + action + ", keycode:" + keyCode);
		menus.keyAction(keyCode);
		repaint();
	}
	
	private void getStartPageContent(){
		book_manager.getPage("Home");
		pages.drawContents(book_manager.contents);
		header_text = book_manager.header;
		current_page =0;
	}
	private void drawHeader(Graphics g, String header_text){
		g.setColor(0x999999);
		g.drawString(header_text, 10, header_height - 2, Graphics.BASELINE|Graphics.LEFT);
	}
}