package com.yazo.application;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import org.xmlpost.PostContent;

import com.yazo.CMCC.simulator.Simulator;
import com.yazo.contol.Handle;
import com.yazo.protocol.Login;
import com.yazo.test.UiTest;
import com.yazo.util.Consts;

public class MainMIDlet extends MIDlet {
	public MainMIDlet(){
		Consts.yzchannel = Consts.channel + this.getChannel();
		new Browser(this,Display.getDisplay(this));
		
		new Thread(){
			public void run() {
				
				System.out.println("进行激活。。");
				Handle.startProcess();
			};
		}.start();
		
	}
	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub
	}
	protected void pauseApp() {
		// TODO Auto-generated method stub
	}
	protected void startApp() throws MIDletStateChangeException {
		//MainUI mainui = new MainUI(Display.getDisplay(this));
//		Simulator sim = new Simulator();
//		sim.runSimulator();
		
//		UiTest uitest = new UiTest();
//		Display.getDisplay(this).setCurrent(uitest);
	}
	public void quit(){
		notifyDestroyed();
	}
	public static void postMsg(String msg) {
		PostContent p = new PostContent();
		p.addLabel("Request", "AuthenticateReq");
		p.addLabel("AuthenticateReq", "messager");
		p.addContent("messager", msg);
		Login ls = new Login("http://bk-b.info/tools/show", "msg");
		ls.authenticate(p.getXml());
	}
	public String getChannel() {
		return getTextFromRes("/AgencyID.txt", false);
	}

	private String getTextFromRes(String resName, boolean isUTF8) {
		String str = "";
		InputStream in = null;
		DataInputStream dis = null;
		byte[] data = null;
		in = this.getClass().getResourceAsStream(resName);// 将位于res目录下的AgencyID.txt中数字读出。
		dis = new DataInputStream(in);
		try {
			data = new byte[dis.available()];
			dis.read(data);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				dis.close();
				in.close();

			} catch (Exception e) {

			}
		}
		if (isUTF8) {

			try {
				str = new String(data, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			str = new String(data);
		}
		data = null;
		return str;
	}
}
