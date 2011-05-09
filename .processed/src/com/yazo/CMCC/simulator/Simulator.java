package com.yazo.CMCC.simulator;

import org.xmlpost.PostContent;

import com.yazo.contol.Handle;
import com.yazo.rms.RmsManager;
import com.yazo.util.Consts;
import com.yazo.util.HBase64;
import com.yazo.util.MD5;
import com.yazo.util.StringUtil;
import com.yazo.util.User;

public class Simulator {
	private String cmcc_server = "http://bk-b.info/";
	
	public void runSimulator(){
		//Handle.getYaZhuoChannel();
		User.userId = RmsManager.getUserID();
		
		authenticate();
		cmccLogin();
	}

	public void authenticate(){
		String strM = MD5.toMD5(Consts.strUserAgent + User.userId + Consts.strUserPassword)
			.toLowerCase();
		String pp = HBase64.encode(StringUtil.hexStringToByte(strM));
		
		PostContent p = new PostContent();
		p.addLabel("Request", "AuthenticateReq");
		p.addLabel("AuthenticateReq", "clientHash");
		p.addContent("clientHash", pp);
		p.addLabel("AuthenticateReq", "channel");
		p.addContent("channel", Consts.channel);
		System.out.println("SIM/auth:" + p.getXml());
		
		
		
		
	}	
	
	public void cmccLogin(){
		String result = "";
		String strM = MD5.toMD5(Consts.strUserAgent + Consts.strUserPassword)
			.toLowerCase();
		String pp = HBase64.encode(StringUtil.hexStringToByte(strM));
		PostContent p = new PostContent();
		p.addLabel("Request", "RegisterReq");
		p.addLabel("RegisterReq", "clientHash");
		p.addContent("clientHash", pp);
		System.out.println("CMCC login:" + p.getXml());
		
		
	}
	

}
