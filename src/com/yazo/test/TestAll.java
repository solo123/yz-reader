package com.yazo.test;

import j2meunit.framework.*;

public class TestAll extends TestCase
{
       public Test suite()
       {
              TestSuite suite = new TestSuite();
              suite.addTest(new MyTest().suite());
              suite.addTest(new NetworkConnectionTest().suite());
              suite.addTest(new MobileFuncTest().suite());
              suite.addTest(new ContentWebTest().suite());
              return suite;
       }


}
