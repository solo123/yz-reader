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
import com.yazo.application.biz.Config;
import com.yazo.application.thread.ThreadJobCmccSimulator;
import com.yazo.model.ConfigKeys;

public class CmccMIDlet extends MIDlet implements CommandListener {
	private Form frm = new Form("请选择操作");
	private Display display;
	private Command cmd_reg_nop = new Command("register(noproxy)", Command.SCREEN, 1);
	private Command cmd_reg_pro = new Command("register(proxy)", Command.SCREEN, 1);
	private Command cmd_welcome = new Command("Welcome", Command.SCREEN, 1);
	private Config config = Config.getInstance();

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {

	}

	protected void pauseApp() {

	}

	protected void startApp() throws MIDletStateChangeException {
		display = Display.getDisplay(this);
		display.setCurrent(frm);
		frm.addCommand(cmd_reg_nop);
		frm.addCommand(cmd_reg_pro);
		frm.addCommand(cmd_welcome);
		frm.setCommandListener(this);
	}

	public void commandAction(Command c, Displayable d) {
		config.add(ConfigKeys.TEST_FUNCS, "");
		if (c == cmd_reg_nop){
			config.add(ConfigKeys.TEST_FUNCS, "[register]");
		} else if (c == cmd_reg_pro){
			config.add(ConfigKeys.TEST_FUNCS, "[register1]");
		} else if (c==cmd_welcome){
			config.add(ConfigKeys.TEST_FUNCS, "[welcome]");
		}
		ThreadJobCmccSimulator tjs = new ThreadJobCmccSimulator(frm);
		tjs.start();
	}

}
