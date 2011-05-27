package com.yazo.application.biz;

import com.yazo.model.TelecomsOperator;

public class MobileInfo {
	public String channel, smsCenter, imsi, imei, interfaceName, version, cmcc_userid;
	public int telecomsOperator;
	private static MobileInfo mobileInfo = null;
	
	private MobileInfo(){
		interfaceName = "NZ_FEE_01";// 接口名称，NZ_FEE_01
		channel = ""; // 渠道号，与基地合作分配的渠道号
		smsCenter = getCNETERNUMBER();
		imsi = getIMSI();
		imei = getIMEI();
		telecomsOperator = getOperator(smsCenter);
		if (smsCenter == null || smsCenter.equals("")) {
			smsCenter = "6";
		}
		version = "";
		cmcc_userid = "";
		//TODO: cmcc_userid = get from RMS 
	}
	public static MobileInfo getInstance(){
		if (mobileInfo==null) mobileInfo = new MobileInfo();
		return mobileInfo;
	}
	
	public String toString(){
		return "Mobile info----" +
			"\nchannel:" + channel +
			"\nsmsCenter:" + smsCenter +
			"\nimsi:" + imsi +
			"\nimei:" + imei +
			"\ninterface:" + interfaceName +
			"\nversion:" + version +
			"\nOperator:" + telecomsOperator + "\n";
	}
	
	/**
	 * IMSI (International Mobile Subscriber Identity) Example IMSI (O2 UK):
	 * 234103530089555 String mcc = imsi.substring(0,3); // 234 (UK) String mnc =
	 * imsi.substring(3,5); // 10 (O2)
	 * 
	 * @return
	 */
	public static String getIMSI() {

		String out = "";

		try {

			out = System.getProperty("IMSI");
			if (out == null || out.equals("null") || out.equals(""))
				out = System.getProperty("phone.imsi");
			if (out == null || out.equals("null") || out.equals(""))
				out = System.getProperty("com.nokia.mid.imsi");
			if (out == null || out.equals("null") || out.equals(""))
				out = System.getProperty("com.nokia.mid.mobinfo.IMSI");

			if (out == null || out.equals("null") || out.equals(""))
				out = System.getProperty("com.sonyericsson.imsi");

			if (out == null || out.equals("null") || out.equals(""))
				out = System.getProperty("IMSI");

			if (out == null || out.equals("null") || out.equals(""))
				out = System.getProperty("com.samsung.imei");

			if (out == null || out.equals("null") || out.equals(""))
				out = System.getProperty("com.siemens.imei");
			if (out == null || out.equals("null") || out.equals(""))
				out = System.getProperty("imsi");

		} catch (Exception e) {

			return out == null ? "" : out;

		} catch (Error e) {

		}

		return out == null ? "" : out;
	}
	/**
	 * 
	 * get the IMEI (International Mobile Equipment Identity (IMEI)) in the
	 * phone
	 * 
	 * @return
	 */
	public String getIMEI() {
		String out = "";
		try {
			out = System.getProperty("com.imei");
			if (out == null || out.equals("null") || out.equals(""))
				out = System.getProperty("phone.imei");
			if (out == null || out.equals("null") || out.equals(""))
				out = System.getProperty("com.nokia.IMEI");
			if (out == null || out.equals("null") || out.equals(""))
				out = System.getProperty("com.nokia.mid.imei");
			if (out == null || out.equals("null") || out.equals(""))
				out = System.getProperty("com.nokia.mid.imei");
			if (out == null || out.equals("null") || out.equals(""))
				out = System.getProperty("com.sonyericsson.imei");
			if (out == null || out.equals("null") || out.equals(""))
				out = System.getProperty("IMEI");
			if (out == null || out.equals("null") || out.equals(""))
				out = System.getProperty("com.motorola.IMEI");
			if (out == null || out.equals("null") || out.equals(""))
				out = System.getProperty("com.samsung.imei");
			if (out == null || out.equals("null") || out.equals(""))
				out = System.getProperty("com.siemens.imei");
			if (out == null || out.equals("null") || out.equals(""))
				out = System.getProperty("imei");
		} catch (Exception e) {

			return out == null ? "" : out;
		} catch (Error e) {

		}
		return out == null ? "" : out;
	}

	public String getCNETERNUMBER() {
		String str = "";
		try {
			str = System.getProperty("wireless.messaging.sms.smsc");
		} catch (Exception e) {

		} catch (Error e) {

		}
		return str == null ? "" : str;
	}
    public int getOperator(String center){
		if (center != null && !center.equals("")) {
			center = center.substring(6, center.length() - 3);
			if (center.substring(0, 2).equals("00"))
				return TelecomsOperator.CMCC;  //移动，否则都算联通
		}
		return TelecomsOperator.CU;
    }
}
