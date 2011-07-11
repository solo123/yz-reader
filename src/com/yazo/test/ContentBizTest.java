package com.yazo.test;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

import com.yazo.application.biz.BookBiz;
import com.yazo.contents.LinkContent;
import com.yazo.contents.PageContent;

public class ContentBizTest extends TestCase  {
	public ContentBizTest() {
		super();
	}
	public ContentBizTest(String name, TestMethod method){
		super(name, method);
	}
	public Test suite(){
		TestSuite suite = new TestSuite();
		suite.addTest(
				new ContentBizTest("linkShortKey", new TestMethod()
				{public void run(TestCase tc){
					((ContentBizTest) tc).linkShortKey();}
				})
		);		
		suite.addTest(
				new ContentBizTest("linkNoShortKey", new TestMethod()
				{public void run(TestCase tc){
					((ContentBizTest) tc).linkNoShortKey();}
				})
		);	
		return suite;
		
	}

	private void linkShortKey(){
		LinkContent lnk = new LinkContent("2. abc", "abc");
		assertEquals(2, lnk.short_key);
	}
	private void linkNoShortKey(){
		LinkContent lnk = new LinkContent("abc", "abc");
		assertEquals(-1, lnk.short_key);
	}

}
