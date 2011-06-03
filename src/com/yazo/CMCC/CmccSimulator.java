package com.yazo.CMCC;


import java.util.Vector;


import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Form;


import com.yazo.CMCC.biz.RegisterParser;
import com.yazo.application.biz.Config;
import com.yazo.model.ConfigKeys;

import com.yazo.util.HBase64;
import com.yazo.util.MD5;
import com.yazo.util.ServiceData;
import com.yazo.util.StringUtil;


public class CmccSimulator {
	public static ServiceData serviceData;
	static Vector progressIDFree = null;
	static Vector progressIDCharge = null;
	public static int total = 0;
	public static int tempInt = 0;
	private Config config = Config.getInstance();
	
	private CmccWebSite cmcc;
	private boolean canceled = false;
	private Form form = null;
	
	private 
	
	

	public void setForm(Form form){
		this.form = form;
	}
	public void doTest(){
//		config.add(ConfigKeys.YZ_CLIENT_ID, 100);
//		config.add(ConfigKeys.CMCC_USER_AGENT, "CMREAD_JavaLS_V1.50_101221");
//		config.add(ConfigKeys.CMCC_SERVICE, "http://211.140.17.83/cmread/portalapi");
//		config.add(ConfigKeys.CMCC_CLIENT_PASSWORD, "12101017");
//		config.add(ConfigKeys.CONTENT_SERVER, "http://bk-b.info");
//		config.add(ConfigKeys.CMCC_DEBUG, 1);

		cmcc = new CmccWebSite();
		cmcc.cmcc_service_url = "http://211.140.17.83/cmread/portalapi";
		cmcc.cmcc_user_agent = "CMREAD_JavaLS_V1.50_101221";
		cmcc.cmcc_agent_password = "12101017";
		cmcc.on_debug = true;
		cmcc.client_id = 100;
		cmcc.log_service = "http://bk-b.info/reader/logs/addop";
		cmcc.use_cmcc_proxy = false;
		
		
		String test_no = config.getString(ConfigKeys.TEST_FUNCS);
		if (test_no.indexOf("[register]")!= -1){
			doRegister();
		}
		if (test_no.indexOf("[switch proxy]")!= -1){
			cmcc.use_cmcc_proxy = !cmcc.use_cmcc_proxy;
			printDbg("proxy:" + cmcc.use_cmcc_proxy);
		}
		if (test_no.indexOf("[welcome]")!=-1){
			doWelcome();
		}
	}
	public void doProcessOnCmcc(){
		String userid = null;
		if (canceled || userid==null) return;  // regist failed.
		sleep(5000);
		if (canceled) return;
		
		Boolean auth = authenticate(
				config.getString(ConfigKeys.CMCC_USER_AGENT), 
				userid, 
				config.getString(ConfigKeys.CMCC_CLIENT_PASSWORD), 
				config.getString(ConfigKeys.CMCC_CHANNEL)
		);
		if (canceled || auth!=Boolean.TRUE) return;  // cannot authenticate
		sleep(5000);
		if (canceled) return;
		
		cmcc.get("getClientWelcomeInfo", "");

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
	
	
	public void doRegister(){
		printDbg("Start register.");
		String userid = register(
					config.getString(ConfigKeys.CMCC_USER_AGENT),
					config.getString(ConfigKeys.CMCC_CLIENT_PASSWORD)
		);
		config.add(ConfigKeys.CMCC_USER_ID, userid);
		printDbg("end register, user_id=" + userid);
	}
	
	public void doWelcome(){
		int client_id = config.getInt(ConfigKeys.YZ_CLIENT_ID);
		if (client_id<=0) return;
		printDbg("Start Welcome.");
		byte[] s = cmcc.post("getClientWelcomeInfo", "");
		if (cmcc.http_status==200 && (s==null || s.length<1)) s = cmcc.post("getClientWelcomeInfo", "");
		printDbg("end welcome:[" + s + "]");
	}
	
	/*
	 * config: CMCC_USER_AGENT, CMCC_CLIENT_PASSWORD
	 * return userid
	 */
	public String register(){
		String strM = MD5.toMD5(cmccagent+password).toLowerCase();
		String pp = HBase64.encode(StringUtil.hexStringToByte(strM));
		String xml = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<Request>" + 
				"<RegisterReq>" +
					"<clientHash>" + pp + "</clientHash>" +
				"</RegisterReq>"+
			"</Request>";
		
		byte[] response_text = cmcc.post("register", xml);
		if (cmcc.http_status==200 && (response_text==null || response_text.length<1)) 
			response_text = cmcc.post("register", xml);
		RegisterParser rp = new RegisterParser();
		String user_id = rp.parse(response_text);
		return user_id;
	}
	
	public Boolean authenticate(String agent, String userid, String password, String channel) {
		String strM = MD5.toMD5(agent+userid+password).toLowerCase();
		String pp = HBase64.encode(StringUtil.hexStringToByte(strM));
		String xml = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<Request>" + 
				"<AuthenticateReq>" +
					"<clientHash>" + pp + "</clientHash>" +
					"<channel>" + channel + "</channel>" +
				"</AuthenticateReq>"+
			"</Request>";

		cmcc.post(null, "authenticate2", xml);
		if (cmcc.http_status == HttpConnection.HTTP_OK) return Boolean.TRUE;
		else return Boolean.FALSE;
	}

	private void sleep(int millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
	private void printDbg(String message){
		System.out.println("[DBG]" + message);
		if (form!=null)
			form.append(message);
	}

}
