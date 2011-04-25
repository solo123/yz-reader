package com.yazo.application;

import com.yazo.books.*;
import com.yazo.ui.*;
import javax.microedition.lcdui.*;

public class Browser extends Canvas{
	private BookManager book_manager;
	private HeaderZone header_zone;
	private MainZone main_zone;
	private MenuZone menu_zone;
	int width, height, header_height, menu_height;
	
	public Browser(){
		header_height = 20;
		menu_height = 20;
		width = getWidth();
		height = getHeight();
		book_manager = new BookManager();

		header_zone = new HeaderZone(width, header_height);
		main_zone = new MainZone(width, height - header_height - menu_height);
		menu_zone = new MenuZone(width, menu_height);
		header_zone.setColor(0x7c90b3, 0xFFFFFF);
		main_zone.setColor(0xdde4ec, 0x363636);
		menu_zone.setColor(0xc2c2c2, 0);
		
		gotoUrl("Home");
	}
	private void gotoUrl(String url){
		book_manager.getPage(url);
		book_manager.content.markPages(height-header_height-menu_height-20);
		
		header_zone.setHeader(book_manager.header);
		main_zone.setContent(book_manager.content);
		menu_zone.setBrowseMenu();
		repaint();
	}

	protected void paint(Graphics g) {
		g.drawImage(header_zone.image, 0, 0, Graphics.TOP|Graphics.LEFT);
		g.drawImage(main_zone.image, 0, header_height, Graphics.TOP|Graphics.LEFT);
		g.drawImage(menu_zone.image, 0, height, Graphics.BOTTOM|Graphics.LEFT);
	}
	
	public void keyReleased(int keyCode) {
		int action = getGameAction(keyCode);
		System.out.println(" action:" + action + ", keycode:" + keyCode);
		//menus.keyAction(keyCode);
		
		if (keyCode == -1){
			main_zone.cursorUp();
		} else if (keyCode == -2) {
			main_zone.cursorDown();
		} else if (keyCode == -3) {
			main_zone.prevPage();
		} else if (keyCode == -4) {
			main_zone.nextPage();
		} else if (keyCode == -5) {
			//main_zone.onClicked();
			System.out.println("Clicked:" + main_zone.current_cmd);
			gotoUrl(main_zone.current_cmd);
		}

		repaint();
	}
}
