package com.yazo.contents;

public class BookMenu {
	public String[] items, cmds;
	public String current_book_id, current_chapter_id;
	
	public BookMenu(){
		items = new String[5];
		cmds = new String[5];
		setMenu("default");
		current_book_id = current_chapter_id = null;
	}
	public void setMenu(String menutype){
		if (menutype=="default"){
			items[0] = "1. 搜索图书";
			items[1] = "2. 系统设置";
			items[2] = "3. 用户帮助";
			items[3] = "4. 软件升级";
			items[4] = "5. 返回首页";
			cmds[0] = "CMD_SEARCH";
			cmds[1] = "config_page";
			cmds[2] = "help_page";
			cmds[3] = "upgrade_page";
			cmds[4] = "home";
		} else {
			items[0] = "1. 下一章";
			items[1] = "2. 上一章";
			items[2] = "3. 查看目录";
			items[3] = "4. 搜索图书";
			items[4] = "5. 返回首页";
			cmds[0] = "books/" + current_book_id + "/chapter/" + (Integer.parseInt(current_chapter_id)+1);
			cmds[1] = "books/" + current_book_id + "/chapter/" + (Integer.parseInt(current_chapter_id)-1);
			cmds[2] = "books/" + current_book_id + "/catalog";
			cmds[3] = "CMD_SEARCH";
			cmds[4] = "home";
		}
	}
}
