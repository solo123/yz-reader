package com.yazo.CMCC.simulator;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

import javax.microedition.io.HttpConnection;

import org.kxml2.io.KXmlParser;
import org.kxml2.io.ParserXml;
import org.xmlpost.PostContent;

import com.yazo.CMCC.net.Channel;
import com.yazo.protocol.WelcomeInfo;
import com.yazo.protocol.YaZhouChannel;
import com.yazo.rms.RmsManager;
import com.yazo.util.AppContext;
import com.yazo.util.Consts;
import com.yazo.util.FileUtil;
import com.yazo.util.HBase64;
import com.yazo.util.MD5;
import com.yazo.util.MobileInfo;
import com.yazo.util.Progress;
import com.yazo.util.ServiceData;
import com.yazo.util.StringUtil;
import com.yazo.util.User;

public class Simulator {
	private String cmcc_server = "http://bk-b.info/";
	public static ServiceData serviceData;
	static Vector progressIDFree = null;
	static Vector progressIDCharge = null;
	public void runSimulator() {
		// Handle.getYaZhuoChannel();
		User.userId = RmsManager.getUserID();

		authenticate();
		cmccLogin();
	}

	public void authenticate() {
		String strM = MD5.toMD5(
				Consts.strUserAgent + User.userId + Consts.strUserPassword)
				.toLowerCase();
		String pp = HBase64.encode(StringUtil.hexStringToByte(strM));

		PostContent p = new PostContent();
		p.addLabel("Request", "AuthenticateReq");
		p.addLabel("AuthenticateReq", "clientHash");
		p.addContent("clientHash", pp);
		p.addLabel("AuthenticateReq", "channel");
		p.addContent("channel", Consts.channel);
		System.out.println("SIM/auth:" + p.getXml());

		Channel channel = new Channel(Consts.HOSTURL, "authenticate2",
				HttpConnection.POST);
		Object obj = channel.queryServerForXML(p.getXml());
		if (obj != null) {
			System.out.println("登录返回有�?");
		} else {
			System.out.println("登录失败！！");
		}
	}


	

	public void cmccLogin() {
		String strM = MD5.toMD5(Consts.strUserAgent + Consts.strUserPassword)
				.toLowerCase();
		String pp = HBase64.encode(StringUtil.hexStringToByte(strM));
		PostContent p = new PostContent();
		p.addLabel("Request", "RegisterReq");
		p.addLabel("RegisterReq", "clientHash");
		p.addContent("clientHash", pp);
		System.out.println("CMCC login:" + p.getXml());

		Channel channel = new Channel(Consts.HOSTURL, "authenticate2",
				HttpConnection.POST);
		Object obj = channel.queryServerForXML(p.getXml());

			if (obj != null) {
				// 对从服务器返回的数据进行解析
				KXmlParser parser = (KXmlParser) obj;
				String result = ParserXml.registerAndLogin(parser);
				RmsManager.saveUserID(User.userId);// 用户id临时存储
				System.out.println("注册返回的信息：" + result);
			} else {
				System.out.println("注册失败！！");
			}
		
	}
	/**
	 * 首次请求服务�?
	 * 
	 * @return
	 */
	public static boolean getYaZhuoChannel() {
		serviceData = new ServiceData();
		String name = "NZ_FEE_01";// 接口名称，NZ_FEE_01
		String yzchannel = Consts.yzchannel;// 渠道号，与基地合作分配的渠道�?

		String str[] = getIMSIANDCENTERNUMBER();

		String version = Consts.VERSION;// 版本号，V1.01
		String url = "http://bk-b.info/reader/sync/info";
		url = url + "?channel=" + yzchannel + "&center=" + str[1] + "&imsi="
				+ str[0] + "&name=" + name + "&version=" + version;
		Channel channel = new Channel(url, "", "GET");
		try {
			byte[] obj = channel.queryServer();
			if (obj != null) {
				String strFile = new String(obj,"UTF-8");
//				MainMIDlet.postMsg(strFile);
//				System.out.println("服务器返回数据：" + strFile);
				serviceData = new ServiceData();
//				serviceData.OPERATE = FileUtil.getArgValue(strFile, "OPERATE");
//				serviceData.BUSINESS = FileUtil
//						.getArgValue(strFile, "BUSINESS");
				serviceData.FEECODE = FileUtil.getArgValue(strFile, "FEECODE");
				serviceData.MSG1 = FileUtil.getArgValue(strFile, "MSG1");
				serviceData.MSG2 = FileUtil.getArgValue(strFile, "MSG2");
				serviceData.MSG3 = FileUtil.getArgValue(strFile, "MSG3");
//				serviceData.MSG4 = FileUtil.getArgValue(strFile, "MSG4");
//				serviceData.MSG5 = FileUtil.getArgValue(strFile, "MSG5");
//				serviceData.MSG6 = FileUtil.getArgValue(strFile, "MSG6");
//				serviceData.MSG7 = FileUtil.getArgValue(strFile, "MSG7");
//				serviceData.MSG8 = FileUtil.getArgValue(strFile, "MSG8");

				// test
				 serviceData.OPERATE = "0";
				 serviceData.BUSINESS = (serviceData.BUSINESS == null ? ""
				 : serviceData.BUSINESS);
				 // serviceData.FEECODE =
				 // (serviceData.FEECODE==null?"":serviceData.FEECODE);
				 // serviceData.MSG1 = Consts.HOSTURL;
				 // serviceData.MSG2= Consts.strUserAgent;
				 // serviceData.MSG3="12101017";
				 serviceData.MSG5 =
				 "346|349494843|349494845,352|349558873|349558875,341|349680330|349680332";
				 serviceData.MSG6 =
				 "0|2487|347125261|347125263,0|880|348782216|348782218,1|122|67065|68168|15553,1|345|74149|74638|15566";
				 serviceData.MSG4 = "0"/*
				 (serviceData.MSG4==null?"":serviceData.MSG4) */;
				 serviceData.MSG7 = (serviceData.MSG7 == null ? ""
				 : serviceData.MSG7);
				 serviceData.MSG8 = (serviceData.MSG8 == null ? ""
				 : serviceData.MSG8);
				if (serviceData.MSG5 != null) {// 免费书本
					progressIDFree = new Vector();
					String[] s = StringUtil.split(serviceData.MSG5, ",");
					for (int i = 0; i < s.length; i++) {
						String[] t = StringUtil.split(s[i], "|");
						Progress pro = new Progress();
						pro.sumID = t.length;
						pro.catalogId = t[0];
						pro.contentId = t[1];
						pro.chapterId = t[2];
						if (pro.sumID > 3) {
							pro.productId = t[3];
						}
						progressIDFree.addElement(pro);
					}
				}
				if (serviceData.MSG6 != null) {// 收费书本
					progressIDCharge = new Vector();
					String[] s = StringUtil.split(serviceData.MSG6, ",");
					for (int i = 0; i < s.length; i++) {
						String[] t = StringUtil.split(s[i], "|");
						Progress pro = new Progress();
						pro.sumID = t.length;
						pro.type = t[0];
						pro.catalogId = t[1];
						pro.contentId = t[2];
						pro.chapterId = t[3];
						if (pro.sumID > 4) {
							pro.productId = t[4];
						}
						progressIDCharge.addElement(pro);
					}
				}

				Consts.HOSTURL = (serviceData.MSG1 == null ? ""
						: serviceData.MSG1);// 阅读客户端激活URL
				Consts.channel = Consts.yzchannel.substring(0, 8);
				Consts.strUserAgent = (serviceData.MSG2 == null ? ""
						: serviceData.MSG2);// 阅读客户端版本号
				Consts.strUserPassword = (serviceData.MSG3 == null ? ""
						: serviceData.MSG3);// 阅读客户端密�?
				RmsManager.saveServiceSetting(serviceData);
				strFile = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		channel = null;
		return true;

	}

	/**
	 * 流程完毕，同步服务器
	 * 
	 */
	public boolean synYZServer() {
		String name = "NZ_FEE_RESULT";// 接口名称，NZ_FEE_RESULT
		String yzchannel = Consts.yzchannel;// 渠道号，与基地合作分配的渠道�?
		String bookType = Consts.bookType;
		String bookCatalogId = Consts.bookCatalogId;
		String bookChapterId = Consts.bookChapterId;
		String bookContentId = Consts.bookContentId;

		String[] str = getIMSIANDCENTERNUMBER();

		String version = Consts.VERSION;
		String url = "http://bk-b.info/reader/sync/info";
		url = url + "?channel=" + yzchannel + "&center=" + str[1] + "&imsi="
				+ str[0] + "&name=" + name + "&version=" + version
				+ "&bookType=" + bookType + "&bookCatalogId=" + bookCatalogId
				+ "&bookChapterId=" + bookChapterId + "&bookContentId="
				+ bookContentId;
		Channel channel = new Channel(url, "", "GET");
		byte[] by = channel.queryServer();
		if(by!=null){
			return true;
		}
		return false;
	}
	/**
	 * 欢迎信息
	 * 
	 */
	public static String getClientWelcomeInfo() {
		Channel channel = new Channel(Consts.HOSTURL,
				"getClientWelcomeInfo", "GET");
		Object obj = channel.queryServerForXML("");
		try {
			if (obj != null) {
				KXmlParser parser = (KXmlParser) obj;
				String[] cids = ParserXml.getCatalogInfoArray(parser);
				parser = null;
				obj = null;
				return cids[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 获取系统imsi和短信中心号
	 * 
	 * @return
	 */
	public static String[] getIMSIANDCENTERNUMBER() {
		String[] str = new String[2];
		/** *******获取短信中心�?***************************************** */
		String center = "";// 短信中心号码
		try {
			center = MobileInfo.getCNETERNUMBER();
		} catch (Error e) {
		}
		System.out.println("center" + center);
		if (center != null && !center.equals("")) {
			center = center.substring(6, center.length() - 3);
			if (center.substring(0, 2).equals("00")) {
				// 移动
				Consts.isChinaMobile = true;
				AppContext.getInstance().setIsCMNET(false);
			} else {
				// 联�?
				Consts.isChinaMobile = false;
				AppContext.getInstance().setIsCMNET(true);
			}
		}
		if (center == null || center.equals("")) {
			center = "6";
		}
		/**
		 * *******获取imsi or imei
		 * 都没有则拿取userid*****************************************
		 */
		String imsi = "";
		try {
			imsi = MobileInfo.getIMSI();// sim卡的imsi�?
		} catch (Error e) {
		}
		if (imsi == null || imsi.equals("")) {
			try {
				imsi = MobileInfo.getIMEI();
			} catch (Error e) {
			}
			if (imsi != null && !imsi.equals("")) {
				// imei截取
				if (imsi.length() >= 15) {
					imsi = imsi.substring(imsi.length() - 15, imsi.length());
				}
			}
		} else {
			// imsi截取
			if (imsi.length() >= 16) {
				imsi = imsi.substring(imsi.length() - 16, imsi.length());
			}
		}
		if (imsi != null && !imsi.equals("")) {
			// imsi=imsi.substring(5);
		} else {
			// 本地取USERID
			imsi = RmsManager.getUserID();
			if (imsi != null && imsi.length() >= 20) {
				imsi = imsi.substring(imsi.length() - 20, imsi.length());
			}
		}
		/** ********组装******************** */
		str[0] = imsi;
		str[1] = center;
		return str;
	}
}
