package com.yazo.model;

public class MobileData {
	public String channel, smsCenter, imsi, imei, interfaceName, version;
	public int telecomsOperator;
	
	public MobileData(){
		channel = smsCenter = imsi = imei = interfaceName = version = null;
		telecomsOperator = 0;
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
	
}
