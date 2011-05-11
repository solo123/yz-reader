package com.yazo.application.ui;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Font;

import com.yazo.contents.BrowserContent;
import com.yazo.contents.LinkContent;
import com.yazo.model.BrowserCommand;
import com.yazo.model.ICommandManager;
import com.yazo.ui.UiControl;

public class CtlMenu extends UiControl {
	private Image img_bar, img_menu, img_shadow;
	private Graphics g_bar, g_menu;
	private int color, bgcolor,  icolor;
	private Font font = null;
	private String menu_text1, menu_text2, middle_text, menu_cmd1;
	public int state = 0;
	private int menu_width, menu_height, line_height, cursor, max_items;
	private Vector menu_contents;
	private ICommandManager command_manager=null;
	
	public CtlMenu(){
		super();
		img_bar = null;
		g_bar = null;
		color = 0; //默认颜色
		bgcolor = 0xc2c2c2;
		icolor = 0xffff00;
		menu_text1 = "返回首页";
		menu_cmd1 = "home";
		menu_text2 = "菜单";
		cursor = max_items = 0;
	}
	public void setMenuText(String text){
		menu_text1 = text;
		paint_bar();
	}
	public void setMiddleText(String text){
		middle_text = text;
		paint_bar();
	}
	public void setCommandManager(ICommandManager manager){
		command_manager = manager;
	}
	public void setRightCommand(String name, String url){
		menu_text1 = name;
		menu_cmd1 = url;
	}
	public void setSize(int width, int height){
		super.setSize(width, height);
		super.setPos(0, 0, Graphics.TOP|Graphics.LEFT);
		img_bar = Image.createImage(width,height);
		g_bar = img_bar.getGraphics();
	}
	public void setColor(int color, int bgcolor, int icolor){
		this.color = color;
		this.bgcolor = bgcolor;
		this.icolor = icolor;
	}
	public void setFont(Font font){
		this.font = font;
	}
	public void paint_bar(){
		if (font==null) font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
		int y = (height-font.getHeight())/2 +2;
		g_bar.setColor(bgcolor);
		g_bar.fillRect(0, 0, width, height);
		g_bar.setColor(0x999999);
		g_bar.drawLine(0, 0, width, 0);
		g_bar.setColor(0xffffff);
		g_bar.drawLine(0, 1, width, 1);
		
		g_bar.setColor(color);
		g_bar.drawString(menu_text2, 4, y, Graphics.TOP|Graphics.LEFT);
		g_bar.drawString(menu_text1, width-4, y, Graphics.TOP|Graphics.RIGHT);
		if (middle_text!=null)
			g_bar.drawString(middle_text, width/2, y, Graphics.HCENTER|Graphics.TOP);
	}
	public void repaint_menu(){
		if (g_menu==null) return;
		int leftGrid = 20;
		g_menu.setColor(0x999999);
		g_menu.drawRect(0, 0, menu_width-1, menu_height-1);
		g_menu.setColor(0xffffff);
		g_menu.drawRect(1, 1, menu_width-3, menu_height-3);
		g_menu.setColor(0xeeeeee);
		g_menu.fillRect(2, 2, menu_width-4, menu_height-4);

		// current line
		g_menu.setColor(0xa8d8eb);
		g_menu.drawRoundRect(5, 2+line_height*cursor, menu_width-10, line_height, 5, 5);
		g_menu.setColor(0xe0edf3);
		g_menu.fillRect(6, 3+line_height*cursor, menu_width-10-2, 18);
		g_menu.setColor(0x333333);
		g_menu.fillRect(11, 2+8+line_height*cursor, 6, 6);
		
		// left grid line
		g_menu.setColor(0xdddddd);
		g_menu.drawLine(leftGrid, 3, leftGrid, menu_height-3);
		g_menu.setColor(0xffffff);
		g_menu.drawLine(leftGrid+1, 3, leftGrid+1, menu_height-3);
		
		g_menu.setColor(0);
		if(menu_contents!=null){
			int y = 5;
			Enumeration contents = menu_contents.elements();
		    while(contents.hasMoreElements()){
				LinkContent c = (LinkContent)contents.nextElement();
				g_menu.drawString(c.content, leftGrid+5, y, Graphics.TOP|Graphics.LEFT);
				y += line_height;
		    }
		}
	}
	public void setSubMenu(Vector contents){
		menu_contents = contents;
		max_items = contents.size();
		if (font==null) font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
		line_height = font.getHeight();
		line_height = line_height + line_height/4;
		menu_width = font.charWidth('国') * 5 + 50;
		menu_height = line_height * contents.size() + 5;
		
		img_menu = Image.createImage(menu_width, menu_height);
		img_shadow = Image.createImage(menu_width, menu_height);
		
		Graphics g = img_shadow.getGraphics();
		g.setColor(0x666666);
		g.fillRect(0, 0, menu_width, menu_height);
		
		g_menu = img_menu.getGraphics();
		repaint_menu();
	}
	
	public void paint(Graphics g) {
		g.drawImage(img_bar, posx, posy, align);
		if(state>0 && img_menu!=null){
			g.drawImage(img_shadow, posx+7, posy-height-1, Graphics.BOTTOM|Graphics.LEFT);
			g.drawImage(img_menu, posx+4, posy-height-4, Graphics.BOTTOM|Graphics.LEFT);
		}
	}
	public void activeMenu(){
		if (state==0){
			if(menu_contents!=null) state = 1;
		}
		else
			state = 0;
	}
	public void keyReleased(int keyCode) {
		// #ifdef DBG
		System.out.println("menu key:" + keyCode);
		// #endif
		switch(keyCode){
		case -1:  // cursor up
			if (cursor>0){ 
				cursor--;
				repaint_menu();
			}
			break;
		case -2:  // cursor down
			if (cursor<max_items-1){
				cursor++;
				repaint_menu();
			}
			break;
		case -4: // cursor right
			break;
		case -3: // cursor left = active(deactive) menu
		case -6:
			activeMenu();
			break;
		case -7:
			if (state>0) activeMenu();
			else{
				if(command_manager!=null) command_manager.command_callback(BrowserCommand.DO_COMMAND, menu_cmd1);
			}
			break;
		case -5: // selected
			state = 0;
			LinkContent lc = (LinkContent)menu_contents.elementAt(cursor);
			System.out.println("menu cmd:" + lc.content + ", cmd:" + lc.url);
			if(command_manager!=null) command_manager.command_callback(BrowserCommand.DO_COMMAND, lc.url);
			break;
		}
	}
}
