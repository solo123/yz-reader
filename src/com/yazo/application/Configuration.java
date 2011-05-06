package com.yazo.application;

import javax.microedition.lcdui.Font;

public class Configuration {
	public static String SERVICE_SERVER = "http://bk-b.info/";
	public static String CONTENT_PATH = "http://bk-b.info/reader/pages/"; //内容服务器
	public static String CONTENT_HOME = "home";
	
	public static int SCREEN_HEIGHT = 100;
	public static int SCREEN_WIDTH = 100;
	public static int FONT_SIZE = 0; //默认中等大小字号
	public static int FONT_WIDTH = 20;
	public static int FONT_HEIGHT = 20;
	public static int HEADER_HEIGHT = 20;
	public static int MENU_HEIGHT = 20;
	
	public static void SetFontSize(int font_size){
		FONT_SIZE = font_size;
		Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, font_size);
		FONT_HEIGHT = font.getHeight();
		FONT_WIDTH  = font.charWidth('国');
		HEADER_HEIGHT = MENU_HEIGHT = FONT_HEIGHT + 6;
	}
	
}
