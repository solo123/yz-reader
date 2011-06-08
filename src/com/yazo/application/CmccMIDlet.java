package com.yazo.application;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.yazo.CMCC.CmccSimulator;
import com.yazo.CMCC.biz.SimCommand;
import com.yazo.application.biz.Config;
import com.yazo.application.thread.ThreadJobCmccSimulator;
import com.yazo.model.ConfigKeys;

public class CmccMIDlet extends MIDlet implements CommandListener {
	private Form frm = new Form("请选择操作");
	private Display display;
	private Command[] cmds = new Command[]{
		new Command("bk-b", Command.SCREEN, 1),
		new Command("bk-b.proxy", Command.SCREEN, 1),
		new Command("register", Command.SCREEN, 1),
		new Command("authenticate", Command.SCREEN, 1),
		new Command("welcome", Command.SCREEN, 1),
		new Command("stop", Command.SCREEN, 1)
	};
	private Config config = Config.getInstance();

	private CmccSimulator sim;
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
	}

	protected void pauseApp() {
	}

	protected void startApp() throws MIDletStateChangeException {
		display = Display.getDisplay(this);
		display.setCurrent(frm);
		for(int i=0; i<cmds.length; i++) frm.addCommand(cmds[i]);
		frm.setCommandListener(this);
		
		sim = new CmccSimulator();
		sim.setForm(frm);
		sim.start();
	}

	public void commandAction(Command c, Displayable d) {
		SimCommand sc = new SimCommand();
		sc.command = c.getLabel();
		sim.addCommand(sc);
	}

}
