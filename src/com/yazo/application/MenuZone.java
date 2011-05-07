package com.yazo.application;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.yazo.contents.BookMenu;
import com.yazo.model.ICommandManager;
import com.yazo.tools.ImageZone;

public class MenuZone extends ImageZone {
	public int state;
	private int  cursor, max_items, line_height;
	private BookMenu book_menu;
	private String middle_text, right_menu_text;
	private ICommandManager command_manager;
	
	private int bar_width, bar_height, menu_width, menu_height;
	
	public MenuZone(ICommandManager manager) {
		super();
		state = 0;
		cursor = 0;
		book_menu = new BookMenu();
		line_height = Configuration.FONT_HEIGHT + Configuration.FONT_HEIGHT / 4;
		max_items = 5;
		bar_width = Configuration.SCREEN_WIDTH;
		bar_height = Configuration.MENU_HEIGHT;
		menu_width = Configuration.FONT_WIDTH * 5 + 50;
		menu_height = line_height * book_menu.items.length + 20;
		middle_text = null;
		right_menu_text = "返回";
		command_manager = manager;
		
		setScreenSize(Configuration.SCREEN_WIDTH, Configuration.SCREEN_HEIGHT);
		setImageSize(Configuration.SCREEN_WIDTH, Configuration.MENU_HEIGHT);
		setPos(0, Configuration.SCREEN_HEIGHT);
		setFontSize(Configuration.FONT_SIZE);
		setColor(0xc2c2c2, 0);
	}
	public void setImageSize(int width, int height){
		super.setImageSize(width, height);
		images = new Image[3];
		images[0] = Image.createImage(width, height);
		images[1] = Image.createImage(menu_width, menu_height);
		images[2] = Image.createImage(menu_width, menu_height);
		graphics = new Graphics[3];
		graphics[0] = images[0].getGraphics();
		graphics[1] = images[1].getGraphics();
		graphics[2] = null;
		Graphics g = images[2].getGraphics();
		g.setColor(0x666666);
		g.fillRect(0, 0, menu_width, menu_height);
		g = null;
	}

	public void repaint_bar(){
		Graphics g = graphics[0];
		g.setColor(bgcolor);
		g.fillRect(0, 0, image_width, image_height);
		g.setColor(0x999999);
		g.drawLine(0, 0, image_width, 0);
		g.setColor(0xffffff);
		g.drawLine(0, 1, image_width, 1);
		
		g.setColor(color);
		g.drawString("菜单", 4, 2, Graphics.TOP|Graphics.LEFT);
		g.drawString(right_menu_text, image_width-4, 2, Graphics.TOP|Graphics.RIGHT);
		if (middle_text!=null)
			g.drawString(middle_text, image_width/2, 2, Graphics.HCENTER|Graphics.TOP);
		state = 0;
	}
	public void repaint_menu(){
		int leftGrid = 20;
		Graphics g = graphics[1];
		g.setColor(0x999999);
		g.drawRect(0, 0, menu_width-1, menu_height-1);
		g.setColor(0xffffff);
		g.drawRect(1, 1, menu_width-3, menu_height-3);
		g.setColor(0xeeeeee);
		g.fillRect(2, 2, menu_width-4, menu_height-4);

		// current line
		System.out.println("menu cursor:"+cursor);
		g.setColor(0xa8d8eb);
		g.drawRoundRect(5, 2+line_height*cursor, menu_width-10, line_height, 5, 5);
		g.setColor(0xe0edf3);
		g.fillRect(6, 3+line_height*cursor, menu_width-10-2, 18);
		g.setColor(0x333333);
		g.fillRect(11, 2+8+line_height*cursor, 6, 6);
		
		// left grid line
		g.setColor(0xdddddd);
		g.drawLine(leftGrid, 3, leftGrid, menu_height-3);
		g.setColor(0xffffff);
		g.drawLine(leftGrid+1, 3, leftGrid+1, menu_height-3);
		
		g.setColor(0);
		for(int i=0; i<book_menu.items.length; i++){
			g.drawString(book_menu.items[i], leftGrid+5, 5+line_height*i, Graphics.TOP|Graphics.LEFT);	
		}
	}
	public void activeMenu(){
		if (state==0){
			repaint_menu();
			state = 1;
		}
		else
			state = 0;
	}
	public void cursorUp(){

	}

	public void cursorDown(){
		
	}
	public void cursorLeft(){
		
	}
	public void cursorRight(){
		//TODO: run command
	}
	public void setMiddleText(String text){
		middle_text = text;
		repaint_bar();
	}
	public void setRightMenuText(String text){
		right_menu_text = text;
		repaint_bar();
	}
	public void paint(Graphics g) {
		if (images==null || images.length<3 || images[0]==null) return;
		
		g.drawImage(images[0], posx, posy, Graphics.BOTTOM|Graphics.LEFT);
		if(state>0){ //pop menu opened
			g.drawImage(images[2], posx+7, posy-image_height-1, Graphics.BOTTOM|Graphics.LEFT);
			g.drawImage(images[1], posx+4, posy-image_height-4, Graphics.BOTTOM|Graphics.LEFT);
		}
		
	}
	public void keyReleased(int keyCode) {
		switch(keyCode){
		case -1:  // cursor up
			if (cursor>0){ 
				cursor--;
				repaint_menu();
			}
			break;
		case -2:  // cursor down
			if (cursor<max_items-1){
				cursor++;
				repaint_menu();
			}
			break;
		case -4: // cursor right
			break;
		case -3: // cursor left = active(deactive) menu
		case -6:
		case -7:
			activeMenu();
			break;
		}
	}
}
