package com.yazo.application;

import java.util.Vector;

import com.yazo.contents.*;
import com.yazo.model.BrowserCommand;
import com.yazo.model.ICommandManager;
import com.yazo.tools.ImageZone;
import javax.microedition.lcdui.*;

public class Browser extends Canvas implements ICommandManager {
	private Vector controls = new Vector(10);
	
	public ContentManager book_manager;
	private MainMIDlet midlet;
	private Display display;
	private FlashCanvas flash;
	private ImageZone[] zones;
	private HeaderZone header_zone;
	private MainZone main_zone;
	private MenuZone menu_zone;
	private PopupZone popup_zone;
	private Boolean on_net_reading;
	private BookMenu book_menu;
	private HistoryManager history_manager;
	
	public Browser(MainMIDlet midlet, Display display){
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
		setFullScreenMode(true);
		on_net_reading = Boolean.TRUE;
		Configuration.SetScreenSize(getWidth(), getHeight());
		Configuration.SetFontSize(Font.SIZE_MEDIUM);
		
		book_menu = new BookMenu();
		history_manager = new HistoryManager(book_menu);
		book_manager = new ContentManager(this);
		zones = new ImageZone[4];
		zones[0] = header_zone = new HeaderZone();
		zones[1] = main_zone = new MainZone(this);
		zones[2] = menu_zone = new MenuZone(this, book_menu);
		zones[3] = popup_zone = new PopupZone(this);
		
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
		if (popup_zone.state>0){
			popup_zone.keyReleased(keyCode);
		} else if (menu_zone.state>0){
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
		if (url==null)
			System.out.println("Quiting");
		else
			System.out.println("gotoURL:" + url);
// #endif
		if (url==null) {
			popup_zone.Alert("确认退出"+Configuration.APP_NAME);
		} else {
			book_manager.loadLineContentFromUrl(Configuration.CONTENT_PATH, url);
			//history_manager.addHistory(url);
			// will callback command_callback
		}
	}
	
	public void command_callback(int command, Object data) {
// #ifdef DBG
		System.out.println("command_callback:" + command);
// #endif
		switch(command){
		case BrowserCommand.LOADING_FROM_INTERNET:
			on_net_reading = Boolean.TRUE;
			menu_zone.setMiddleText("正在读取网络...");
			repaint();
			break;
		case BrowserCommand.AFTER_LINECONTENT_LOADED:
			after_content_loaded((LineContent)data);
			break;
		case BrowserCommand.LOAD_ERROR:
			after_content_loaded(null);			
			break;
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
		case BrowserCommand.QUIT_APPLICATION:
			midlet.quit();
			break;
		case BrowserCommand.SEARCH:
			SearchUi searchui = new SearchUi();
			searchui.inputSearchText(this, display);
			break;
		case BrowserCommand.SEARCH_TEXT:
			System.out.println("Search text:" + data);
			gotoUrl("search?s=" + data);
			break;

		}
	}
}
