package com.yazo.application;

import com.yazo.application.thread.ThreadManager;
import com.yazo.application.ui.*;
import com.yazo.contents.*;
import com.yazo.mobile.MobileSysData;
import com.yazo.model.BrowserCommand;
import com.yazo.model.ICommandManager;
import com.yazo.network.HttpConnect;
import com.yazo.ui.UiContainer;
import javax.microedition.lcdui.*;

public class Browser extends Canvas implements ICommandManager {
	private UiContainer container = new UiContainer();
	private CtlHeader ctl_header;
	private CtlExplorer ctl_explorer;
	private CtlMenu ctl_menu;
	private CtlAlert ctl_alert;
	
	public ContentManager contents;
	private ThreadManager threadManager;
	private MainMIDlet midlet;
	private Display display;
	private SplashCanvas flash;
	private Boolean on_net_reading;
	private BookMenu book_menu;
	
	public Browser(MainMIDlet midlet, Display display){
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
		contents = new ContentManager(this);
		threadManager = new ThreadManager();
		
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
		ctl_menu.setPos(0, Configuration.SCREEN_HEIGHT-1, Graphics.BOTTOM|Graphics.LEFT);
		ctl_menu.setMenuText("返回");
		ctl_menu.setCommandManager(this);
		container.addControl(ctl_menu);
		
		ctl_alert = new CtlAlert();
		ctl_alert.setSize(Configuration.SCREEN_WIDTH-30, 60);
		ctl_alert.setPos(15, Configuration.SCREEN_HEIGHT-Configuration.MENU_HEIGHT-10, Graphics.BOTTOM|Graphics.LEFT);
		ctl_alert.setFont(Configuration.DEFAULT_FONT);
		ctl_alert.setBar(Configuration.SCREEN_WIDTH, Configuration.MENU_HEIGHT, Configuration.SCREEN_HEIGHT-1);
		ctl_alert.setCommandManager(this);
		container.addControl(ctl_alert);
		
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
		if (ctl_alert.state>0) {
			ctl_alert.keyReleased(keyCode);
		} else if (ctl_menu.state>0){
			ctl_menu.keyReleased(keyCode);
		} else {
			int key = ctl_explorer.keyReleased(keyCode);
			if (key!=0) ctl_menu.keyReleased(key);
		}
		repaint();
	}
	
	private void after_content_loaded(Object data){
		if (flash!=null){
			if (data==null){
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
				threadManager.loginThread(this);
			}
		}
		if (data!=null) {
			contents.setCurrentContent(data);

			ctl_header.setTitle(contents.content.header);
			ctl_explorer.setContent(contents.content, 0);
			ctl_explorer.setCurrentPage(0);
			ctl_menu.setMiddleText("" + (ctl_explorer.current_page+1) + " / " + ctl_explorer.total_pages);
			ctl_menu.setSubMenu(contents.content.menus);
			if (contents.content.rightKeyMenu!=null){
				String t = contents.content.rightKeyMenu.content;
				String u = contents.content.rightKeyMenu.url;
				ctl_menu.setRightCommand(t,u);
				ctl_menu.paint_bar();
			}

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
		threadManager.getPageContentThread(Configuration.CONTENT_PATH, url, this);
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
			after_content_loaded(data);
			break;
		case BrowserCommand.LOAD_ERROR:
			after_content_loaded(null);			
			break;
		case BrowserCommand.DO_COMMAND:
			String cmd = (String)data;
			// #ifdef DBG
			System.out.println("DO_COMMAND:" + cmd);
			// #endif
			if (cmd.startsWith("CMD_")){ 
				if(cmd.equals("CMD_SEARCH")) {
					SearchUi searchui = new SearchUi();
					searchui.inputSearchText(this, display);
				} else if(cmd.equals("CMD_QUIT")){
					ctl_alert.confirm("确认退出吗？", "CMD_QUIT_NOW");
				} else if(cmd.equals("CMD_QUIT_NOW")){
					midlet.quit();
				} else if(cmd.startsWith("CMD_ALERT")){
					ctl_alert.confirm(cmd.substring(10), "CMD_NOP");
				}
			} else {
				gotoUrl(cmd);
			}
			break;
		case BrowserCommand.GOTO_URL:
			gotoUrl((String)data);
			break;
		case BrowserCommand.SET_MENU_TITLE:
			ctl_menu.setMiddleText((String)data);
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
			display.setCurrent(this);
			setFullScreenMode(true);
			gotoUrl("search?s=" + data);
			break;
		case BrowserCommand.REFRESH_STATUS:
			ctl_menu.setMiddleText("" + (ctl_explorer.current_page+1) + " / " + ctl_explorer.total_pages);
			break;
		case BrowserCommand.AFTER_LOGIN:
			break;
		case BrowserCommand.LOGIN_ERROR:
			break;

		}
	}
}
