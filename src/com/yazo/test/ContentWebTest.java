package com.yazo.test;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

import com.yazo.contents.ContentService;
import com.yazo.tools.mobile.MobileSysData;
import com.yazo.model.ServiceData;


public class ContentWebTest extends TestCase {

	public ContentWebTest() {
		super();
	}
	public ContentWebTest(String name, TestMethod method){
		super(name, method);
	}

	public Test suite(){
		TestSuite suite = new TestSuite();
		suite.addTest(
				new ContentWebTest("Get http page", new TestMethod()
				{public void run(TestCase tc){
					((ContentWebTest) tc).loginToContentServerTest();}
				})
		);

		return suite;
	}

	private void loginToContentServerTest(){
		ContentService cs = new ContentService("http://bk-b.info/reader/");
		MobileSysData mb = MobileSysData.getInstance();
		ServiceData data = cs.login(mb);
		assertNotNull(data.BUSINESS);
	}
	
}
