package com.yazo.test;

import j2meunit.framework.*;

public class MyTest extends TestCase {
	public MyTest() {
		super();
	}
	public MyTest(String name, TestMethod method){
		super(name, method);
	}


	public Test suite(){
		TestSuite suite = new TestSuite();
		suite.addTest(
				new MyTest("testOne", new TestMethod(){
					public void run(TestCase tc) {
						((MyTest) tc).testOne();
					}
				})
		);
		suite.addTest(
				new MyTest("testTwo", new TestMethod(){
					public void run(TestCase tc) {
						((MyTest) tc).testTwo();
					}
				})
		);
		return suite;
	}


	private void testOne() {
		System.out.println("testOne");
		assertEquals(1, 1);
	}

	private void testTwo() {
		System.out.println("testTwo");
		assertEquals(2, 2);
	}

}
