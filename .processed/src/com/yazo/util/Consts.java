package com.yazo.util;

/**
 * 配置�?
 * 
 */
public class Consts {

	public static String yzchannel = "";// 动�?生成，根�?位主渠道+文件4�?
	public static String channel = "05003001";// 新主渠道�?
	public static String VERSION = "0153";// 版本�?
	public static String HOSTURL = "http://211.140.17.83/cmread/portalapi";//移动服务�?
	public static String strUserAgent = "CMREAD_Javamini_WH_V1.05_100407";// 128标准
	public static String strUserPassword = "";// 阅读客户端密�?
	public static String READ_URL = "http://bk-b.info/vamp/mtkbook/details.aspx?";
	public static String IMG_URL = "http://bk-b.info/resource/books/cover/";
	/***** 系统类型 1暗扣 2提示后扣�?3为不带提�?，点击阅读直接扣�?4扣费前，对话框提示，确定则扣 5前一章免费阅读，从第二章�?��收费 *******/
	public static int SYSTEMTYPE = 1;
	/*** true 中国移动 false联�? ********/
	public static boolean isChinaMobile = true;
	/**
	 * 提交书本的信�?
	 */
	public static String bookType = "";//0:包月�?:购买本书�?:购买本章,3:免费
	public static String bookCatalogId = "";//专区ID
	public static String bookContentId = "";//内容ID
	public static String bookChapterId = "";//章节ID
}
