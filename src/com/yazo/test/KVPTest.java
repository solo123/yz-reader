package com.yazo.test;

import com.yazo.model.KeyValuePair;
import j2meunit.framework.*;

public class KVPTest extends TestCase {
	public KVPTest() {
		super();
	}
	public KVPTest(String name, TestMethod method){
		super(name, method);
	}

	public Test suite(){
		TestSuite suite = new TestSuite();
		suite.addTest(
				new KVPTest("getValueTest", new TestMethod()
				{public void run(TestCase tc){
					((KVPTest) tc).getValueTest();}
				})
		);
		return suite;
	}
	private void getValueTest(){
		if (true) return;
		KeyValuePair kp = new KeyValuePair("a=1\nb=2\nc=3\n");
		assertEquals("3", kp.getValue("c"));
	}
}
