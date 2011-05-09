package com.yazo.application;

import com.yazo.util.StringUtil;

public class HistoryManager {
	private String[] page_levels;
	private int page_level;
	public HistoryManager(){
		page_levels = new String[3];
		page_levels[0] = "home";
		page_levels[1] = page_levels[2] = null;
		page_level = 0;
	}
	
	public boolean isTopLevel(){
		return page_level < 1;
	}
	public void addHistory(String url){
// #ifdef DBG
//@		System.out.println("Add to history:" + url);
// #endif		
		if (url.equals(page_levels[0])){
			page_level = 0;
		} else {
			String[] s = StringUtil.split(url, "/");
			if (s!=null && s.length>2 && !s[2].equals("catalog")){
				page_level = 2;
				page_levels[1] = s[0]+ "/" + s[1] + "/catalog";
				page_levels[2] = url;
			} else {
				page_level = 1;
				page_levels[1] = url;
			}
		}
	}
	public String getBackUrl(){
		if (page_level<1) return null;
		page_level--;
		return page_levels[page_level];
	}
	
}
