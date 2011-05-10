package com.yazo.application;

import java.util.Vector;

import com.yazo.application.ui.*;
import com.yazo.contents.*;
import com.yazo.model.BrowserCommand;
import com.yazo.model.ICommandManager;
import com.yazo.tools.ImageZone;
import com.yazo.ui.UiContainer;

import javax.microedition.lcdui.*;

public class MainBrowser extends Canvas implements ICommandManager {
	private UiContainer container = new UiContainer();
	private CtlHeader ctl_header;
	private CtlExplorer ctl_explorer;
	private CtlMenu ctl_menu;
	
	public ContentManager contents;
	private MainMIDlet midlet;
	private Display display;
	private SplashCanvas flash;
	private ImageZone[] zones;
	private HeaderZone header_zone;
	private MainZone main_zone;
	private MenuZone menu_zone;
	private PopupZone popup_zone;
	private Boolean on_net_reading;
	private BookMenu book_menu;
	private HistoryManager history_manager;
	
	public MainBrowser(MainMIDlet midlet, Display display){
		this.display = display;
		this.midlet = midlet;
		
		flash = new SplashCanvas(midlet);
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
		contents = new ContentManager(this);
		
		ctl_header = new CtlHeader();
		ctl_header.setSize(Configuration.SCREEN_WIDTH, Configuration.HEADER_HEIGHT);
		ctl_header.setPos(0, 0, Graphics.TOP|Graphics.LEFT);
		ctl_header.setTitle(Configuration.APP_NAME);
		container.addControl(ctl_header);
		
		ctl_explorer = new CtlExplorer();
		ctl_explorer.setSize(Configuration.SCREEN_WIDTH, Configuration.BROWSER_HEIGHT);
		ctl_explorer.setPos(0, Configuration.HEADER_HEIGHT, Graphics.TOP|Graphics.LEFT);
		ctl_explorer.setCommandManager(this);
		container.addControl(ctl_explorer);

		ctl_menu = new CtlMenu();
		ctl_menu.setSize(Configuration.SCREEN_WIDTH, Configuration.MENU_HEIGHT);
		ctl_menu.setPos(0, Configuration.SCREEN_HEIGHT, Graphics.BOTTOM|Graphics.LEFT);
		ctl_menu.setMenuText("返回");
		container.addControl(ctl_menu);
		
		gotoUrl(Configuration.CONTENT_HOME);
	}
	
	protected void paint(Graphics g) {
		container.paint(g);
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
		int key = ctl_explorer.keyReleased(keyCode);
		switch(key){
		case -6:
			//menu_zone.activeMenu();
			break;
		case -7:
			//gotoUrl(history_manager.getBackUrl());
			break;
		}
		ctl_menu.setMiddleText("" + (ctl_explorer.current_page+1) + " / " + ctl_explorer.total_pages);
		repaint();
	}
	
	private void after_content_loaded(LineContent lineContent){
		if (flash!=null){
			if (lineContent==null){
				flash.retryNetwork();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				gotoUrl(Configuration.CONTENT_HOME);
				return;
			} else {
				flash.stopTimer();
				display.setCurrent(this);
				flash = null;
			}
		}
		if (lineContent!=null) {
			//contents.content = lineContent;
			ctl_header.setTitle(lineContent.header);
			ctl_explorer.setContent(lineContent);
			ctl_explorer.setCurrentPage(0);
			ctl_menu.setMiddleText("" + (ctl_explorer.current_page+1) + " / " + ctl_explorer.total_pages);

		} else {
			ctl_menu.setMiddleText("读取资料错误。");
		}
		on_net_reading = Boolean.FALSE;
		repaint();
	}
	
	private void gotoUrl(String url){
		// #ifdef DBG
		System.out.println("gotoURL:" + url);
		// #endif
		contents.loadLineContentFromUrl(Configuration.CONTENT_PATH, url);
		// after loaded callback command_callback
	}
	
	public void command_callback(int command, Object data) {
		// #ifdef DBG
		System.out.println("command_callback:" + command);
		// #endif
		
		switch(command){
		case BrowserCommand.LOADING_FROM_INTERNET:
			on_net_reading = Boolean.TRUE;
			ctl_menu.setMiddleText("正在读取网络...");
			break;
		case BrowserCommand.AFTER_LINECONTENT_LOADED:
			after_content_loaded((LineContent)data);
			break;
		case BrowserCommand.LOAD_ERROR:
			after_content_loaded(null);			
			break;
		case BrowserCommand.GOTO_URL:
			gotoUrl((String)data);
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
			//searchui.inputSearchText(this, display);
			break;
		case BrowserCommand.SEARCH_TEXT:
			System.out.println("Search text:" + data);
			gotoUrl("search?s=" + data);
			break;

		}
	}
}
