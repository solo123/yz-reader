package com.yazo.application;

import javax.microedition.lcdui.*;
import com.yazo.tools.ImageZone;

public class HeaderZone extends ImageZone {
	public HeaderZone() {
		super();
		setScreenSize(Configuration.SCREEN_WIDTH, Configuration.SCREEN_HEIGHT);
		setImageSize(Configuration.SCREEN_WIDTH, Configuration.HEADER_HEIGHT);
		setFontSize(Configuration.FONT_SIZE);
		setColor(0x7c90b3, 0xFFFFFF);
	}
	public void setImageSize(int width, int height){
		super.setImageSize(width, height);
		images = new Image[1];
		images[0] = Image.createImage(width, height);
		graphics = new Graphics[1];
		graphics[0] = images[0].getGraphics();
	}
	public void setHeader(String header){
		Graphics g = graphics[0];
		g.setColor(bgcolor);
		g.fillRect(0, 0, image_width, image_height-1);
		
		g.setColor(0xffff00);
		g.fillRect(10, 5, 4, 10);
		g.setColor(color);
		if(header!=null){
			g.setFont(default_font);
			g.drawString(header, 24, 2, Graphics.TOP|Graphics.LEFT);
		}
		
		//shadow line
		g.setColor(0xeeeeee);
		g.drawLine(0, image_height-1, image_width-1, image_height-1);
	}
	public void paint(Graphics g) {
		if (images!=null && images.length>0 && images[0]!=null)
			g.drawImage(images[0], 0, 0, Graphics.TOP|Graphics.LEFT);
	}
}
