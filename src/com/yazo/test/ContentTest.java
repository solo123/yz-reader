package com.yazo.test;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

import com.yazo.application.biz.BookBiz;
import com.yazo.contents.PageContent;

public class ContentTest extends TestCase  {
	public ContentTest() {
		super();
	}
	public ContentTest(String name, TestMethod method){
		super(name, method);
	}
	public Test suite(){
		TestSuite suite = new TestSuite();
		suite.addTest(
				new ContentTest("getTextContent", new TestMethod()
				{public void run(TestCase tc){
					((ContentTest) tc).getTextContent();}
				})
		);
		
		return suite;
		
	}

	private void getTextContent(){
		BookBiz bp = new BookBiz();
		PageContent pc = bp.getPageContentFromUrl("http://bk-b.info/reader/pages/", "home");
		assertNotNull(pc);
	}
}
