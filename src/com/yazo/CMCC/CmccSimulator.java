package com.yazo.CMCC;


import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Form;
import com.yazo.CMCC.biz.CommandQueue;
import com.yazo.CMCC.biz.SimCommand;
import com.yazo.application.biz.Config;
import com.yazo.network.WebSite;
import com.yazo.util.ServiceData;


public class CmccSimulator extends Thread {
	public static ServiceData serviceData;
	static Vector progressIDFree = null;
	static Vector progressIDCharge = null;
	public static int total = 0;
	public static int tempInt = 0;
	private Config config = Config.getInstance();
	
	private CmccWebsite cmcc;
	private Form form = null;
	
	public boolean continue_run = true;
	private CommandQueue queue = new CommandQueue();
	
	public void setForm(Form form){
		this.form = form;
	}
	
	public void run(){
		cmcc = new CmccWebsite("http://211.140.17.83/cmread/portalapi", "CMREAD_JavaLS_V1.50_101221", "12101017", "05001001", false, "http://bk-b.info/reader/logs/addop");
		cmcc.on_debug = false;
		while(continue_run){
			if (queue.hasElement()){
				SimCommand cmd = queue.get();
				try {
					runCommand(cmd);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else sleep(1000);
		}
		printDbg("Stopped.");
	}
	
	public void addCommand(SimCommand cmd){
		queue.add(cmd);
	}
	public void runCommand(SimCommand cmd) throws IOException{
//		config.add(ConfigKeys.YZ_CLIENT_ID, 100);
//		config.add(ConfigKeys.CMCC_USER_AGENT, "CMREAD_JavaLS_V1.50_101221");
//		config.add(ConfigKeys.CMCC_SERVICE, "http://211.140.17.83/cmread/portalapi");
//		config.add(ConfigKeys.CMCC_CLIENT_PASSWORD, "12101017");
//		config.add(ConfigKeys.CONTENT_SERVER, "http://bk-b.info");
//		config.add(ConfigKeys.CMCC_DEBUG, 1);
		
		printDbg("run [" + cmd.command + "]");
		if(cmd.command.equals("stop")){
			continue_run = false;
		} else if (cmd.command.equals("register")){
			printDbg("Start register.");
			cmcc.register("register", "Response/RegisterRsp/UserInfo/userID");
			printDbg("end register, user_id=" + cmcc.cmcc_user_id);
		} else if(cmd.command.equals("authenticate")){
			printDbg("authenticate:" + cmcc.authenticate("authenticate2"));
		}else if(cmd.command.equals("welcome")){
			printDbg("Start Welcome.");
			String s = cmcc.welcome("getClientWelcomeInfo");
			printDbg("end welcome:" + s);
		} else if(cmd.command.equals("bk-b")){
			WebSite ws = new WebSite();
			byte[] buf = ws.post("http://bk-b.info/reader/pages/home", null, "");
			printDbg("error["+ws.error_code+"]:" + ws.error_message);
			printDbg("data:[" + new String(buf) + "]");
		} else if (cmd.command.equals("bk-b.proxy")){
			WebSite ws = new WebSite();
			ws.use_cmcc_proxy = true;
			byte[] buf = ws.post("http://bk-b.info/reader/pages/home", null, "");
			printDbg("error["+ws.error_code+"]:" + ws.error_message);
			printDbg("data:[" + new String(buf) + "]");
		} else if (cmd.command.equals("catalog")){
			printDbg("Start Catalog.");
			String s = cmcc.getCatalogInfo("getCatalogInfo","352");
			printDbg("end catalog:" + s);
		} else if (cmd.command.equals("content")){
			printDbg("Start Content.");
			String s = cmcc.getContentInfo("getContentInfo", "349558873", "352");
			printDbg("end content:" + s);
		} else if (cmd.command.equals("chapter")){
			printDbg("Start Chapter.");
			String s = cmcc.getChapterInfo("getChapterInfo", "349558873", "349558875");
			printDbg("end chapter:" + s);
		} else if (cmd.command.equals("product")){
			printDbg("Start Product.");
			String s = cmcc.getContentProductInfo("getContentProductInfo", "67065", "67067");
			printDbg("end product:" + s);
		} else if (cmd.command.equals("product content")){
			printDbg("Start Product Content.");
			String s = cmcc.subscribeContent("subscribeContent", "67065", "15553");
			printDbg("end product chapter:" + s);
		} else if (cmd.command.equals("subscribeCatalog")){
			printDbg("Start subscribeCatalog.");
			String s = cmcc.subscribeCatalog("subscribeCatalog", "352");
			printDbg("end subscribeCatalog:" + s);
		}
	}
	
	private void sleep(int millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
	private void printDbg(String message){
		System.out.println("[DBG]" + message);
		if (form!=null){
			if (message.length()>100)
				form.append(message.substring(0,100)+"...共" + message.length() + "字");
			else
				form.append(message);
		}
	}

}
