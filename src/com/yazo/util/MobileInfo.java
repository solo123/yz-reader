package com.yazo.util;

public class MobileInfo {
	
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
	public static String getIMEI() {
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

	public static String getCNETERNUMBER() {
		String str = "";
		try {
			str = System.getProperty("wireless.messaging.sms.smsc");
		} catch (Exception e) {

		} catch (Error e) {

		}
		return str == null ? "" : str;
	}

}