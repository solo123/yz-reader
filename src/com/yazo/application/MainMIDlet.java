package com.yazo.application;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.yazo.CMCC.ProcessStart;

public class MainMIDlet extends MIDlet {
	public MainMIDlet(){
	}
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
	}
	protected void pauseApp() {
	}
	protected void startApp() throws MIDletStateChangeException {
		new Browser(this,Display.getDisplay(this));
		new Thread(){
			public void run() {
				ProcessStart start = new ProcessStart();
				start.startProcess();
			};
		}.start();
	}
	public void quit(){
		notifyDestroyed();
	}
	
	
	
}
