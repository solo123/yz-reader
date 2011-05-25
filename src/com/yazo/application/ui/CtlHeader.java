package com.yazo.application.ui;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Font;

public class CtlHeader extends UiControl {
	private Image img;
	private Graphics g;
	private int color, bgcolor, icolor;
	private Font font = null;
	
	public CtlHeader(){
		super();
		img = null;
		g = null;
		color = 0xffffff; //默认颜色
		bgcolor = 0x7c90b3;
		icolor = 0xffff00;
	}
	
	public void setSize(int width, int height){
		super.setSize(width, height);
		super.setPos(0, 0, Graphics.TOP|Graphics.LEFT);
		img = Image.createImage(width,height);
		g = img.getGraphics();
	}
	public void setColor(int color, int bgcolor, int icolor){
		this.color = color;
		this.bgcolor = bgcolor;
		this.icolor = icolor;
	}
	public void setFont(Font font){
		this.font = font;
	}
	
	public void setTitle(String title){
		if (img==null) return;
		if (font==null) font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
		g.setColor(bgcolor);
		g.fillRect(0, 0, width, height-1);
		
		int w = 4;
		int h = font.getHeight();
		int y = (height - h)/2;
		g.setColor(icolor);
		g.fillRect(10, y+3, w, h-6);
		
		g.setColor(color);
		if (font!=null) g.setFont(font);
		if (title!=null) g.drawString(title, 24, y, Graphics.TOP|Graphics.LEFT);
		
		//shadow line
		g.setColor(0xeeeeee);
		g.drawLine(0, height-1, width-1, height-1);
	}
	
	public void paint(Graphics g) {
		g.drawImage(img, posx, posy, align);
	}
	
}
