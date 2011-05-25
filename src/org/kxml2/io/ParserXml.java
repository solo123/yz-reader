package org.kxml2.io;

import org.xmlpull.v1.XmlPullParser;

import com.yazo.util.User;

public class ParserXml {

	// 登陆和注册解析都调用这个方法，调用后User会有数据，User.userID是必须要得到的，
	public static String getUserInfo(KXmlParser parser) {
		StringBuffer result = new StringBuffer();
		try {
			int eventType = parser.getEventType();
			String strText = "";
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.TEXT) {
					strText = parser.getText();
				} else if (eventType == XmlPullParser.END_TAG) {
					String str = parser.getName().toLowerCase();
					result.append(strText);
					result.append("</" + str + ">");
					if ("userid".equals(str)) {
						User.userId = strText;
					} else if ("userinfo".equals(str)) {
						break;
					}
				} else if (eventType == XmlPullParser.START_TAG) {
					String str = parser.getName().toLowerCase();
					result.append("<" + str + ">");

				}
				eventType = parser.next();
			}
		} catch (Exception e) {
		}
		return result.toString();
	}

	/**
	 * 获得注册和登录返回的信息
	 * <userinfo>userid</userinfo>
	 */
	public static String registerAndLogin(KXmlParser parser) {
		StringBuffer result = new StringBuffer();
		try {
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {

				if (eventType == XmlPullParser.START_TAG) {
					String str = parser.getName().toLowerCase();
					if (!str.equals("userinfo")) {
						result.append("<" + str + ">");
					}
					if (str.equals("userinfo")) {
						String userInfo = getUserInfo(parser);
						result.append(userInfo);
					}
				}
				if (eventType == XmlPullParser.TEXT) {
					result.append(parser.getText());
				}
				if (eventType == XmlPullParser.END_TAG) {
					String str = parser.getName().toLowerCase();
					result.append("</" + str + ">");
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	/**
	 * 用于解析KxmlParser文件的内容，转换成string类型
	 * @param parser
	 * @return
	 */
	public static String getCatalogInfo(KXmlParser parser) {
		StringBuffer result = new StringBuffer();
		try {
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {

				if (eventType == XmlPullParser.START_TAG) {
					String str = parser.getName().toLowerCase();
					result.append("<" + str + ">");
				}
				if (eventType == XmlPullParser.TEXT) {
					result.append(parser.getText());
//					System.out.println(parser.getText());
				}
				if (eventType == XmlPullParser.END_TAG) {
					String str = parser.getName().toLowerCase();
					result.append("</" + str + ">");
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
//		System.out.println(result.toString());
		return result.toString();
	}
	/**
	 * 用于获取专区的类型数组
	 * @param parser
	 * @return
	 */
	public static String[] getCatalogInfoArray(KXmlParser parser) {
		String[] cids = new String[1];
		try {
			int eventType = parser.getEventType();
			String strText = "";
			int count = 0;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.TEXT) {
					strText = parser.getText().trim();
				} else if (eventType == XmlPullParser.END_TAG) {
					String str = parser.getName().toLowerCase();
					if ("catalogid".equals(str)) {
						cids[count] = strText;
						count++;
						if (count == cids.length)
							break;
					}
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return cids;
	}
}
