package com.yazo.books;

import java.io.*;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.kxml2.io.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class BookManager {
	public String header;
	public LineContent content;
	public int line_chars;
	
	public BookManager(){
		header = null;
		content = null;
		line_chars = 0;
	}
	public int getPage(String page_name){
		System.out.println("getPage:" + page_name);
		try {
			content = getWebPage(page_name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (content!=null) {
			header = content.header;
			return 1;
		}
		return 0;
	}
	public LineContent getWebPage(String page_name) throws IOException, XmlPullParserException{
		HttpConnection conn = (HttpConnection)Connector.open  ("http://bk-b.info/reader/pages/" + page_name, Connector.READ );
		conn.setRequestProperty("Accept-Charset", "UTF-8");
		KXmlParser parser = new KXmlParser();
		LineContent c = new LineContent(line_chars);
		parser.setInput(conn.openInputStream(), "utf-8");
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
						String arrow, text, desc, url;
						arrow = text = desc = url = null;
						while(parser.nextTag()!=XmlPullParser.END_TAG){
							parser.require(XmlPullParser.START_TAG, null, null);
							String n = parser.getName();
							String t = parser.nextText();
							if (n.equals("arrow"))	arrow = t;
							else if (n.equals("text")) text = t;
							else if (n.equals("desc")) desc = t;
							else if (n.equals("url")) url = t;
							parser.require(XmlPullParser.END_TAG, null, n);
						}
						c.addLink(arrow, text, desc, url);
					}else if(nn.equals("text")){
						c.addText(parser.nextText());
					}
					parser.require(XmlPullParser.END_TAG, null, nn);
				}
			}
			parser.require(XmlPullParser.END_TAG, null, name);
		}
		parser.require(XmlPullParser.END_TAG, null, "page");
		return c;
	}
	public LineContent getPageContent(String page_name) throws IOException{
		String xml = null;
		if (page_name.equals("home")){
			xml = "<page>\n<header>Reader书城</header>\n<content>" +
				"<link><text>1. 浏览历史</text><desc>图书浏览记录</desc><url>history</url></link>\n" +
				"<link><text>2. 热门推荐</text><desc>热门或高评价</desc><url>history</url></link>\n" +
				"<link><arrow>1</arrow><text>命运记事本</text><desc>100%已读</desc><url>bookid=123</url></link>" +
				"<link><arrow>1</arrow><text>十全十美的闪婚</text><desc>100%已读</desc><url>bookid=123</url></link>" +
				"<link><arrow>1</arrow><text>斗破苍穹</text><desc>100%已读</desc><url>bookid=123</url></link>" +
				"<text>玄幻玄幻《奇客》世界上有两件东西能够深深抓住你的心，从此你的命运就开始改写...\n按*键阅读详情\n</text>" +
				"</content></page>";
			System.out.println(xml);
		} else if (page_name.equals("history")){
			xml = "<page>\n<header>我的浏览历史</header>\n<content>" +
				"<link><arrow>1</arrow><text>命运记事本</text><desc>100%已读</desc><url>bookid=123</url></link>\n" +
				"<link><arrow>1</arrow><text>十全十美的闪婚</text><desc>100%已读</desc><url>bookid=123</url></link>\n" +
				"<link><arrow>1</arrow><text>斗破苍穹</text><desc>100%已读</desc><url>bookid=123</url></link>\n" +
				"</content></page>";
		} else if (page_name.startsWith("bookid=")){
			xml = "<page>\n<header>图书信息</header>\n<content>" +
				"<link><text>1. 阅读</text><url>/book/1</url></link>\n" +
				"<link><text>2. 目录</text><url>/book/1/catalog</url></link>" +
				"<text>作者：林薇宣</text>" +
				"<text>分类：都市生活</text>" +
				"<text>每天写字成了习惯，两千字，说起来不难，可是坚持下来还真是不容易。已经十一万字了，还要多少完稿呢？估计十万吧。</text>" +
				"<text>我的小说《十全十美的闪婚》http://www.qdwenxue.com/Book/1755718.aspx这是链接，有空大家看看。内容介绍：我被男友甩掉，一怒之下要选在还有两个月就到的10月10号结婚，可问题是去哪里找个自己喜欢的人完成这个任务呢？我在不停地相亲，网聊中找寻着那个属于我的另一半。如果您有什么好的建议，可要跟我提起啊。</text>" +
				"</content></page>";
		} else if (page_name.startsWith("/book/1/catalog")){
			xml = "<page>\n<header>图书目录</header>\n<content>" +
				"<link><text>《命运记事本》</text><url>abc</url></link>" +
				"<link><arrow>1</arrow><text>第一章 天上掉下一个笔记本</text><url>content</url></link>\n" +
				"</content></page>";
		} else if (page_name.startsWith("/book/1?chapter=") || page_name.equals("content")){
			xml = "<page>\n<header>命运记事本</header>\n<content>" +
				"<text>这个手机是我和文浩热恋的时候买的，都是移动的号，并且配的是情侣号，这样我们之间打电话就不用收费了。刚开始，我们总有很多话要说，每天都不知道要打多少次电话，发多少次短信，5元的短信包不够用，后来改成10元的，总之每天都是甜蜜的，哪怕看到夕阳西下时的一抹云彩很漂亮，都要发个信息告诉他。他也是有很多话要说，他会告诉我，早上刷牙用了几分钟，见不到我吃饭味道不好，想吃我烧的家乡菜。以前文浩不吃辣的，可是自从做了我的男友之后，对辣椒就来者不拒了。我当时还笑话他，对美女不要来者不拒，不然我就不要你啦。</text>" +
				"<text>和郑文浩恋爱从大学快毕业的时候就开始了，也可能内心里因为他的家在这里，我才决定爱上文浩的吧。我没有听从爸妈的意思，而是坚决要留在这里，找了一份文员的工作，理由就是省城机会比较多，我的家在湖南的农村，不想回家。我没有提及和文浩的交往，是想等我们有空回家的时候再说也不迟，两年之间我只在春节的时候才回家的，而这个时候文浩得陪他的父母，我没有勉强过他，所以他不主动说要去我家，我也没有提过这事，没有想到最终连提都不用提了。</text>" +
				"</content></page>";
		}
		if (xml==null) return null;
		InputStream stream = new ByteArrayInputStream(xml.getBytes());
		InputStreamReader reader = new InputStreamReader(stream);
		KXmlParser parser = new KXmlParser();
		LineContent c = new LineContent(line_chars);
		try {
			parser.setInput(reader);
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
							String arrow, text, desc, url;
							arrow = text = desc = url = null;
							while(parser.nextTag()!=XmlPullParser.END_TAG){
								parser.require(XmlPullParser.START_TAG, null, null);
								String n = parser.getName();
								String t = parser.nextText();
								if (n.equals("arrow"))	arrow = t;
								else if (n.equals("text")) text = t;
								else if (n.equals("desc")) desc = t;
								else if (n.equals("url")) url = t;
								parser.require(XmlPullParser.END_TAG, null, n);
							}
							c.addLink(arrow, text, desc, url);
						}else if(nn.equals("text")){
							c.addText(parser.nextText());
						}
						parser.require(XmlPullParser.END_TAG, null, nn);
					}
				}
				parser.require(XmlPullParser.END_TAG, null, name);
			}
			parser.require(XmlPullParser.END_TAG, null, "page");
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

		return c;
	}

	
}
