package com.yazo.ui;

import javax.microedition.lcdui.Image;
import com.yazo.books.*;

public class ScreenPages {
	int width, height;
	BrowserContent[] contents;
	Image[] screens;
	
	public ScreenPages(BrowserContent[] contents){
		this.contents = contents;
		screens = new Image[20];
	}
	
	public void PreDraw(){
		int i = 0;
		
	}
}
