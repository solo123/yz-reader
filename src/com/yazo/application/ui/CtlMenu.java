package com.yazo.application.ui;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Font;
import com.yazo.ui.UiControl;

public class CtlMenu extends UiControl {
	private Image img_bar;
	private Graphics g_bar;
	private int color, bgcolor,  icolor;
	private Font font = null;
	private String menu_text1, menu_text2, middle_text;
	private int state = 0;
	
	public CtlMenu(){
		super();
		img_bar = null;
		g_bar = null;
		color = 0; //默认颜色
		bgcolor = 0xc2c2c2;
		icolor = 0xffff00;
		menu_text1 = "返回";
		menu_text2 = "菜单";
	}
	public void setMenuText(String text){
		menu_text1 = text;
		paint_bar();
	}
	public void setMiddleText(String text){
		middle_text = text;
		paint_bar();
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
	private void paint_bar(){
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
		state = 0;
	}
	
	public void paint(Graphics g) {
		g.drawImage(img_bar, posx, posy, align);
	}
	
}
