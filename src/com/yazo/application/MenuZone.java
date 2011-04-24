package com.yazo.application;

import javax.microedition.lcdui.Graphics;

import com.yazo.ui.Zone;

public class MenuZone extends Zone {
	public MenuZone(int width, int height) {
		super(width, height);
	}
	
	
	public void setBrowseMenu(){
		g.setColor(bgcolor);
		g.fillRect(0, 0, width, height);
		
		g.setColor(color);
		g.drawString("菜单", 4, 2, Graphics.TOP|Graphics.LEFT);
		g.drawString("退出", width-4, 2, Graphics.TOP|Graphics.RIGHT);
	}

}
