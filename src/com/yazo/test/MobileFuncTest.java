package com.yazo.test;

import com.yazo.application.biz.MobileInfo;

import j2meunit.framework.*;

public class MobileFuncTest extends TestCase {
	public MobileFuncTest() {
		super();
	}
	public MobileFuncTest(String name, TestMethod method){
		super(name, method);
	}
	public Test suite(){
		TestSuite suite = new TestSuite();
		suite.addTest(
				new MobileFuncTest("Get http page", new TestMethod()
				{public void run(TestCase tc){
					((MobileFuncTest) tc).getMobileInfoTest();}
				})
		);

		return suite;
	}

	
	private void getMobileInfoTest(){
		MobileInfo mb = MobileInfo.getInstance();
		assertNotNull(mb);
		assertNotNull(mb.channel);
		assertNotNull(mb.imei);
		assertNotNull(mb.imsi);
		assertNotNull(mb.interfaceName);
		assertNotNull(mb.smsCenter);
		assertNotNull(mb.version);
		assertTrue(mb.telecomsOperator>0);
		System.out.println("Mobile info:" + mb.toString());
	}

}
