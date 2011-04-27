package com.yazo.util;

/**
 * 配置类
 * 
 */
public class Consts {

	public static String cmchannel = "";// 动态生成，根据8位主渠道+文件4位

//	public static String channel = "05001015";// 主渠道号
	public static String channel = "05003001";// 新主渠道号

	public static String VERSION = "0153";// 版本号

	public static String HOSTURL = "http://211.140.17.83/cmread/portalapi";//移动服务端
	
	public static String strUserAgent = "CMREAD_Javamini_WH_V1.05_100407";// 128标准
	
	public static String strUserPassword = "";// 阅读客户端密码
	
	public static int orderState = 10;// 订购 的标记

	public static String READ_URL = "http://bk-b.info/vamp/mtkbook/details.aspx?";

	public static String IMG_URL = "http://bk-b.info/resource/books/cover/";

	/***** 系统类型 1暗扣 2提示后扣费 3为不带提示 ，点击阅读直接扣费 4扣费前，对话框提示，确定则扣 5前一章免费阅读，从第二章开始收费 *******/
	public static int SYSTEMTYPE = 1;
	
	/*** true 中国移动 false联通 ********/
	public static boolean isChinaMobile = true;



}
