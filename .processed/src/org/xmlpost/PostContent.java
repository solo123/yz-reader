package org.xmlpost;

public class PostContent {

	String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Request></Request>";

	public void addLabel(String upLabel, String label) {
		try {
			String fatherTag = "<" + upLabel + ">";
			int index = str.indexOf(fatherTag) + fatherTag.length();
			String tmp1 = str.substring(0, index);
			String tmp2 = str.substring(index);
			str = tmp1 + "<" + label + ">" + "</" + label + ">" + tmp2;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addContent(String label, String content) {
		try {
			String tag = "<" + label + ">";
			int index = str.indexOf(tag) + tag.length();
			String tmp1 = str.substring(0, index);
			String tmp2 = str.substring(index);
			str = tmp1 + content + tmp2;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getXml() {
		return str;
	}
}
