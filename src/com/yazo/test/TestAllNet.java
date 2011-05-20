package com.yazo.test;

import j2meunit.framework.*;

public class TestAllNet extends TestCase {
	public Test suite()
    {
           TestSuite suite = new TestSuite();
           suite.addTest(new PostDataTest().suite());
           suite.addTest(new ContentTest().suite());
           suite.addTest(new ContentWebTest().suite());
           suite.addTest(new NetworkConnectionTest().suite());
           return suite;
    }
}
