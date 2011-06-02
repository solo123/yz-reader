package com.yazo.CMCC;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Form;

import org.kxml2.io.KXmlParser;

import com.yazo.application.biz.Config;
import com.yazo.model.ConfigKeys;
import com.yazo.util.Consts;
import com.yazo.util.GZIP;
import com.yazo.util.HBase64;
import com.yazo.util.MD5;
import com.yazo.util.ServiceData;
import com.yazo.util.StringUtil;
import com.yazo.util.User;

public class CmccSimulator {
	public static ServiceData serviceData;
	static Vector progressIDFree = null;
	static Vector progressIDCharge = null;
	public static int total = 0;
	public static int tempInt = 0;
	private Config config = Config.getInstance();
	
	private CmccWebSite website;
	private boolean canceled = false;
	private Form form = null;
	
	private String cookie = "";

	public void setForm(Form form){
		this.form = form;
	}
	public void doTest(){
		config.add(ConfigKeys.YZ_CLIENT_ID, 100);
		config.add(ConfigKeys.CMCC_USER_AGENT, "CMREAD_Javamini_WH_V1.05_100407");
		config.add(ConfigKeys.CMCC_SERVICE, "http://211.140.17.83/cmread/portalapi");
		config.add(ConfigKeys.CMCC_CLIENT_PASSWORD, "12101017");
		config.add(ConfigKeys.CONTENT_SERVER, "http://bk-b.info");
		config.add(ConfigKeys.CMCC_DEBUG, 1);

		String test_no = config.getString(ConfigKeys.TEST_FUNCS);
		if (test_no.indexOf("[register]")!= -1){
			doRegister();
		}
		if (test_no.indexOf("[register1]")!= -1){
			doRegister1();
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
		
		website.get("getClientWelcomeInfo", "");

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
		int client_id = config.getInt(ConfigKeys.YZ_CLIENT_ID);
		if (client_id<=0) return;
		website = new CmccWebSite(
				config.getString(ConfigKeys.CMCC_USER_AGENT),
				config.getString(ConfigKeys.CMCC_SERVICE),
				false
		);
		String userid = config.getString(ConfigKeys.CMCC_USER_ID);
		if (userid==null || userid.length()<3){
			userid = register(
					config.getString(ConfigKeys.CMCC_USER_AGENT),
					config.getString(ConfigKeys.CMCC_CLIENT_PASSWORD)
			);
		}
	}
	public void doRegister1(){
		printDbg("Start register by cmwap.");
		int client_id = config.getInt(ConfigKeys.YZ_CLIENT_ID);
		if (client_id<=0) return;
		website = new CmccWebSite(
				config.getString(ConfigKeys.CMCC_USER_AGENT),
				config.getString(ConfigKeys.CMCC_SERVICE),
				true
		);
		String userid = config.getString(ConfigKeys.CMCC_USER_ID);
		if (userid==null || userid.length()<3){
			userid = register(
					config.getString(ConfigKeys.CMCC_USER_AGENT),
					config.getString(ConfigKeys.CMCC_CLIENT_PASSWORD)
			);
		}
	}
	
	public void doWelcome(){
		int client_id = config.getInt(ConfigKeys.YZ_CLIENT_ID);
		if (client_id<=0) return;
		printDbg("Start Welcome...");
		website = new CmccWebSite(
				config.getString(ConfigKeys.CMCC_USER_AGENT),
				config.getString(ConfigKeys.CMCC_SERVICE),
				true
		);
		String s = website.post("getClientWelcomeInfo", "");
		if (website.http_status==200 && (s==null || s.length()<1)) s = website.post("getClientWelcomeInfo", "");
		printDbg("end welcome:[" + s + "]");
	}
	
	/*
	 * config: CMCC_USER_AGENT, CMCC_CLIENT_PASSWORD
	 * return userid
	 */
	public String register(String agent, String password){
		printDbg("start register...");
		String strM = MD5.toMD5(agent+password).toLowerCase();
		String pp = HBase64.encode(StringUtil.hexStringToByte(strM));
		String xml = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<Request>" + 
				"<RegisterReq>" +
					"<clientHash>" + pp + "</clientHash>" +
				"</RegisterReq>"+
			"</Request>";
		
		String s = null;
		try{
			printDbg("begin...");
			HttpConnection hc = (HttpConnection)Connector.open("http://10.0.0.172:80"
					+ "/cmread/portalapi", Connector.READ_WRITE, true);
			hc.setRequestProperty("X-Online-Host", "211.140.17.83");
			hc.setRequestMethod(HttpConnection.POST);
			hc.setRequestProperty("Accept-Charset", "UTF-8");
			hc.setRequestProperty("Accept", "*/*");
			hc.setRequestProperty("Client-Agent", "CMREAD_Javamini_WH_V1.05_100407");
			hc.setRequestProperty("x-up-calling-line-id", "13400981944");
			hc.setRequestProperty("user-id", "");
			hc.setRequestProperty("APIVersion", "1.1.0");
			hc.setRequestProperty("Cookie", cookie);
			hc.setRequestProperty("Encoding-Type", "gzip");// gzip
			hc.setRequestProperty("Action", "register"); //
			hc.setRequestProperty("ClientHash", "");
			hc.setRequestProperty("Version", "CMREAD_Javamini_WH_V1.05_100407");
			hc.setRequestProperty("Content-Length", "" + xml.length());
			OutputStream ostream = hc.openOutputStream();
			ostream.write(xml.getBytes("UTF-8"));
			printDbg("writed data.");
			
			String cEncoding = hc.getHeaderField("Encoding-Type");
			String resultCode = hc.getHeaderField("Result-Code");
			resultCode = (resultCode == null ? "0" : resultCode);
			String cLength = hc.getHeaderField("Content-Length");
			int length = Integer.parseInt(cLength.trim());
			String cook = hc.getHeaderField("Set-Cookie");
			cookie = cook == null ? cookie : cook;
			String regCode = hc.getHeaderField("RegCode");
			if (resultCode.equals("4003")) {
				Consts.HOSTURL = hc.getHeaderField("Request-URL");
			}
			printDbg("geted header: resultCode="+resultCode+", cookie=["+cook+"], length="+cLength +", regCode="+regCode);
			InputStream istream = hc.openInputStream();
			printDbg("opened input stream.");
			
			if (length > 0) {
				byte[] totalData = new byte[length];
				int actual = 0;
				int bytesread = 0;
				while ((bytesread != length) && (actual != -1)) {
					actual = istream.read(totalData, bytesread, length - bytesread);
					bytesread += actual;
				}
				s = new String(totalData);
			}
			
			if (hc!=null){
				hc.close();
				hc = null;
			}
			if (ostream!=null){
				ostream.close();
				ostream = null;
			}
			if(istream!=null){
				istream.close();
				istream = null;
			}
		} catch (Exception e){
			e.printStackTrace();
			printDbg("error!");
		}
		
		
		
		
		
		//Object o = website.post(new CmccRegisterParser(), "register", xml);
		//if (o==null) return null;
		//else return (String)o;
		
//		String s = website.post("register", xml);
//		if (website.http_status==200 && (s==null || s.length()<1)) s = website.post("register", xml);
//		printDbg("end register:[" + s + "]");
		printDbg("res:[" + s + "]");
		return s;
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

		website.post(null, "authenticate2", xml);
		if (website.http_status == HttpConnection.HTTP_OK) return Boolean.TRUE;
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
