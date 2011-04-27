package com.yazo.protocol;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import com.yazo.books.LineContent;
import com.yazo.net.Connect;

public class RequestBookInfo extends Connect{

	public RequestBookInfo(String url,String action,String methed){
		
		super(url,action,methed);
	}
	/**
	 * 获取章节内容
	 * @return
	 */
	public KXmlParser getBookChapterContext(){
	
			KXmlParser output=null;
			try {
				output = (KXmlParser) this.queryServerForXML("");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return output;
	}
	
	public LineContent getRequestInfo() throws Exception{
		KXmlParser parser = new KXmlParser();
		LineContent c = new LineContent(0);
//		InputStream in=rbi.queryServerForStream();
		parser.setInput(queryServerForStream(), "utf-8");
//		parser.setInput(conn.openInputStream(), "utf-8");
		parser.nextTag();
		parser.require(XmlPullParser.START_TAG, null, "page");
		while (parser.nextTag() != XmlPullParser.END_TAG) {
			parser.require(XmlPullParser.START_TAG, null, null);
			String name = parser.getName();
			if (name.equals("header")) {
				c.header = parser.nextText();
				System.out.println("---"+c.header);
			} else if (name.equals("content")) {
				while (parser.nextTag() != XmlPullParser.END_TAG) {
					parser.require(XmlPullParser.START_TAG, null, null);
					String nn = parser.getName();
					if (nn.equals("link")) {
						String arrow, text, desc, url;
						arrow = text = desc = url = null;
						while (parser.nextTag() != XmlPullParser.END_TAG) {
							parser.require(XmlPullParser.START_TAG, null, null);
							String n = parser.getName();
							String t = parser.nextText();
							if (n.equals("arrow"))
								arrow = t;
							else if (n.equals("text"))
								text = t;
							else if (n.equals("desc"))
								desc = t;
							else if (n.equals("url"))
								url = t;
							parser.require(XmlPullParser.END_TAG, null, n);
						}
						c.addLink(arrow, text, desc, url);
					} else if (nn.equals("text")) {
						
						String test = parser.nextText();
						System.out.println("0.0.0."+test);
						c.addText(test);
					}
					parser.require(XmlPullParser.END_TAG, null, nn);
				}
			}
			parser.require(XmlPullParser.END_TAG, null, name);
		}
		parser.require(XmlPullParser.END_TAG, null, "page");
		return c;
	}

}
