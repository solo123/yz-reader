package com.yazo.test;

import com.yazo.CMCC.CmccSimulator;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

public class CmccTest extends TestCase {
	public CmccTest() {
		super();
	}
	public CmccTest(String name, TestMethod method){
		super(name, method);
	}

	public Test suite(){
		TestSuite suite = new TestSuite();
		suite.addTest(
				new CmccTest("registerTest", new TestMethod()
				{public void run(TestCase tc){
					((CmccTest) tc).registerTest();}
				})
		);
		return suite;
	}
	
	private void registerTest(){
		// load config
		
		
		
		// run register
		CmccSimulator cmcc = new CmccSimulator();
		//String userid = cmcc.register();
		//assertNotNull(userid);
	}
	private void authenticateTest(){
		// load config
		// config.userid,strUserAgent,strUserPassword,channel

		
		//CmccSimulator cmcc = new CmccSimulator();
		//assertTrue(cmcc.authenticate());
	}
	private void getWelcomeTest(){
		//CmccSimulator cmcc = new CmccSimulator();
		//assertNotNull(cmcc.getWelcomeInfo());
	}

}
