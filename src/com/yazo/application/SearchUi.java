package com.yazo.application;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import com.yazo.model.BrowserCommand;

public class SearchUi implements CommandListener {
	private TextBox textbox;
	private Command cmd1, cmd2;
	private Browser browser;
	private Display display;
	public String result;
	
	public SearchUi(){
		textbox = new TextBox("请输入搜索关键字：", "", 100, TextField.ANY);
		result = null;
		cmd1 = new Command("确定",Command.BACK,1);  
	    cmd2 = new Command("返回",Command.BACK,1); 
	    textbox.addCommand(cmd2);
	    textbox.addCommand(cmd1);
	    textbox.setCommandListener(this);
	}
	
	public void inputSearchText(Browser browser, Display display){
		this.browser = browser;
		this.display = display;
		display.setCurrent(textbox);
	}

	public void commandAction(Command c, Displayable d) {
		if (c==cmd1){
			result = textbox.getString();
			this.browser.execute_command(BrowserCommand.SEARCH_TEXT, result);
		} else if (c==cmd2) {
			result = null;
			System.out.println("cmd2");
		}
		this.display.setCurrent(this.browser);
	}
	
}
