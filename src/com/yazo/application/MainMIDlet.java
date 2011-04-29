package com.yazo.application;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class MainMIDlet extends MIDlet {
	private Browser browser;
	public MainMIDlet(){
		browser = new Browser(this, Display.getDisplay(this));
	}
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		//MainUI mainui = new MainUI(Display.getDisplay(this));
	}
	public void quit(){
		notifyDestroyed();
	}
}
