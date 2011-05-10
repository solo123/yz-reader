package com.yazo.test;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;

import com.yazo.application.ui.CtlExplorer;
import com.yazo.application.ui.CtlHeader;
import com.yazo.ui.UiContainer;

public class UiTest extends Canvas {
	private UiContainer container = new UiContainer();

	public UiTest(){
		setFullScreenMode(true);
		int width = getWidth();
		int height = getHeight();

		CtlExplorer explorer = new CtlExplorer();
		explorer.setSize(100, 100);
		explorer.setPos(20, 20, Graphics.TOP|Graphics.LEFT);
		container.addControl(explorer);

		CtlHeader header = new CtlHeader();
		header.setSize(width, 30);
		header.setPos(0, 0, Graphics.TOP|Graphics.LEFT);
		header.setTitle("Hello,你好！");
		container.addControl(header);
	}
	protected void paint(Graphics g) {
		System.out.println("UiTest paint.");
		container.paint(g);
	}

}
