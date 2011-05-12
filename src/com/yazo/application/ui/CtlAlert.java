package com.yazo.application.ui;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Font;

import com.yazo.application.Configuration;
import com.yazo.model.BrowserCommand;
import com.yazo.model.ICommandManager;
import com.yazo.ui.UiControl;

public class CtlAlert extends UiControl {
	private ICommandManager command_manager = null;
	private Image img, img_shadow, img_bar;
	private Graphics g;
	private int color, bgcolor, bordercolor, bar_posy;
	private Font font = null;
	public int state = 0;
	private String confirmCommand = null;
	
	public CtlAlert(){
		super();
		img = img_bar = null;
		g = null;
		color = 0; //默认颜色
		bgcolor = 0xeeeeee;
		bordercolor = 0x999999;
	}
	
	public void setSize(int width, int height){
		super.setSize(width, height);
		img = Image.createImage(width,height);
		g = img.getGraphics();
		
		img_shadow = Image.createImage(width,height);
		Graphics gs = img_shadow.getGraphics();
		gs.setColor(0x666666);
		gs.fillRect(0, 0, width, height);
		gs = null;
	}
	public void setColor(int color, int bgcolor, int bordercolor){
		this.color = color;
		this.bgcolor = bgcolor;
		this.bordercolor = bordercolor;
	}
	public void setFont(Font font){
		this.font = font;
	}
	public void setCommandManager(ICommandManager manager){
		command_manager = manager;
	}
	public void setBar(int width, int height, int posy){
		bar_posy = posy;
		img_bar = Image.createImage(width,height);
		Graphics g = img_bar.getGraphics();
		g.setColor(0xeeeeee);
		g.fillRect(0, 0, width, height);
		g.setColor(0x999999);
		g.drawLine(0, 0, width, 0);
		g.setColor(0xffffff);
		g.drawLine(0, 1, width, 1);
		
		if (font==null) font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
		int y = (height-font.getHeight())/2 +2;
		g.setColor(0);
		g.setFont(font);
		g.drawString("确定", 4, y, Graphics.TOP|Graphics.LEFT);
		g.drawString("取消", width-4, y, Graphics.TOP|Graphics.RIGHT);
		g = null;
	}
	
	public void confirm(String message, String command){
		confirmCommand = command;
		g.setColor(bordercolor);
		g.drawRect(0, 0, width-1, height-1);
		g.setColor(0xffffff);
		g.drawRect(1, 1, width-3, height-3);
		g.setColor(bgcolor);
		g.fillRect(2, 2, width-4, height-4);
		
		g.setColor(color);
		if (font!=null) g.setFont(font);
		g.drawString(message, width/2, height/2, Graphics.HCENTER|Graphics.BASELINE);
		state = 1;		
	}
	
	public void paint(Graphics g) {
		if(state==1){
			g.drawImage(img_shadow, posx+3, posy+3, align);
			g.drawImage(img, posx, posy, align);
			if (img_bar!=null) g.drawImage(img_bar, 0, bar_posy, Graphics.BOTTOM|Graphics.LEFT);
		}
	}
	public void keyReleased(int keyCode) {
		// #ifdef DBG_KEYS
		System.out.println("quit key:" + keyCode);
		// #endif
		if (keyCode==-7) state=0;
		else if (keyCode==-6){
			state = 0;
			if(confirmCommand!=null && command_manager!=null) command_manager.command_callback(BrowserCommand.DO_COMMAND, confirmCommand);
		}
	}
	
}
