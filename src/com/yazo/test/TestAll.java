package com.yazo.test;

import j2meunit.framework.*;

public class TestAll extends TestCase
{
       public Test suite()
       {
              TestSuite suite = new TestSuite();
              suite.addTest(new MyTest().suite());
              suite.addTest(new MobileFuncTest().suite());
              suite.addTest(new KVPTest().suite());
              suite.addTest(new ConfigTest().suite());
              suite.addTest(new RmsTest().suite());
//              suite.addTest(new TestAllNet().suite());
              return suite;
       }


}
