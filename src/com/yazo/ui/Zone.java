package com.yazo.ui;

import javax.microedition.lcdui.*;

public abstract class Zone {
	protected int width, height, bgcolor, color;
	public Image image;
	protected Graphics g;
	protected Font font;
	public Zone(int width, int height){
		this.width = width;
		this.height = height;
		this.bgcolor = 0xFFFFFF;
		this.color = 0;
		this.image = Image.createImage(width, height);
		g = image.getGraphics();
		font = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE|Font.SIZE_MEDIUM);
	}
	public void setColor(int bgcolor, int color){
		this.bgcolor = bgcolor;
		this.color = color;
	}
}
