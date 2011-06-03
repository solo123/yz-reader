package com.yazo.test;

import com.yazo.util.HBase64;
import com.yazo.util.MD5;
import com.yazo.util.StringUtil;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

public class UtilTest extends TestCase{
	public UtilTest() {
		super();
	}
	public UtilTest(String name, TestMethod method){
		super(name, method);
	}

	public Test suite(){
		TestSuite suite = new TestSuite();
		suite.addTest(
				new UtilTest("md5base64test", new TestMethod()
				{public void run(TestCase tc){
					((UtilTest) tc).md5base64test();}
				})
		);

		return suite;
	}
	private void md5base64test(){
		String strM = MD5.toMD5("CMREAD_JavaLS_V1.50_101221" + "12101017").toLowerCase();
		String pp = HBase64.encode(StringUtil.hexStringToByte(strM));
		assertEquals("ad2hLFlTIYNszKNCeR48YQ==", pp);
	}
}
