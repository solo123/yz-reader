package com.yazo.ui;

import javax.microedition.lcdui.Graphics;

public class MainMenu {
	int width,height;
	public MainMenu(int width, int height){
		this.width = width;
		this.height = height;
	}
	public void paint(Graphics g){
		g.setColor(0x00FF00);
		g.fillRect(0, 0, width, height);
	}
}
