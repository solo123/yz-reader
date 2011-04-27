package com.yazo.util;

public class ServiceData {
	/**操作激活	0：无操作1：SMS计费2：IVR计费	3：游戏URL计费4：阅读URL计费5：视频URL计费***/
	public String OPERATE;
	/**业务名称	如：原创音乐**/
	public String SERVICE;
	/*******业务资费	如：100，以分为单位*****/
	public String FEECODE;
	/******阅读客户端激活URL******/
	public String MSG1;
	/******阅读客户端版本号******/
	public String MSG2;
	/*******当操作码为6时表示：阅读客户端密码*****/
	public String MSG3;
	/******阅读客户端渠道号******/
	public String MSG4;
	/*****手机阅读显示开关（0开1关）*******/
	public String MSG5;
	
}
