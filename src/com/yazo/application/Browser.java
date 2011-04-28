package com.yazo.application;

import java.io.IOException;

import com.yazo.books.*;
import com.yazo.net.ContentServer;
import com.yazo.tools.ImageZone;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;

public class Browser extends Canvas{
	public BookManager book_manager;
	private Display display;
	private MIDlet midlet;
	private FlashCanvas flash;
	private ContentServer content_server;
	private ImageZone[] zones;
	private HeaderZone header_zone;
	private MainZone main_zone;
	private MenuZone menu_zone;
	int width, height, header_height, menu_height;
	private Font font;
	private Boolean on_net_reading;
	
	public Browser(MIDlet midlet, Display display){
		this.display = display;
		this.midlet = midlet;
		flash = new FlashCanvas(midlet);
		display.setCurrent(flash);
		on_net_reading = Boolean.TRUE;
		
		content_server = new ContentServer();
		setFullScreenMode(true);
		font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
		int font_height = font.getHeight();
		int font_width  = font.charWidth('国');
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
		gotoUrl(Configuration.content_home);
	}
	public void onLineContentUpdated(LineContent lineContent){
		book_manager.content = lineContent;
		book_manager.content.line_height = main_zone.line_height;
		book_manager.content.markPages(height-header_height-menu_height-20);
		
		header_zone.setHeader(book_manager.content.header);
		main_zone.setContent(book_manager.content);
		menu_zone.repaint_bar();
		on_net_reading = Boolean.FALSE;
		repaint();
	}
	private void gotoUrl(String url){
		on_net_reading = Boolean.TRUE;
		content_server.getWebContent(url, this);
		menu_zone.setMiddleText("读取网络....");
	}

	public void setPageText(String pageText){
		menu_zone.setMiddleText(pageText);
	}

	protected void paint(Graphics g) {
		g.drawImage(header_zone.image, 0, 0, Graphics.TOP|Graphics.LEFT);
		g.drawImage(main_zone.image, 0, header_height, Graphics.TOP|Graphics.LEFT);
		g.drawImage(menu_zone.image, 0, height, Graphics.BOTTOM|Graphics.LEFT);
		if(menu_zone.state>0){ //pop menu opened
			g.drawImage(menu_zone.menuShadowImage, 7, height-21, Graphics.BOTTOM|Graphics.LEFT);
			g.drawImage(menu_zone.menuImage, 4, height-21-3, Graphics.BOTTOM|Graphics.LEFT);
		}
	}
	
	public void keyReleased(int keyCode) {
		if (on_net_reading == Boolean.TRUE){
			System.out.println("Skip where reading on net...");
			return;
		}
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
				gotoUrl(main_zone.next_cmd);
			}
		} else if (keyCode == -6){
			menu_zone.activeMenu();
		} else if (keyCode == -7){
			if (main_zone.back_url==null){
				menu_zone.setRightMenuText("退出");
			} else gotoUrl(main_zone.back_url);
		}
			
	}
	
}
