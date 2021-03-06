package com.yazo.test;

import com.yazo.test.cmcc.TestAllCmcc;

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
              suite.addTest(new IniParserTest().suite());
              suite.addTest(new UtilTest().suite());
              suite.addTest(new ContentBizTest().suite());
              suite.addTest(new TestAllCmcc().suite());
//              suite.addTest(new TestAllNet().suite());
              return suite;
       }


}
