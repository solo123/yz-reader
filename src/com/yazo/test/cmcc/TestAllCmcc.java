package com.yazo.test.cmcc;

import j2meunit.framework.*;

public class TestAllCmcc extends TestCase {
	public Test suite()
    {
           TestSuite suite = new TestSuite();
           suite.addTest(new ParseRegisterTest().suite());
           return suite;
    }
}
