package com.yazo.application;

import javax.microedition.lcdui.*;
import com.yazo.books.BookMenu;
import com.yazo.ui.Zone;

public class MenuZone extends Zone {
	public int state;
	public Image menuImage, menuShadowImage;
	private Graphics g1;
	private int menu_width, menu_height, cursor, max_items;
	private BookMenu book_menu;
	
	public MenuZone(int width, int height) {
		super(width, height);
		state = 0;
		cursor = 0;
		max_items = 5;
		menu_width = 120;
		menu_height = 110;
		menuImage = Image.createImage(menu_width, menu_height);
		g1 = menuImage.getGraphics();
		menuShadowImage = Image.createImage(menu_width, menu_height);
		Graphics g0 = menuShadowImage.getGraphics();
		g0.setColor(0x666666);
		g0.fillRect(0, 0, menu_width, menu_height);
		g0 = null;
		book_menu = new BookMenu();
	}
	
	
	public void setBrowseMenu(){
		g.setColor(bgcolor);
		g.fillRect(0, 0, width, height);
		
		g.setColor(color);
		g.drawString("菜单", 4, 2, Graphics.TOP|Graphics.LEFT);
		g.drawString("退出", width-4, 2, Graphics.TOP|Graphics.RIGHT);
		repaint();
		state = 0;
	}
	public void repaint(){
		int leftGrid = 20;
		g1.setColor(0x999999);
		g1.drawRect(0, 0, menu_width-1, menu_height-1);
		g1.setColor(0xffffff);
		g1.drawRect(1, 1, menu_width-3, menu_height-3);
		g1.setColor(0xeeeeee);
		g1.fillRect(2, 2, menu_width-4, menu_height-4);

		// current line
		System.out.println("menu cursor:"+cursor);
		g1.setColor(0xa8d8eb);
		g1.drawRoundRect(5, 2+20*cursor, menu_width-10, 20, 5, 5);
		g1.setColor(0xe0edf3);
		g1.fillRect(6, 3+20*cursor, menu_width-10-2, 18);
		g1.setColor(0x333333);
		g1.fillRect(11, 2+8+20*cursor, 6, 6);
		
		// left grid line
		g1.setColor(0xdddddd);
		g1.drawLine(leftGrid, 3, leftGrid, menu_height-3);
		g1.setColor(0xffffff);
		g1.drawLine(leftGrid+1, 3, leftGrid+1, menu_height-3);
		
		g1.setColor(0);
		for(int i=0; i<book_menu.items.length; i++){
			g1.drawString(book_menu.items[i], leftGrid+5, 5+20*i, Graphics.TOP|Graphics.LEFT);	
		}
	}
	public void activeMenu(){
		if (state==0)
			state = 1;
		else
			state = 0;
	}
	public void cursorUp(){
		if (cursor>0){ 
			cursor--;
			repaint();
		}
	}
	public void cursorDown(){
		if (cursor<max_items-1){
			cursor++;
			repaint();
		}
	}
	public void cursorLeft(){
		activeMenu();
	}
	public void cursorRight(){
		//TODO: run command
	}
	

}
