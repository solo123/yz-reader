package com.yazo.application;

import javax.microedition.lcdui.Font;

public class Configuration {
	public static String SERVICE_SERVER = "http://192.168.0.110:3000";
	public static String CONTENT_PATH = "http://192.168.0.110:3000/reader/pages/"; //内容服务器
	public static String CONTENT_HOME = "home";
	
	public static String APP_NAME = "手机阅读";
	
	public static int SCREEN_HEIGHT = 100;
	public static int SCREEN_WIDTH = 100;
	public static Font DEFAULT_FONT = null;
	public static int FONT_SIZE = 0; //默认中等大小字号
	public static int FONT_WIDTH = 20;
	public static int FONT_HEIGHT = 20;
	public static int LINE_HEIGHT = 25;
	public static int HEADER_HEIGHT = 20;
	public static int MENU_HEIGHT = 20;
	public static int BROWSER_HEIGHT = 100;
	
	public static void SetScreenSize(int width, int height){
		SCREEN_WIDTH = width;
		SCREEN_HEIGHT = height;
	}
	public static void SetFontSize(int font_size){
		FONT_SIZE = font_size;
		Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, font_size);
		DEFAULT_FONT = font;
		FONT_HEIGHT = font.getHeight();
		LINE_HEIGHT = FONT_HEIGHT + FONT_HEIGHT/4;
		FONT_WIDTH  = font.charWidth('国');
		HEADER_HEIGHT = MENU_HEIGHT = FONT_HEIGHT + 6;
		BROWSER_HEIGHT = SCREEN_HEIGHT - HEADER_HEIGHT - MENU_HEIGHT;
	}
	
}
