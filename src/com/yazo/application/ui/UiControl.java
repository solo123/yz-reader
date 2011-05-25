package com.yazo.application.ui;

import javax.microedition.lcdui.Graphics;

public abstract class UiControl {
	protected int width, height, posx, posy, align;

	public UiControl(){
		width = height = posx = posy = align = 0;
	}
	public void setSize(int width, int height){
		this.width = width;
		this.height = height;
	}
	public void setPos(int posx, int posy, int align){
		this.posx = posx;
		this.posy = posy;
		this.align = align;
	}
	public abstract void paint(Graphics g);
}
