package com.yazo.application.ui;

import javax.microedition.lcdui.Graphics;

public class CtlNetIndicator extends UiControl {
	public boolean on_reading = false;
	
	public void paint(Graphics g) {
		if(on_reading){
			g.setColor(0xfff5c5);
			g.fillRect(posx-50, posy-18, 100, 16);
			g.setColor(0);
			g.drawString("读取网络...", posx, posy-2, align);
		}
	}

}
