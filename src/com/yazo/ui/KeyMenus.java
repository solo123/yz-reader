package com.yazo.ui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class KeyMenus {
	int image_count;
	Image[] images;
	int[] posx, posy;
	int width,height;
	int state;
	
	public KeyMenus(int width, int height){
		this.width = width;
		this.height = height;
		image_count = 0;
		images = new Image[20];
		posx = new int[20];
		posy = new int[20];
		state = 0;  // init state
	}
	
	public void keyAction(int key_code){
		if (key_code == -6){ // left menu
			Image img = Image.createImage(width,height);
			Graphics g = img.getGraphics();
			
			g.setColor(0xFF0000);
			g.fillRect(0, 0, width, height);
			g = null;
			
			image_count = 1;
			images[0] = img;
			posx[0] = 0;
			posy[0] = 0;
		}
	}
	
}
