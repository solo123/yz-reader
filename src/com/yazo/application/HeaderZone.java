package com.yazo.application;

import javax.microedition.lcdui.*;

import com.yazo.ui.*;

public class HeaderZone extends Zone {
	public HeaderZone(int width, int height) {
		super(width, height);
	}

	public void setHeader(String header){
		g.setColor(bgcolor);
		g.fillRect(0, 0, width, height);
		
		g.setColor(0xa17431);
		g.fillRect(10, 3, 10, 12);
		g.setColor(color);
		g.drawString(header, 24, 2, Graphics.TOP|Graphics.LEFT);
	}
}
