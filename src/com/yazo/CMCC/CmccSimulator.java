package com.yazo.CMCC;


import java.util.Vector;


import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Form;


import com.yazo.CMCC.biz.CommandQueue;
import com.yazo.CMCC.biz.RegisterParser;
import com.yazo.CMCC.biz.SimCommand;
import com.yazo.application.biz.Config;
import com.yazo.model.ConfigKeys;
import com.yazo.network.WebSite;

import com.yazo.util.HBase64;
import com.yazo.util.MD5;
import com.yazo.util.ServiceData;
import com.yazo.util.StringUtil;


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
		cmcc = new CmccWebsite();
		cmcc.on_debug = true;
		while(continue_run){
			if (queue.hasElement()){
				SimCommand cmd = queue.get();
				runCommand(cmd);
			} else sleep(1000);
		}
		printDbg("Stopped.");
	}
	
	public void addCommand(SimCommand cmd){
		queue.add(cmd);
	}
	public void runCommand(SimCommand cmd){
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
			cmcc.register();
			printDbg("end register, user_id=" + cmcc.cmcc_user_id);
		} else if(cmd.command.equals("authenticate")){
			printDbg("authenticate:" + cmcc.authenticate());
		}else if(cmd.command.equals("welcome")){
			printDbg("Start Welcome.");
			String s = cmcc.welcome();
			printDbg("end welcome:[" + s.length() + "]");
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
		}
	}
	
	public void doProcessOnCmcc(){
//		String userid = null;
//		if (canceled || userid==null) return;  // regist failed.
//		sleep(5000);
//		if (canceled) return;
//		
//		Boolean auth = authenticate(
//				config.getString(ConfigKeys.CMCC_USER_AGENT), 
//				userid, 
//				config.getString(ConfigKeys.CMCC_CLIENT_PASSWORD), 
//				config.getString(ConfigKeys.CMCC_CHANNEL)
//		);
//		if (canceled || auth!=Boolean.TRUE) return;  // cannot authenticate
//		sleep(5000);
//		if (canceled) return;
//		
//		cmcc.get("getClientWelcomeInfo", "");

		// TODO: 安装服务器传回的访问序列，依次访问免费或收费页面
		//  if !catalogId && contentid, 1)getContentInfo?contentId=xxx 2)getChapterInfo?contentId=xxx&chapterId=xxx
		//  if catalogId && contentid,  1)getCatalogInfo?catalogId=xxx 2)getContentInfo, 3)getChapterInfo
		
		
		// TODO: 模拟扣费章节流程
		//
		//
//				doSubscribeChapter(index);
//				doSubscribeChapterPV(index);

		// 包月目录
		// get subscribeCatalog?catalogId=xxx
		
		// 购买图书
		// get getContentProductInfo?contentId=xxx
		// get subscribeContent?contentId=xxx&productId=xxx
		
		// 购买章节
		// get getContentProductInfo?contentId=xxx&chapterId=xxx
		// get subscribeContent?contentId=xxx&chapterId=xxx&productId=xxx
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
