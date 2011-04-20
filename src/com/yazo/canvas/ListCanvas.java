package com.yazo.canvas;

import com.yazo.ui.*;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import com.sun.perseus.j2d.RGB;

public class ListCanvas extends Canvas implements CommandListener {
	MainMenu main_menu;
	MainBoard main_board;
	int width, height;
	public ListCanvas(){
		width = getWidth();
		height = getHeight();
		main_menu = new MainMenu(width, 100);
		main_board = new MainBoard(100, width, height);
	}

	protected void paint(Graphics g) {
		g.setColor(0xFF0000);
		g.fillRect(0,0,width,height);
		
		main_menu.paint(g);
		main_board.paint(g);
	}
	
	

	public void commandAction(Command c, Displayable d) {
		
	}

}
