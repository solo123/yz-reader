package com.yazo.application;

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
	private Command cmd_reg = new Command("register", Command.SCREEN, 1);
	private Config config = Config.getInstance();

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {

	}

	protected void pauseApp() {

	}

	protected void startApp() throws MIDletStateChangeException {
		display = Display.getDisplay(this);
		display.setCurrent(frm);
		frm.addCommand(cmd_reg);
		frm.setCommandListener(this);
	}

	public void commandAction(Command c, Displayable d) {
		if (c == cmd_reg){
			d.setTitle("run register...");
			
			config.add(ConfigKeys.YZ_CLIENT_ID, 100);
			config.add(ConfigKeys.CMCC_USER_AGENT, "CMREAD_Javamini_WH_V1.05_100407");
			config.add(ConfigKeys.CMCC_SERVICE, "http://211.140.17.83/cmread/portalapi");
			config.add(ConfigKeys.CMCC_CLIENT_PASSWORD, "12101017");
			System.out.println("RUN CMCC!!!!!!");
			ThreadJobCmccSimulator tjs = new ThreadJobCmccSimulator();
			tjs.start();
			d.setTitle("register done.");
		}
	}

}
