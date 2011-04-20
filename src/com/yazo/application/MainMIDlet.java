package com.yazo.application;

import com.yazo.canvas.*;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class MainMIDlet extends MIDlet {
	ListCanvas cv;
	public MainMIDlet(){
		cv = new ListCanvas();
	}
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		//MainUI mainui = new MainUI(Display.getDisplay(this));
		Display.getDisplay(this).setCurrent(cv);

	}

}
