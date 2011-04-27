package com.yazo.application;

import java.io.IOException;

import com.yazo.books.*;
import com.yazo.net.ContentServer;

import javax.microedition.lcdui.*;

public class Browser extends Canvas{
	public BookManager book_manager;
	private HeaderZone header_zone;
	private MainZone main_zone;
	private MenuZone menu_zone;
	int width, height, header_height, menu_height;
	private String[] history;
	private int history_count ;
	private Font font;
	private Boolean network_init;
	
	private ContentServer cs;
	
	public Browser(){
		network_init = Boolean.FALSE;
		cs = new ContentServer();
		
		setFullScreenMode(true);
		font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
		int font_height = font.getHeight();
		int font_width  = font.charWidth('国');
		
		history = new String[10];
		history_count = 0;
		header_height = font_height + 6;
		menu_height = header_height;
		
		width = getWidth();
		height = getHeight();
		book_manager = new BookManager();
		book_manager.line_chars = (width - 20)/font_width;
		System.out.println("line chars:" + book_manager.line_chars + ", font width:"+ font_width + ", font height:" + font_height);

		header_zone = new HeaderZone(width, header_height);
		main_zone = new MainZone(width, height - header_height - menu_height);
		main_zone.setFontHeight(font_height);
		menu_zone = new MenuZone(width, menu_height);
		main_zone.setBrowser(this);
		header_zone.setColor(0x7c90b3, 0xFFFFFF);
		main_zone.setColor(0xdde4ec, 0x363636);
		menu_zone.setColor(0xc2c2c2, 0);
		//gotoUrl(Configuration.content_home);
		cs.getWebContent(Configuration.content_home, this);
	}
	public void onLineContentUpdated(LineContent lineContent){
		book_manager.content = lineContent;
		book_manager.content.line_height = main_zone.line_height;
		book_manager.content.markPages(height-header_height-menu_height-20);
		
		header_zone.setHeader(book_manager.content.header);
		main_zone.setContent(book_manager.content);
		menu_zone.repaint_bar();
		this.network_init = Boolean.TRUE;
		repaint();
	}
	private void gotoUrl(String url){
		main_zone.current_cmd=url;
		book_manager.getPage(url);
		book_manager.content.line_height = main_zone.line_height;
		book_manager.content.markPages(height-header_height-menu_height-20);
		
		header_zone.setHeader(book_manager.header);
		main_zone.setContent(book_manager.content);
		menu_zone.repaint_bar();
		this.network_init = Boolean.TRUE;
		repaint();
	}
	public void setPageText(String pageText){
		menu_zone.setMiddleText(pageText);
	}

	protected void paint(Graphics g) {
		if (network_init == Boolean.FALSE){
			Image splash;
			try {
				splash = Image.createImage("/ebook.jpg");
				g.drawImage(splash, 0, 0, Graphics.TOP|Graphics.LEFT);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			g.drawImage(header_zone.image, 0, 0, Graphics.TOP|Graphics.LEFT);
			g.drawImage(main_zone.image, 0, header_height, Graphics.TOP|Graphics.LEFT);
			g.drawImage(menu_zone.image, 0, height, Graphics.BOTTOM|Graphics.LEFT);
			if(menu_zone.state>0){ //pop menu opened
				g.drawImage(menu_zone.menuShadowImage, 7, height-21, Graphics.BOTTOM|Graphics.LEFT);
				g.drawImage(menu_zone.menuImage, 4, height-21-3, Graphics.BOTTOM|Graphics.LEFT);
			}
		}
	}
	
	public void keyReleased(int keyCode) {
		int action = getGameAction(keyCode);
		System.out.println(" action:" + action + ", keycode:" + keyCode);
		if (menu_zone.state>0){
			menuKeyAction(keyCode);
		} else {
			mainKeyAction(keyCode);
		}
		repaint();
	}
	private void menuKeyAction(int keyCode){
		if (keyCode == -1){
			menu_zone.cursorUp();
		} else if (keyCode == -2) {
			menu_zone.cursorDown();
		} else if (keyCode == -3) {
			menu_zone.cursorLeft();
		} else if (keyCode == -4) {
			menu_zone.cursorRight();
		} else if (keyCode == -5) {
			//TODO: menu action, gotoUrl(main_zone.current_cmd);
		} else if (keyCode == -6){
			menu_zone.activeMenu();
		} else if (keyCode == -7){
			menu_zone.activeMenu();
		}
	}
	private void mainKeyAction(int keyCode){
		if (keyCode == -1){
			main_zone.cursorUp();
		} else if (keyCode == -2) {
			main_zone.cursorDown();
		} else if (keyCode == -3) {
			main_zone.prevPage();
		} else if (keyCode == -4) {
			main_zone.nextPage();
		} else if (keyCode == -5) {
			if(main_zone.next_cmd!=null){
				history[history_count++]=main_zone.current_cmd;
				menu_zone.setRightMenuText("返回");
				gotoUrl(main_zone.next_cmd);
			}
		} else if (keyCode == -6){
			menu_zone.activeMenu();
		} else if (keyCode == -7){
			//TODO: back or quick
			if(history_count>0){
				gotoUrl(history[--history_count]);
			} else {
				menu_zone.setRightMenuText("退出");
			}
			
		} else if (keyCode == 49){
			try {
				cs.connect(10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (keyCode == 50){
			cs.printState();
		}
			
	}
	
}
