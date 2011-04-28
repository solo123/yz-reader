package com.yazo.application;

import java.io.IOException;

import com.yazo.books.*;
import com.yazo.net.MultiThreadDataObject;
import com.yazo.thread.ThreadPool;
import com.yazo.thread.WaitCallback;
import com.yazo.tools.CallbackData;
import com.yazo.tools.ImageZone;
import com.yazo.tools.ThreadCallback;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;

public class Browser extends Canvas implements ThreadCallback {
	public BookManager book_manager;
	private Display display;
	private MIDlet midlet;
	private FlashCanvas flash;
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
		
		new Thread(){
			public void run(){
				init_browser();
			}
		}.start();
	}
	private void init_browser(){
		on_net_reading = Boolean.TRUE;
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

		zones = new ImageZone[3];
		header_zone = new HeaderZone();
		header_zone.setScreenSize(width, height);
		header_zone.setImageSize(width, header_height);
		header_zone.setFont(font);
		
		main_zone = new MainZone();
		main_zone.setScreenSize(width, height);
		main_zone.setImageSize(width, height - header_height - menu_height);
		main_zone.setPos(0, header_height);
		main_zone.setFont(font);

		menu_zone = new MenuZone();  //
		menu_zone.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
		menu_zone.setImageSize(width, menu_height);
		menu_zone.setPos(0, height);
		main_zone.setBrowser(this);
		header_zone.setColor(0x7c90b3, 0xFFFFFF);
		main_zone.setColor(0xdde4ec, 0x363636);
		menu_zone.setColor(0xc2c2c2, 0);
		zones[0] = header_zone;
		zones[1] = main_zone;
		zones[2] = menu_zone;
		
		gotoUrl(Configuration.content_home);
	}
	public void setPageText(String pageText){
		menu_zone.setMiddleText(pageText);
	}
	protected void paint(Graphics g) {
		for(int i=0; i<zones.length; i++){
			if (zones[i]!=null) zones[i].paint(g);
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
	
	private void after_content_loaded(LineContent lineContent){
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
		System.out.println("gotoURL:" + url);
		LineContent c = book_manager.threadGetPage(Configuration.content_server, url, this);
		if ( c!=null) after_content_loaded(c);
	}
	// Callback by other thread
	public void thread_callback(Object data) {
		CallbackData cdata = (CallbackData)data;
		if (cdata.command==null) return;
		else if (cdata.command.equals("LoadContent")) after_content_loaded((LineContent)cdata.data1);
		else if (cdata.command.equals("LoadingFromInternet")){
			on_net_reading = Boolean.TRUE;
			menu_zone.setMiddleText("正在读取网络...");
		} else if (cdata.command.equals("LoadError")){
			on_net_reading = Boolean.FALSE;
			menu_zone.setMiddleText("读取网络错误，请重试。");
		}
	}

	
}
