package com.yazo.application;

import com.yazo.ui.Browser;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class MainMIDlet extends MIDlet {
	Browser browser;
	public MainMIDlet(){
		browser = new Browser();
	}
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		//MainUI mainui = new MainUI(Display.getDisplay(this));
		Display.getDisplay(this).setCurrent(browser);
	}
}