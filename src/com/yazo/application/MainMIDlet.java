package com.yazo.application;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class MainMIDlet extends MIDlet {
	public MainMIDlet(){
	}
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
	}
	protected void pauseApp() {
	}
	protected void startApp() throws MIDletStateChangeException {
		new Browser(this,Display.getDisplay(this));
	}
	public void quit(){
		notifyDestroyed();
	}
	
	
	
}
