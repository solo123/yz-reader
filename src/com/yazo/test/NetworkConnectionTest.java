package com.yazo.test;

import j2meunit.framework.*;

import javax.microedition.io.HttpConnection;


import com.yazo.network.HttpConnect;

public class NetworkConnectionTest extends TestCase {

	public NetworkConnectionTest() {
		super();
	}
	public NetworkConnectionTest(String name, TestMethod method){
		super(name, method);
	}

	public Test suite(){
		TestSuite suite = new TestSuite();
		suite.addTest(
				new NetworkConnectionTest("getHttpPage", new TestMethod()
				{public void run(TestCase tc){
					((NetworkConnectionTest) tc).getHttpPage();}
				})
		);
		suite.addTest(
				new NetworkConnectionTest("getTextContent", new TestMethod()
				{public void run(TestCase tc){
					((NetworkConnectionTest) tc).getTextContent();}
				})
		);
		suite.addTest(
				new NetworkConnectionTest("getHttpRecdirect", new TestMethod()
				{public void run(TestCase tc){
					((NetworkConnectionTest) tc).getHttpRecdirect();}
				})
		);
		suite.addTest(
				new NetworkConnectionTest("getWrongUrl", new TestMethod()
				{public void run(TestCase tc){
					((NetworkConnectionTest) tc).getWrongUrl();}
				})
		);	
		suite.addTest(
				new NetworkConnectionTest("getWrongUrlStatus", new TestMethod()
				{public void run(TestCase tc){
					((NetworkConnectionTest) tc).getWrongUrlStatus();}
				})
		);	
	
		suite.addTest(
				new NetworkConnectionTest("getWapByProxy", new TestMethod()
				{public void run(TestCase tc){
					((NetworkConnectionTest) tc).getWapByProxy();}
				})
		);	
		return suite;
		
	}

	private void getHttpPage(){
		HttpConnect conn = new HttpConnect("", "", "GET");
		conn.setNoProxy();
		String[] header = {
				"Accept-Charset", "UTF-8",
				"Content-Type", "application/xml",
				"Accept", "*/*"
				};
		conn.setHttpHeader(header);
		conn.open("http://bk-b.info/reader/pages/home");
		System.out.println("Status="+conn.status);
		assertEquals(conn.status, HttpConnection.HTTP_OK);
		if (conn.status == HttpConnection.HTTP_OK){
			byte[] s = conn.getContent();
			System.out.println("Content length:[" + s.length + "]");
			assertTrue(s!=null && s.length>0);
		}
		conn.close();
		conn = null;
	}
	private void getTextContent(){
		HttpConnect conn = new HttpConnect();
		String result = conn.getTextFromUrl("http://bk-b.info/reader/pages/home", false);
		conn.close();
		conn = null;
		
		assertNotNull(result);
		if (result!=null) assertTrue(result.length()>0);
	}
	private void getHttpRecdirect(){
		HttpConnect conn = new HttpConnect();
		String result = conn.getTextFromUrl("http://211.140.17.83/", false);
		assertNull(result);
		assertTrue(conn.status>=300 && conn.status<400);
		conn.close();
		conn = null;
	}
	private void getWrongUrl(){
		HttpConnect conn = new HttpConnect();
		String result = conn.getTextFromUrl("http://some.wrong.url/", false);
		assertNull(result);
		conn.close();
		conn = null;
	}
	private void getWrongUrlStatus(){
		HttpConnect conn = new HttpConnect();
		conn.getTextFromUrl("http://some.wrong.url/", false);
		assertTrue(conn.status!=200);
		conn.close();
		conn = null;
	}	

	private void getWapByProxy(){
		HttpConnect conn = new HttpConnect();
		String result = conn.getTextFromUrl("http://wap.baidu.com", true);
		assertEquals(HttpConnection.HTTP_OK, conn.status);
		assertNotNull(result);
		conn.close();
		conn = null;		
	}


	
}
