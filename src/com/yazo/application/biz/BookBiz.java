package com.yazo.application.biz;

import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Font;
import com.yazo.contents.PageContent;
import com.yazo.model.ConfigKeys;
import com.yazo.network.HttpConnect;

public class BookBiz {
	private Config config = Config.getInstance();
	public  int network_connect_status = 0; 
	public PageContent getPageContentFromUrl(String service, String action) {
		String url = service+action;
		PageContent c = null;
		HttpConnect conn = new HttpConnect();
		conn.open(url);
		if (conn.status == HttpConnection.HTTP_OK){
			PageContentParser parser = new PageContentParser(
					config.getInt(ConfigKeys.SCREEN_WIDTH),
					config.getInt(ConfigKeys.SCREEN_WIDTH),
					config.getInt(ConfigKeys.LINE_HEIGHT),
					(Font)config.getObject(ConfigKeys.DEFAULT_FONT));
			Object r = parser.parse(conn.inStream);
			if (r!=null) c = (PageContent)r;
		}
		conn.setNoProxy();
		conn.open(url);
		
		if (c!=null){
			c.service = service;
			c.action = action;
			c.load_from_cache = false;
		}
		network_connect_status = conn.status;
		conn.close();
		conn = null;
		return c;
	}
	
	public String doLogin(){
		MobileInfo mb = MobileInfo.getInstance();
		HttpConnect conn = new HttpConnect();
		conn.setNoProxy();
		String data = "channel="+mb.channel+
			"&sms_center=" + mb.smsCenter +
			"&imsi=" + mb.imsi +
			"&imei=" + mb.imei +
			"&interface_name="+ mb.interfaceName +
			"&version=" + mb.version +
			"&cmcc_userid=" + mb.cmcc_userid;
		conn.post(config.getString(ConfigKeys.CONTENT_SERVER) + "/reader/sync/login", data);
		String r = conn.getContent();
		if (conn.status==200)
			System.out.println("login ok");
		System.out.println("login:" + r);
		
		conn.close();
		conn = null;
		return r;
	}
}
