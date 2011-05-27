package com.yazo.test;

import com.yazo.application.biz.Config;
import com.yazo.application.biz.RmsManager;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

public class RmsTest extends TestCase{
	public RmsTest() {
		super();
	}
	public RmsTest(String name, TestMethod method){
		super(name, method);
	}
	public Test suite(){
		TestSuite suite = new TestSuite();
		suite.addTest(
				new RmsTest("loadFromEmpty", new TestMethod()
				{public void run(TestCase tc){
					((RmsTest) tc).loadFromEmpty();}
				})
		);
		suite.addTest(
				new RmsTest("under100Test", new TestMethod()
				{public void run(TestCase tc){
					((RmsTest) tc).under100Test();}
				})
		);
		suite.addTest(
				new RmsTest("between100_200Test", new TestMethod()
				{public void run(TestCase tc){
					((RmsTest) tc).between100_200Test();}
				})
		);
		return suite;
	}
	
	private void loadFromEmpty(){
		RmsManager mng = RmsManager.getInstance();
		Config config = Config.getInstance();
		config.add(1,101);
		mng.save(config);
		mng.load(config);
		assertEquals(101, config.getInt(1));
	}
	private void under100Test(){
		RmsManager mng = RmsManager.getInstance();
		Config config = Config.getInstance();
		config.add(1,102);
		mng.save(config);
		config.add(1, 101);
		mng.load(config);
		assertEquals(101, config.getInt(1));
	}
	private void between100_200Test(){
		RmsManager mng = RmsManager.getInstance();
		Config config = Config.getInstance();
		config.add(101,"abc");
		mng.save(config);
		config.add(101, "def");
		mng.load(config);
		assertEquals("abc", config.getString(101));
	}
}
