package com.yazo.application;

public class Configuration {
	public static String content_server = "http://bk-b.info/reader/pages/";  // 内容服务器
	public static String cmcc_server = "http://211.140.17.83/"; // 移动服务器（浙江）
	public static String content_home = "home";
	
	public static String channel = "05001015";//主渠道号
	public static String cmchannel = "";//动态生成，根据8位主渠道+文件4位
	public static String VERSION = "0153";//版本号
	
	public static String strUserAgent = "CMREAD_Javamini_WH_V1.05_100407";// 128标准
	public static String strUserPassword = "";// 阅读客户端密码
	public static int orderState = 10;// 订购状态 0只有激活 1订购一次 2订购2次
	public static StringBuffer test=new StringBuffer();
	public final static String HASBUY="已订购";
	public static boolean isNextRead = true;//是否可以继续阅读
	public static boolean hasChanges=false;
	/*****系统类型 1暗扣 2提示后扣费 3为不带提示 ，点击阅读直接扣费   4扣费前，对话框提示，确定则扣  5前一章免费阅读，从第二章开始收费*******/
	public  static int SYSTEMTYPE=1;//
	/***true 中国移动    false联通********/
	public static boolean isChinaMobile=true;
	public static String CHARGES_ALERT_MESSAGE="激活5元,终身免费，需发5条确认信息";
	/************MM游戏下载临时用的常量**************/
	public static String LOCATION_PATH="";
	public static String WAPALERT="";
}
