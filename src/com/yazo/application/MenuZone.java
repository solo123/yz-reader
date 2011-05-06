package com.yazo.application;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.yazo.books.BookMenu;
import com.yazo.model.ICommandManager;
import com.yazo.tools.ImageZone;

public class MenuZone extends ImageZone {
	public int state;
	public Image menuImage, menuShadowImage;
	private int menu_width, menu_height, cursor, max_items, line_height;
	private BookMenu book_menu;
	private String middle_text, right_menu_text;
	private ICommandManager command_manager;
	
	public MenuZone(ICommandManager manager) {
		super();
		state = 0;
		cursor = 0;
		line_height = 20;
		max_items = 5;
		menu_width = 150;
		menu_height = 130;
		middle_text = null;
		right_menu_text = "退出";
		command_manager = manager;
		menuImage = Image.createImage(menu_width, menu_height);
		menuImage.getGraphics();
		menuShadowImage = Image.createImage(menu_width, menu_height);
		Graphics g0 = menuShadowImage.getGraphics();
		g0.setColor(0x666666);
		g0.fillRect(0, 0, menu_width, menu_height);
		g0 = null;
		book_menu = new BookMenu();
		
		setScreenSize(Configuration.SCREEN_WIDTH, Configuration.SCREEN_HEIGHT);
		setImageSize(Configuration.SCREEN_WIDTH, Configuration.MENU_HEIGHT);
		setPos(0, Configuration.SCREEN_HEIGHT);
		setFontSize(Configuration.FONT_SIZE);
		setColor(0xc2c2c2, 0);

		line_height = Configuration.FONT_HEIGHT;
		line_height = line_height + line_height/4;
		menu_width = default_font.stringWidth("系统配置") + 50;
		menu_height = line_height * max_items + 4;
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
		if (cursor>0){ 
			cursor--;
			repaint_menu();
		}
	}

	public void cursorDown(){
		if (cursor<max_items-1){
			cursor++;
			repaint_menu();
		}
	}
	public void cursorLeft(){
		activeMenu();
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
		if (keyCode == -1){
			cursorUp();
		} else if (keyCode == -2) {
			cursorDown();
		} else if (keyCode == -3) {
			cursorLeft();
		} else if (keyCode == -4) {
			cursorRight();
		} else if (keyCode == -5) {
			//TODO: menu action, gotoUrl(main_zone.current_cmd);
		} else if (keyCode == -6){
			activeMenu();
		} else if (keyCode == -7){
			activeMenu();
		}
	}
	

}
