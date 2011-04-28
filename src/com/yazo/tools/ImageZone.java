package com.yazo.tools;

import javax.microedition.lcdui.*;

public abstract class ImageZone {
	protected int screen_width, screen_height, image_width, image_height, bgcolor, color, posx,posy;
	protected Image[] images;
	protected Graphics[] graphics;
	protected Font default_font;
	
	public ImageZone(){
		screen_width = screen_height = image_width = image_height = bgcolor = color = posx = posy = 0;
		images = null;
		graphics = null;
		default_font = null;
	}
	public void setScreenSize(int width, int height){
		screen_width = width;
		screen_height = height;
	}
	public void setPos(int posx, int posy){
		this.posx = posx;
		this.posy = posy;
	}
	public void setImageSize(int width, int height){
		image_width = width;
		image_height = height;
	}
	public void setColor(int bgcolor, int color){
		this.bgcolor = bgcolor;
		this.color = color;
	}
	public void setFont(Font font){
		default_font = font;
	}
	public abstract void paint(Graphics g);
}
