package com.yazo.application;

import javax.microedition.lcdui.*;

import com.yazo.tools.ImageZone;
import com.yazo.ui.*;

public class HeaderZone extends ImageZone {
	public HeaderZone() {
		super();
	}
	public void setImageSize(int width, int height){
		super.setImageSize(width, height);
		images = new Image[1];
		images[0] = Image.createImage(width, height);
		graphics = new Graphics[1];
		graphics[0] = images[0].getGraphics();
	}
	public void setHeader(String header){
		Font font;
		font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
		
		Graphics g = graphics[0];
		g.setColor(bgcolor);
		g.fillRect(0, 0, image_width, image_height-1);
		
		g.setColor(0xffff00);
		g.fillRect(10, 5, 4, 10);
		g.setColor(color);
		if(header!=null){
			g.setFont(font);
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
