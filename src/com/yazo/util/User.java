package com.yazo.util;


public class User {

	public static String userId = "";//用户ID  用户唯一标识
	
	public static String regCode = "";//激活码。DRM使用RegCode生成REK（许可证加密密钥），客户端同样根据RegCode生成REK，用于许可证解密〄1�7
	
	public static String userMonthlyType = "";//包月区域类型 2两元包月；5五元包月
}
