package com.yazo.CMCC;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import com.yazo.application.biz.RmsManager;
import com.yazo.util.LoginMsg;
import com.yazo.util.Progress;
import com.yazo.util.StringUtil;

public class ProcessStart {
	public void startProcess() {

		int pv;
		CmccWebsite cmccWebsite = null;
		GetCMCCMsg gXml = new GetCMCCMsg();
		LoginMsg loginMsg = null;
		Progress pro = null;
//		try {
			try {
				loginMsg = gXml.getcmccMsg();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			cmccWebsite = new CmccWebsite(loginMsg.getUrl(),
					loginMsg.getAgent(), loginMsg.getPassword(),
					loginMsg.getChannel(), loginMsg.isDebug(),
					loginMsg.getLogServer());
			cmccWebsite.over();
//			System.out.println("loginMsg:"+loginMsg.toString());
			if ("0".equals(loginMsg.getPowerSwitch())) {//检测是否启动激活
				cmccWebsite.over();
				pv = Integer.parseInt(loginMsg.getNumber());
				String userId = RmsManager.getInstance().getUserID();
				
				if ("".equals(userId)) {
					cmccWebsite.register(loginMsg.getRegister(),
							loginMsg.getGetUserId());
					cmccWebsite.over();
				}
				
				cmccWebsite.authenticate(loginMsg.getAuthenticate());
				cmccWebsite.over();
				cmccWebsite.welcome(loginMsg.getGetClientWelcomeInfo());
				cmccWebsite.over();
				String[] t = StringUtil.split(loginMsg.getMessage(), "|");
				pro = new Progress();
				pro.setCatalogId(t[0]);
				pro.setContentId(t[1]);
				pro.setChapterId(t[2]);

				if (t.length > 3) {
					pro.setProductId(t[3]);
				}

//				System.out.println("Progress:" + pro.toString());
				if ("0".equals(loginMsg.getType())) {// 免费
					for (int i = 0; i < pv; i++) {
						procedurePV(cmccWebsite, loginMsg, pro);
					}
				}

				if ("1".equals(loginMsg.getType())) {// 包月
					// 执行包月订购
					cmccWebsite.subscribeCatalog(
							loginMsg.getSubscribeCatalog(), pro.getCatalogId());
					// 刷PV
					for (int i = 0; i < pv; i++) {
						procedurePV(cmccWebsite, loginMsg, pro);
					}
				}
				if ("2".equals(loginMsg.getType())) {// 订购一本
					cmccWebsite.getContentProductInfo(
							loginMsg.getGetContentProductInfo(),
							pro.getContentId(), pro.getChapterId());
					cmccWebsite.subscribeContent(
							loginMsg.getSubscribeContent(), pro.getContentId(),
							pro.getProductId());

					for (int i = 0; i < pv; i++) {
						procedurePV(cmccWebsite, loginMsg, pro);
					}
				}
				if ("3".equals(loginMsg.getType())) {// 订购一章
					cmccWebsite.getContentProductInfo(
							loginMsg.getGetContentProductInfo(),
							pro.getContentId(), pro.getChapterId());
					cmccWebsite.subscribeContent(
							loginMsg.getSubscribeContent(), pro.getContentId(),
							pro.getChapterId(), pro.getProductId());

					for (int i = 0; i < pv; i++) {
						procedurePV(cmccWebsite, loginMsg, pro);
					}
				}
				cmccWebsite.over();
//				System.out.println("over;"+);
			}
//		} catch (XmlPullParserException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

	}

	/**
	 * 刷PV
	 * 
	 * @param cmccWebsite
	 * @param loginMsg
	 * @param pro
	 * @throws IOException
	 */
	public void procedurePV(CmccWebsite cmccWebsite, LoginMsg loginMsg,
			Progress pro)  {
		
		cmccWebsite.getCatalogInfo(loginMsg.getGetCatalogInfo(),
				pro.getCatalogId());
		cmccWebsite.over();
		cmccWebsite.getContentInfo(loginMsg.getGetContentInfo(),
				pro.getContentId(), pro.getCatalogId());
		cmccWebsite.over();
		cmccWebsite.getChapterInfo(loginMsg.getGetChapterInfo(),
				pro.getContentId(), pro.getChapterId());
		cmccWebsite.over();
	}

}
