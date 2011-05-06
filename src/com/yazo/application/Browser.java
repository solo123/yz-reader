package com.yazo.application;

import com.yazo.books.*;
import com.yazo.model.BrowserCommand;
import com.yazo.model.ICommandManager;
import com.yazo.tools.CallbackData;
import com.yazo.tools.ImageZone;
import com.yazo.tools.ThreadCallback;

import javax.microedition.lcdui.*;

public class Browser extends Canvas implements ThreadCallback, ICommandManager {
	public BookManager book_manager;
	private Display display;
	private FlashCanvas flash;
	private ImageZone[] zones;
	private HeaderZone header_zone;
	private MainZone main_zone;
	private MenuZone menu_zone;
	private Boolean on_net_reading;
	private HistoryManager history_manager = new HistoryManager();
	
	public Browser(MainMIDlet midlet, Display display){
		this.display = display;
		flash = new FlashCanvas(midlet);
		display.setCurrent(flash);
		
		new Thread(){
			public void run(){
				init_browser();
			}
		}.start();
	}
	private void init_browser(){
		setFullScreenMode(true);
		on_net_reading = Boolean.TRUE;
		Configuration.SetFontSize(Font.SIZE_MEDIUM);
		Configuration.SCREEN_WIDTH = getWidth();
		Configuration.SCREEN_HEIGHT = getHeight();
		
		book_manager = new BookManager();
		zones = new ImageZone[3];
		zones[0] = header_zone = new HeaderZone();
		zones[1] = main_zone = new MainZone(this);
		zones[2] = menu_zone = new MenuZone(this);
		
		gotoUrl(Configuration.CONTENT_HOME);
	}
	protected void paint(Graphics g) {
// #ifdef DBG		
		System.out.println("Browser repainted.");
// #endif
		for(int i=0; i<zones.length; i++){
			if (zones[i]!=null) zones[i].paint(g);
		}
	}
	public void keyReleased(int keyCode) {
		if (on_net_reading == Boolean.TRUE){
// #ifdef DBG_NET
			System.out.println("Skip where reading on net...");
// #endif
			return;
		}
// #ifdef DBG_KEYS
		int action = getGameAction(keyCode);
		System.out.println(" action:" + action + ", keycode:" + keyCode);
// #endif
		if (menu_zone.state>0){
			menu_zone.keyReleased(keyCode);
		} else {
			main_zone.keyReleased(keyCode);
		}
		repaint();
	}
	
	private void after_content_loaded(LineContent lineContent){
		if (flash!=null){
			flash.stopTimer();
			display.setCurrent(this);
			flash = null;
		}
		if (lineContent!=null) {
			book_manager.content = lineContent;
			book_manager.content.line_height = main_zone.line_height;
			book_manager.content.markPages(Configuration.SCREEN_HEIGHT - Configuration.HEADER_HEIGHT - Configuration.MENU_HEIGHT - 20);
			
			header_zone.setHeader(book_manager.content.header);
			main_zone.setContent(book_manager.content);
		} else {
			menu_zone.setMiddleText("读取资料错误。");
		}
		on_net_reading = Boolean.FALSE;
		menu_zone.repaint_bar();
		repaint();
	}
	private void gotoUrl(String url){
// #ifdef DBG		
		System.out.println("gotoURL:" + url);
// #endif
		if (url==null) return;
		
		LineContent c = book_manager.threadGetPage(Configuration.CONTENT_PATH, url, this);
		if ( c!=null) after_content_loaded(c);
		history_manager.addHistory(url);
	}
	// Callback by other thread
	public void thread_callback(Object data) {
		CallbackData cdata = (CallbackData)data;
		if (cdata.command==null) return;
// #ifdef DBG
		System.out.println("thread_callback:" + cdata.command);
// #endif		
		if (cdata.command.equals("LoadContent")) after_content_loaded((LineContent)cdata.data1);
		else if (cdata.command.equals("LoadingFromInternet")){
			on_net_reading = Boolean.TRUE;
			menu_zone.setMiddleText("正在读取网络...");
			repaint();
		} else if (cdata.command.equals("LoadError")){
			on_net_reading = Boolean.FALSE;
			menu_zone.setMiddleText("读取网络错误，请重试。");
			repaint();
		}
	}
	public void command_callback(int command, Object data) {
		switch(command){
		case BrowserCommand.ACTIVE_MENU:
			menu_zone.activeMenu();
			break;
		case BrowserCommand.GOTO_URL:
			gotoUrl((String)data);
			break;
		case BrowserCommand.GO_BACK:
			gotoUrl(history_manager.getBackUrl());
			break;
		case BrowserCommand.SET_MENU_TITLE:
			menu_zone.setMiddleText((String)data);
			repaint();
			break;
		}
		
	}

	
}
