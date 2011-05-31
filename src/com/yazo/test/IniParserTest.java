package com.yazo.test;

import com.yazo.tools.IniParser;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

public class IniParserTest extends TestCase {
	public IniParserTest() {
		super();
	}
	public IniParserTest(String name, TestMethod method){
		super(name, method);
	}
	public Test suite(){
		TestSuite suite = new TestSuite();
		suite.addTest(
				new IniParserTest("hasMoreElement", new TestMethod()
				{public void run(TestCase tc){
					((IniParserTest) tc).hasMoreElement();}
				})
		);
		suite.addTest(
				new IniParserTest("hasNoElement", new TestMethod()
				{public void run(TestCase tc){
					((IniParserTest) tc).hasNoElement();}
				})
		);
		suite.addTest(
				new IniParserTest("nextElement", new TestMethod()
				{public void run(TestCase tc){
					((IniParserTest) tc).nextElement();}
				})
		);
		suite.addTest(
				new IniParserTest("nextElementEmpty", new TestMethod()
				{public void run(TestCase tc){
					((IniParserTest) tc).nextElementEmpty();}
				})
		);
		
		suite.addTest(
				new IniParserTest("nnextElement", new TestMethod()
				{public void run(TestCase tc){
					((IniParserTest) tc).nnextElement();}
				})
		);
		suite.addTest(
				new IniParserTest("multiLines", new TestMethod()
				{public void run(TestCase tc){
					((IniParserTest) tc).multiLines();}
				})
		);
		suite.addTest(
				new IniParserTest("crlfTest", new TestMethod()
				{public void run(TestCase tc){
					((IniParserTest) tc).crlfTest();}
				})
		);
		return suite;
	}
	private void hasMoreElement(){
		IniParser ipr = new IniParser("aa=bb\nbb=aa bb cc\ncc= aa \n");
		assertTrue(ipr.hasMoreElements());
	}
	private void hasNoElement(){
		IniParser ipr = new IniParser("");
		assertTrue(!ipr.hasMoreElements());
	}
	private void nextElement(){
		IniParser ipr = new IniParser("aa=bb");
		ipr.next();
		assertEquals("aa", ipr.key);
		assertEquals("bb", ipr.value);
		assertTrue(!ipr.hasMoreElements());
	}
	private void nextElementEmpty(){
		IniParser ipr = new IniParser("aa=bb\n");
		ipr.next();
		assertTrue(!ipr.hasMoreElements());
	}
	private void nnextElement(){
		IniParser ipr = new IniParser("aa=bb\nbb= cc ");
		ipr.next();
		ipr.next();
		assertEquals("bb", ipr.key);
		assertEquals(" cc ", ipr.value);
	}
	private void multiLines(){
		IniParser ipr = new IniParser("aa=bb\nbb= cc \nee=");
		ipr.next();
		assertEquals("aa", ipr.key);
		assertEquals("bb", ipr.value);
		ipr.next();
		assertEquals("bb", ipr.key);
		assertEquals(" cc ", ipr.value);
		ipr.next();
		assertEquals("ee", ipr.key);
		assertEquals("", ipr.value);
	}
	private void crlfTest(){
		IniParser ipr = new IniParser("aa=bb\r\nbb= cc \nee=");
		ipr.next();
		assertEquals("aa", ipr.key);
		assertEquals("bb", ipr.value);
	}

}
