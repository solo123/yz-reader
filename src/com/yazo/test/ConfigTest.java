package com.yazo.test;

import com.yazo.application.biz.Config;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

public class ConfigTest extends TestCase {
	public ConfigTest() {
		super();
	}
	public ConfigTest(String name, TestMethod method){
		super(name, method);
	}
	public Test suite(){
		TestSuite suite = new TestSuite();
		suite.addTest(
				new ConfigTest("intValTest", new TestMethod()
				{public void run(TestCase tc){
					((ConfigTest) tc).intValTest();}
				})
		);
		suite.addTest(
				new ConfigTest("intStringTest", new TestMethod()
				{public void run(TestCase tc){
					((ConfigTest) tc).intStringTest();}
				})
		);
		suite.addTest(
				new ConfigTest("loadIniIntTest", new TestMethod()
				{public void run(TestCase tc){
					((ConfigTest) tc).loadIniIntTest();}
				})
		);
		suite.addTest(
				new ConfigTest("loadIniStringTest", new TestMethod()
				{public void run(TestCase tc){
					((ConfigTest) tc).loadIniStringTest();}
				})
		);		
		return suite;
	}
	
	private void intValTest(){
		Config config = Config.getInstance();
		config.add(1, 101);
		assertEquals(101, config.getInt(1));
	}
	private void intStringTest(){
		Config config = Config.getInstance();
		config.add(1, "ab100");
		assertEquals("ab100", config.getString(1));
	}
	private void loadIniIntTest(){
		Config config = Config.getInstance();
		config.loadString("\n900=abc\n901=i.586\n902=");
		assertEquals(586, config.getInt(901));
	}
	private void loadIniStringTest(){
		Config config = Config.getInstance();
		config.loadString("\n900=abc\n901=i586\n902=");
		assertEquals("i586", config.getString(901));
	}

}
