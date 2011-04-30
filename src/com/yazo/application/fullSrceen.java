package com.yazo.application;

import javax.microedition.lcdui.Graphics;

import com.yazo.books.BookManager;

public class fullSrceen extends Browser {
	public fullSrceen(String url){
		setFullScreenMode(true);
		init();
		history = new String[10];
		history_count = 0;
		header_height = 20;
		menu_height = 20;
		width = getWidth();
		height = getHeight();
		book_manager = new BookManager();

		header_zone = new HeaderZone(width, header_height);
		main_zone = new MainZone(width, height);
		menu_zone = new MenuZone(width, menu_height);
		header_zone.setColor(0x7c90b3, 0xFFFFFF);
		main_zone.setColor(0xdde4ec, 0x363636);
		menu_zone.setColor(0xc2c2c2, 0);
	}
	protected void paint(Graphics g){
		setFullScreenMode(true);
		g.drawImage(main_zone.image,0,0, Graphics.TOP
				| Graphics.LEFT);
	}
}
