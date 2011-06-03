package com.yazo.test.cmcc;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

import com.yazo.CMCC.biz.RegisterParser;

public class ParseRegisterTest extends TestCase {
	public ParseRegisterTest() {
		super();
	}
	public ParseRegisterTest(String name, TestMethod method){
		super(name, method);
	}
	public Test suite(){
		TestSuite suite = new TestSuite();
		suite.addTest(
				new ParseRegisterTest("getUserid", new TestMethod()
				{public void run(TestCase tc){
					((ParseRegisterTest) tc).getUserid();}
				})
		);	
		return suite;
	}
	
	private void getUserid(){
		String response = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
			"<Response>" +
				"<RegisterRsp>" +
					"<UserInfo>" +
						"<userID>123abc</userID>" +
					"</UserInfo>" +
				"</RegisterRsp>" +
			"</Response>";
		RegisterParser rp = new RegisterParser();
		String userid = rp.parse(response.getBytes());
		assertEquals("123abc", userid);
	}
	
}
