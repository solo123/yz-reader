package com.yazo.application;

import com.yazo.application.biz.Config;
import com.yazo.application.biz.ContentManager;
import com.yazo.application.thread.ThreadManager;
import com.yazo.application.ui.*;
import com.yazo.model.BrowserCommand;
import com.yazo.model.ConfigKeys;
import com.yazo.model.ICommandListener;
import javax.microedition.lcdui.*;

public class Browser extends Canvas implements ICommandListener {
	private UiContainer container = new UiContainer();
	private CtlHeader ctl_header;
	private CtlExplorer ctl_explorer;
	private CtlMenu ctl_menu;
	private CtlAlert ctl_alert;
	
	public ContentManager contents;
	private ThreadManager threadManager;
	private MainMIDlet midlet;
	private Display display;
	private SplashCanvas splash;
	private Boolean on_net_reading;
	private Config config = Config.getInstance();
	
	public Browser(MainMIDlet midlet, Display display){
		setFullScreenMode(true);
		this.display = display;
		this.midlet = midlet;
		initConfig();
		
		splash = new SplashCanvas(midlet);
		display.setCurrent(splash);
		
		new Thread(){
			public void run(){
				init_browser();
			}
		}.start();


	}
	private void init_browser(){
		on_net_reading = Boolean.TRUE;
		
		contents = new ContentManager(this);
		threadManager = new ThreadManager();
		
		ctl_header = new CtlHeader();
		ctl_header.setSize(config.getInt(ConfigKeys.SCREEN_WIDTH), config.getInt(ConfigKeys.HEADER_HEIGHT));
		ctl_header.setPos(0, 0, Graphics.TOP|Graphics.LEFT);
		ctl_header.setTitle(config.getString(ConfigKeys.APP_NAME));
		container.addControl(ctl_header);
		
		ctl_explorer = new CtlExplorer();
		ctl_explorer.setSize(config.getInt(ConfigKeys.SCREEN_WIDTH), config.getInt(ConfigKeys.BROWSER_HEIGHT));
		ctl_explorer.setPos(0, config.getInt(ConfigKeys.HEADER_HEIGHT), Graphics.TOP|Graphics.LEFT);
		ctl_explorer.setCommandManager(this);
		container.addControl(ctl_explorer);

		ctl_menu = new CtlMenu();
		ctl_menu.setSize(config.getInt(ConfigKeys.SCREEN_WIDTH), config.getInt(ConfigKeys.MENU_HEIGHT));
		ctl_menu.setPos(0, config.getInt(ConfigKeys.SCREEN_HEIGHT), Graphics.BOTTOM|Graphics.LEFT);
		ctl_menu.setMenuText("返回");
		ctl_menu.setCommandManager(this);
		container.addControl(ctl_menu);
		
		ctl_alert = new CtlAlert();
		ctl_alert.setSize(config.getInt(ConfigKeys.SCREEN_WIDTH)-30, 60);
		ctl_alert.setPos(15, config.getInt(ConfigKeys.SCREEN_HEIGHT)-config.getInt(ConfigKeys.MENU_HEIGHT)-10, Graphics.BOTTOM|Graphics.LEFT);
		ctl_alert.setFont((Font)config.getObject(ConfigKeys.DEFAULT_FONT));
		ctl_alert.setBar(config.getInt(ConfigKeys.SCREEN_WIDTH), config.getInt(ConfigKeys.MENU_HEIGHT), config.getInt(ConfigKeys.SCREEN_HEIGHT)-1);
		ctl_alert.setCommandManager(this);
		container.addControl(ctl_alert);
		
		gotoUrl(config.getString(ConfigKeys.CONTENT_HOME));
	}
	private void initConfig(){
		int screen_height = getHeight();
		config.add(ConfigKeys.SCREEN_WIDTH, getWidth());
		config.add(ConfigKeys.SCREEN_HEIGHT, screen_height);
		Font font = Font.getDefaultFont();
		int font_height = font.getHeight();
		int header_height = font_height + 6;
		int menu_height = font_height + 6;
		config.add(ConfigKeys.DEFAULT_FONT, font);
		config.add(ConfigKeys.FONT_WIDTH, font.charWidth('国'));
		config.add(ConfigKeys.FONT_HEIGHT, font_height);
		config.add(ConfigKeys.LINE_HEIGHT, font_height + font_height/4);
		config.add(ConfigKeys.HEADER_HEIGHT, header_height);
		config.add(ConfigKeys.MENU_HEIGHT, menu_height);
		config.add(ConfigKeys.BROWSER_HEIGHT, screen_height - header_height - menu_height);
		
		config.add(ConfigKeys.APP_NAME, "app name"); //TODO: 从打包配置中读取app name
		config.add(ConfigKeys.CONTENT_SERVER, "http://192.168.0.110:3000");
		config.add(ConfigKeys.CONTENT_PATH, "http://192.168.0.110:3000/reader/pages/");
		config.add(ConfigKeys.CONTENT_HOME, "home");
		
		// load config from RMS
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
		if (splash!=null){
			if (data==null){
				splash.retryNetwork();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				gotoUrl(config.getString(ConfigKeys.CONTENT_HOME));
				return;
			} else {
				splash.stopTimer();
				display.setCurrent(this);
				splash = null;
				threadManager.loginThread(this);  // login after splash closed.
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
		threadManager.getPageContentThread(config.getString(ConfigKeys.CONTENT_PATH), url, this);
		// after loaded callback command_callback
	}
	
	public void execute_command(int command, Object data) {
		// #ifdef DBG
		System.out.println("Browser.execute_command:" + command);
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
			// TODO: run CMCC simulator if configed to run
			if (config.getInt(ConfigKeys.APP_RUN_CMCC)==1){
				
			}
			break;
		case BrowserCommand.LOGIN_ERROR:
			// TODO: retry 3 time?
			break;
		case BrowserCommand.NO_NETWORK:
			if (splash!=null){
				splash.setError("无网络连接，请退出后重新进入。");
			}
			break;

		}
	}
}
