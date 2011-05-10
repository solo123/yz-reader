package com.yazo.application.ui;

import javax.microedition.lcdui.Graphics;

import com.yazo.ui.UiControl;

public class CtlExplorer extends UiControl  {

	public void paint(Graphics g) {
		System.out.println("CtlExplorer paint:" + posx + ", " + posy + "," +width+", "+height);
		g.setColor(0xFF0000);
		g.fillRect(posx, posy, width, height);
		
	}


}
