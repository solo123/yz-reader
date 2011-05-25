package com.yazo.application.biz;

import com.yazo.contents.LinkContent;
import com.yazo.contents.PageContent;
import java.io.InputStream;
import javax.microedition.lcdui.Font;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

public class PageContentParser {
	private int width, height, line_height;
	private Font font;
	public PageContentParser(int width, int height, int line_height, Font font){
		this.width = width;
		this.height = height;
		this.line_height = line_height;
		this.font = font;
	}

	public Object parse(InputStream iStream) {
		PageContent c = null;
		try {
			KXmlParser parser = new KXmlParser();
			c = new PageContent(width, height, line_height, font);
			parser.setInput(iStream, "utf-8");
			parser.nextTag();
			parser.require(XmlPullParser.START_TAG, null, "page");
			while (parser.nextTag()!=XmlPullParser.END_TAG){
				parser.require(XmlPullParser.START_TAG, null, null);
				String name = parser.getName();
				if (name.equals("header")){
					c.header = parser.nextText();
				} else if (name.equals("content")){
					while(parser.nextTag()!=XmlPullParser.END_TAG){
						parser.require(XmlPullParser.START_TAG, null, null);
						String nn = parser.getName();
						if (nn.equals("link")){
							String arrow, text, desc, aurl;
							arrow = text = desc = aurl = null;
							while(parser.nextTag()!=XmlPullParser.END_TAG){
								parser.require(XmlPullParser.START_TAG, null, null);
								String n = parser.getName();
								String t = parser.nextText();
								if (n.equals("arrow"))	arrow = t;
								else if (n.equals("text")) text = t;
								else if (n.equals("desc")) desc = t;
								else if (n.equals("url")) aurl = t;
								parser.require(XmlPullParser.END_TAG, null, n);
							}
							c.addLink(arrow, text, desc, aurl);
						}else if(nn.equals("text")){
							String t = parser.nextText();
							c.addText(t);
						}
						parser.require(XmlPullParser.END_TAG, null, nn);
					}
				} else if (name.equals("menu")){
					while(parser.nextTag()!=XmlPullParser.END_TAG){
						parser.require(XmlPullParser.START_TAG, null,null);
						String nn = parser.getName();
						if (nn.equals("link")){
							String text=null, aurl=null;
							while(parser.nextTag()!=XmlPullParser.END_TAG){
								parser.require(XmlPullParser.START_TAG, null, null);
								String n = parser.getName();
								String t = parser.nextText();
								if (n.equals("text")) text = t;
								else if (n.equals("url")) aurl = t;
								parser.require(XmlPullParser.END_TAG, null, n);
							}
							c.menus.addElement(new LinkContent(text, aurl));
						}
					}
				} else if (name.equals("rightkey")){
					String text=null, aurl=null;
					while(parser.nextTag()!=XmlPullParser.END_TAG){
						parser.require(XmlPullParser.START_TAG, null, null);
						String n = parser.getName();
						String t = parser.nextText();
						if (n.equals("text")) text = t;
						else if (n.equals("url")) aurl = t;
						parser.require(XmlPullParser.END_TAG, null, n);
					}
					c.rightKeyMenu = new LinkContent(text, aurl);
				}
				parser.require(XmlPullParser.END_TAG, null, name);
			}
			parser.require(XmlPullParser.END_TAG, null, "page");
			c.load_from_cache = false;
		} catch (Exception e) {
			e.printStackTrace();
			c = null;
		}
		return c;
	}

}
