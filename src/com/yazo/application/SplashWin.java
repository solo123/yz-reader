package com.yazo.application;

import java.io.IOException;

import javax.microedition.lcdui.Image;

public class SplashWin {
	public Image image;
	public SplashWin(){
		try {
			image = Image.createImage("/ebook.jpg");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
