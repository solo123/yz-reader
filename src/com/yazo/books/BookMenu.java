package com.yazo.books;

public class BookMenu {
	public String[] items, cmds;
	public BookMenu(){
		items = new String[5];
		cmds = new String[5];
		setMenu("default");
	}
	public void setMenu(String menutype){
		if (menutype=="default"){
			items[0] = "1. 搜索图书";
			items[1] = "2. 系统设置";
			items[2] = "3. 用户帮助";
			items[3] = "4. 软件升级";
			items[4] = "5. 返回首页";
			cmds[0] = "Search";
			cmds[1] = "Config";
			cmds[2] = "Help";
			cmds[3] = "Upgrade";
			cmds[4] = "Home";
		} else {
			items[0] = "1. 下一章";
			items[1] = "2. 上一章";
			items[2] = "3. 查看目录";
			items[3] = "4. 搜索图书";
			items[4] = "5. 返回首页";
			cmds[0] = "nextChapter";
			cmds[1] = "prevChapter";
			cmds[2] = "catalog";
			cmds[3] = "Search";
			cmds[4] = "Home";
		}
	}
}
