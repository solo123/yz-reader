package com.yazo.application;

import com.yazo.contents.BookMenu;
import com.yazo.util.StringUtil;

public class HistoryManager {
	private String[] page_levels;
	private int page_level;
	private BookMenu book_menu;
	public HistoryManager(BookMenu book_menu){
		page_levels = new String[3];
		page_levels[0] = "home";
		page_levels[1] = page_levels[2] = null;
		page_level = 0;
		this.book_menu = book_menu;
	}
	
	public boolean isTopLevel(){
		return page_level < 1;
	}
	public int getCurrentLevel(){
		return page_level;
	}
	public void addHistory(String url){
// #ifdef DBG
		System.out.println("Add to history:" + url);
// #endif		
		if (url.equals(page_levels[0])){
			page_level = 0;
		} else {
			String[] s = StringUtil.split(url, "/");
			if (s!=null && s.length>2 && !s[2].equals("catalog")){
				page_level = 2;
				page_levels[1] = s[0]+ "/" + s[1] + "/catalog";
				page_levels[2] = url;
				book_menu.current_book_id = s[1];
				if (s.length>3) book_menu.current_chapter_id = s[3];
				else book_menu.current_chapter_id = "1";
			} else {
				page_level = 1;
				page_levels[1] = url;
			}
		}
		if (page_level>1)
			book_menu.setMenu("content");
		else
			book_menu.setMenu("default");

	}
	public String getBackUrl(){
		if (page_level<1) return null;
		page_level--;
		return page_levels[page_level];
	}
	
}
