package com.yazo.test;

import j2meunit.midletui.TestRunner;

public class MIDletTest extends TestRunner {
    protected void startApp()
    {
           start(new String[] { "com.yazo.test.TestAll" });
    }
//    public static void main(String[] args) { 
//	    String[] runnerArgs = new String[]{"com.yazo.test.TestAll"}; 
//	    j2meunit.textui.TestRunner.main(runnerArgs);
//  } 
}
