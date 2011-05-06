package com.yazo.application;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.yazo.model.BrowserCommand;
import com.yazo.model.ICommandManager;
import com.yazo.tools.ImageZone;

public class PopupZone extends ImageZone {
	ICommandManager command_manager;
	public int state = 0;
	public PopupZone(ICommandManager manager){
		command_manager = manager;
		image_width = 150;
		image_height = 60;
		images = new Image[3];
		images[0] = Image.createImage(image_width, image_height);
		graphics = new Graphics[1];
		graphics[0] = images[0].getGraphics();
		images[1] = Image.createImage(image_width, image_height);
		Graphics g = images[1].getGraphics();
		g.setColor(0x666666);
		g.fillRect(0, 0, image_width, image_height);
		g = null;
		images[2] = Image.createImage(Configuration.SCREEN_WIDTH, Configuration.MENU_HEIGHT);
		paint_bar(images[2]);
	}
	public void paint_bar(Image img){
		int w = img.getWidth();
		int h = img.getHeight();
		Graphics g = img.getGraphics();
		g.setColor(0xeeeeee);
		g.fillRect(0, 0, w, h);
		g.setColor(0x999999);
		g.drawLine(0, 0, w, 0);
		g.setColor(0xffffff);
		g.drawLine(0, 1, w, 1);
		
		g.setColor(0);
		g.drawString("退出程序", 4, 2, Graphics.TOP|Graphics.LEFT);
		g.drawString("返回", w-4, 2, Graphics.TOP|Graphics.RIGHT);
		g = null;
	}
	public void Alert(String message){
		Graphics g = graphics[0];
		g.setColor(0x999999);
		g.drawRect(0, 0, image_width-1, image_height-1);
		g.setColor(0xffffff);
		g.drawRect(1, 1, image_width-3, image_height-3);
		g.setColor(0xeeeeee);
		g.fillRect(2, 2, image_width-4, image_height-4);
		
		g.setColor(0);
		g.drawString(message, image_width/2, image_height/2, Graphics.HCENTER|Graphics.BASELINE);
		state = 1;
	}
	public void paint(Graphics g) {
		if (state>0){
			int posx = (Configuration.SCREEN_WIDTH - image_width)/2;
			int posy = Configuration.SCREEN_HEIGHT - Configuration.MENU_HEIGHT - 20;
			g.drawImage(images[1],posx+3, posy+3, Graphics.BOTTOM|Graphics.LEFT);
			g.drawImage(images[0],posx, posy , Graphics.BOTTOM|Graphics.LEFT);
			g.drawImage(images[2], 0, Configuration.SCREEN_HEIGHT, Graphics.BOTTOM|Graphics.LEFT);
		}
		
	}
	public void keyReleased(int keyCode) {
		if (keyCode == -6){
			if (command_manager!=null) command_manager.command_callback(BrowserCommand.CONFIRM, null);
		} else if (keyCode == -7){
			state = 0;
		}
	}

}
