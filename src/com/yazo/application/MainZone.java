package com.yazo.application;

import java.io.IOException;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.yazo.books.BrowserContent;
import com.yazo.books.LineContent;
import com.yazo.books.LinkContent;
import com.yazo.model.BrowserCommand;
import com.yazo.model.ICommandManager;
import com.yazo.tools.ImageZone;

public class MainZone extends ImageZone {
	private LineContent content;
	public int current_page;
	private int cursor, total_links;
	public String back_url,current_cmd,next_cmd;
	public int catalog_bg, text_bg;
	private Image arrow1;
	public int line_height;
	private int font_height, line_space, line_top_padding, line_bottom_padding;
	private ICommandManager command_manager;
	
	public MainZone(ICommandManager manager) {
		super();
		cursor = 0;
		total_links = 0;
		next_cmd = null;
		current_cmd = null;
		catalog_bg = 0xfdface;
		text_bg = 0xaaaaaa;
		arrow1 = null;
		command_manager = manager;
		try {
			arrow1 = Image.createImage("/arrow-blue.png");
		} catch (IOException e) {
			e.printStackTrace();
		}

		setScreenSize(Configuration.SCREEN_WIDTH, Configuration.SCREEN_HEIGHT);
		setImageSize(Configuration.SCREEN_WIDTH, Configuration.SCREEN_HEIGHT - Configuration.HEADER_HEIGHT - Configuration.MENU_HEIGHT);
		setPos(0, Configuration.HEADER_HEIGHT);
		setFontSize(Configuration.FONT_SIZE);
		setColor(0xdde4ec, 0x363636);
		font_height = Configuration.FONT_HEIGHT;
		line_space =  font_height/4;
		line_height = font_height + line_space;
		line_top_padding = line_space/2;
		line_bottom_padding = line_space - line_top_padding;
	}
	public void setImageSize(int width, int height){
		super.setImageSize(width, height);
		images = new Image[1];
		images[0] = Image.createImage(width, height);
		graphics = new Graphics[1];
		graphics[0] = images[0].getGraphics();
	}

	public void setContent(LineContent content){
		this.content = content;
		this.back_url = "home"; // from content.back_url; ??
		setCurrentPage(0);
	}
	public void setCurrentPage(int page){
		if (page>=0 && page<content.page_count) {
			cursor = 0;
			current_page = page;
			String title = "" + (current_page+1) + " / " + content.page_count;
			if (command_manager!=null) command_manager.command_callback(BrowserCommand.SET_MENU_TITLE, title);
			paintImage();
		}
	}
	public void paintImage(){
		Graphics g = graphics[0];
		g.setColor(bgcolor);
		g.fillRect(0, 0, image_width, image_height);
		g.setColor(color);
		int posy = line_top_padding;
		int lnk_cnt = 0;
		next_cmd = null;
		int st = content.page_pos[current_page];
		int ed = content.page_pos[current_page+1];
		for(int i=st; i<ed; i++){
			BrowserContent c = content.lines[i];
			if (c.content_type == "text") {
				g.setColor(color);
				g.drawString(c.content, 10, posy, Graphics.TOP|Graphics.LEFT);
			} else if (c.content_type == "link") {
				LinkContent lc = (LinkContent)c;
				if (lnk_cnt == cursor){
					g.setColor(color);
					g.fillRect(10, posy-line_top_padding, image_width-19, line_height-1);
					if(arrow1!=null) g.drawImage(arrow1, 12, posy+font_height/2, Graphics.VCENTER|Graphics.LEFT);
					g.setColor(bgcolor);
					g.drawString(lc.content, 20, posy, Graphics.TOP|Graphics.LEFT);
					next_cmd = lc.url;
				} else {
					g.setColor(color);
					g.drawString(c.content, 20, posy, Graphics.TOP|Graphics.LEFT);
					if(arrow1!=null) g.drawImage(arrow1, 12, posy+6, Graphics.TOP|Graphics.LEFT);
				}
				g.setColor(0x999999);
				g.drawLine(10, posy+line_height-line_bottom_padding, image_width-10, posy+line_height-line_bottom_padding);
				lnk_cnt++;
			} else if (c.content_type == "line") {
				g.drawLine(10, posy, image_width, posy);
			}
			posy += line_height;
		}
		total_links = lnk_cnt;
	}
	public void nextPage(){
		setCurrentPage(current_page+1);
	}
	public void prevPage(){
		setCurrentPage(current_page-1);
	}
	public void cursorUp(){
		if (cursor>0) {
			cursor--;
			paintImage();
		} else{
			prevPage();
			cursor=total_links-1;
			paintImage();
		}
		
	}
	public void cursorDown(){
		if (cursor<total_links-1) {
			cursor++;
			paintImage();
		} else nextPage();
	}
	public void onClicked(){
	}
	public void paint(Graphics g) {
		if(images!=null && images.length>0 && images[0]!=null)
			g.drawImage(images[0], posx, posy, Graphics.TOP|Graphics.LEFT);
		
	}
	
	public void keyReleased(int keyCode) {
// #ifdef DBG_KEYS
		System.out.println("main_zone key:" + keyCode);
// #endif		
		if (keyCode == -1){
			cursorUp();
		} else if (keyCode == -2) {
			cursorDown();
		} else if (keyCode == -3) {
			prevPage();
		} else if (keyCode == -4) {
			nextPage();
		} else if (keyCode == -5) {
// #ifdef DBG_KEYS
			System.out.println("run command:" + next_cmd);
// #endif			
			if(next_cmd!=null && command_manager!=null)
				command_manager.command_callback(BrowserCommand.GOTO_URL, next_cmd);
		} else if (keyCode == -6){
			if (command_manager!=null) command_manager.command_callback(BrowserCommand.ACTIVE_MENU, null);
		} else if (keyCode == -7){
			if (command_manager!=null) command_manager.command_callback(BrowserCommand.GO_BACK, null);
		}
	}
	
	
}
