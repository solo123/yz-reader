package com.yazo.application.ui;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.yazo.application.biz.Config;
import com.yazo.contents.BrowserContent;
import com.yazo.contents.LineContent;
import com.yazo.contents.LinkContent;
import com.yazo.contents.PageContent;
import com.yazo.model.BrowserCommand;
import com.yazo.model.ConfigKeys;
import com.yazo.model.ICommandListener;

public class CtlExplorer extends UiControl  {
	private PageContent content;
	public int current_page, total_pages;
	private int cursor, total_links;
	public String back_url,current_cmd,next_cmd;
	public int catalog_bg, text_bg, color, bgcolor;
	private Image arrow1, img;
	private Graphics g;
	public int line_height;
	private int font_height, line_space, line_top_padding, line_bottom_padding;
	private ICommandListener command_manager;
	private Config config = Config.getInstance();

	public CtlExplorer(){
		super();
		cursor = 0;
		total_links = 0;
		next_cmd = null;
		current_cmd = null;
		catalog_bg = 0xfdface;
		text_bg = 0xaaaaaa;
		arrow1 = null;
		command_manager = null;
		total_pages = 0;
		
		try {
			arrow1 = Image.createImage("/arrow-blue.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		bgcolor = 0xdde4ec;
		color = 0x363636;
		
		font_height = config.getInt(ConfigKeys.FONT_HEIGHT);
		line_space =  font_height/4;
		line_height = font_height + line_space;
		line_top_padding = line_space/2;
		line_bottom_padding = line_space - line_top_padding;		
	}
	public void setContent(PageContent content, int page){
		this.content = content;
		this.current_page = page;
		this.total_pages = content.getTotalPages();
	}
	public void setCommandManager(ICommandListener manager){
		command_manager = manager;
	}
	private void paintImage(){
		if (img == null){
			img = Image.createImage(width, height);
			g = img.getGraphics();
		}
		g.setColor(bgcolor);
		g.fillRect(0, 0, width, height);
		g.setColor(color);
		int posy = line_top_padding;
		int lnk_cnt = 0;
		next_cmd = null;
		Vector page = (Vector)content.getPage(current_page);
		if (page==null) return;
		
		Enumeration linecontent = page.elements();
	    while(linecontent.hasMoreElements()){
			BrowserContent c = (BrowserContent)linecontent.nextElement();
			if (c.content_type == "text") {
				g.setColor(color);
				g.drawString(c.content, 10, posy, Graphics.TOP|Graphics.LEFT);
			} else if (c.content_type == "link") {
				LinkContent lc = (LinkContent)c;
				if (lnk_cnt == cursor){
					g.setColor(color);
					g.fillRect(10, posy-line_top_padding, width-19, line_height-1);
					if(arrow1!=null && lc.arrow_style!=null && lc.arrow_style.equals("1")) g.drawImage(arrow1, 12, posy+font_height/2, Graphics.VCENTER|Graphics.LEFT);
					g.setColor(bgcolor);
					g.drawString(lc.content, 20, posy, Graphics.TOP|Graphics.LEFT);
					next_cmd = lc.url;
				} else {
					g.setColor(color);
					g.drawString(c.content, 20, posy, Graphics.TOP|Graphics.LEFT);
					if(arrow1!=null && lc.arrow_style!=null && lc.arrow_style.equals("1")) g.drawImage(arrow1, 12, posy+6, Graphics.TOP|Graphics.LEFT);
				}
				g.setColor(0x999999);
				g.drawLine(10, posy+line_height-line_bottom_padding, width-10, posy+line_height-line_bottom_padding);
				lnk_cnt++;
			} else if (c.content_type == "line") {
				g.drawLine(10, posy, width, posy);
			}
			posy += line_height;
		}
		total_links = lnk_cnt;		
	}
	public void setCurrentPage(int page){
		if (page>=0 && page<total_pages) {
			cursor = 0;
			current_page = page;
			paintImage();
			if(command_manager!=null) command_manager.execute_command(BrowserCommand.REFRESH_STATUS, null);
		}
	}
	public void nextPage(){
		setCurrentPage(current_page+1);
	}
	public void prevPage(){
		setCurrentPage(current_page-1);
	}
	public void paint(Graphics g) {
		if (img!=null)
			g.drawImage(img, posx, posy, align);
	}
	public int keyReleased(int keyCode) {
		// #ifdef DBG_KEYS
		System.out.println("main_zone key:" + keyCode);
		// #endif
		int k = 0;
		switch(keyCode){
		case -1:
			if (cursor>0) {
				cursor--;
				paintImage();
			} else{
				prevPage();
				cursor=total_links-1;
				paintImage();
			}
			break;
		case -2:
			if (cursor<total_links-1) {
				cursor++;
				paintImage();
			} else nextPage();
			break;
		case -3:
			prevPage();
			break;
		case -4:
			nextPage();
			break;
		case -5:
			// #ifdef DBG_KEYS
			System.out.println("run command:" + next_cmd);
			// #endif			
			if(next_cmd!=null && command_manager!=null)
				command_manager.execute_command(BrowserCommand.GOTO_URL, next_cmd);
			break;
		default:
			k = keyCode;
			break;
		}
		return k;
	}

}
