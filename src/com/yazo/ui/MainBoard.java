package com.yazo.ui;

import javax.microedition.lcdui.Graphics;

public class MainBoard {
	int menu_height, width,height;
	public MainBoard(int menu_height, int width, int height){
		this.menu_height = menu_height;
		this.width = width;
		this.height = height;
	}
	public void paint(Graphics g){
		g.setColor(0x0000FF);
		g.fillRect(0, menu_height, width, height-menu_height);
	}
}
