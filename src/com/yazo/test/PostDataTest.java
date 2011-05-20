package com.yazo.test;

import com.yazo.application.biz.BookBiz;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

public class PostDataTest extends TestCase {
	public PostDataTest() {
		super();
	}
	public PostDataTest(String name, TestMethod method){
		super(name, method);
	}

	public Test suite(){
		TestSuite suite = new TestSuite();
		suite.addTest(
				new PostDataTest("loginTest", new TestMethod()
				{public void run(TestCase tc){
					((PostDataTest) tc).loginTest();}
				})
		);
		return suite;
	}
	
	private void loginTest(){
		BookBiz bp = new BookBiz();
		String r = bp.doLogin();
		assertNotNull(r);
		System.out.println("Login:" + r);
	}
}
