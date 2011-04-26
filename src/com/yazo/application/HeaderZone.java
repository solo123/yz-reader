package com.yazo.application;

import javax.microedition.lcdui.*;
import com.yazo.ui.*;

public class HeaderZone extends Zone {
	public HeaderZone(int width, int height) {
		super(width, height);
	}

	public void setHeader(String header){
		Font font;
		font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
		
		g.setColor(bgcolor);
		g.fillRect(0, 0, width, height-1);
		
		g.setColor(0xffff00);
		g.fillRect(10, 5, 4, 10);
		g.setColor(color);
		if(header!=null){
			g.setFont(font);
			g.drawString(header, 24, 2, Graphics.TOP|Graphics.LEFT);
		}
		
		//shadow line
		g.setColor(0xeeeeee);
		g.drawLine(0, height-1, width-1, height-1);
	}
}
