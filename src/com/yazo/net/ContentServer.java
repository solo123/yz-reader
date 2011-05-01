package com.yazo.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import org.xmlpull.v1.XmlPullParserException;

import com.yazo.application.Browser;
import com.yazo.books.LineContent;
import com.yazo.net.QueryServerData;
import com.yazo.thread.ThreadPool;
import com.yazo.thread.WaitCallback;

public class ContentServer {
	private HttpConnection hc;
	private OutputStream out;
	private InputStream in;
	byte[] dataArray = null;
	private String mystate = "init...";
	QueryServerData data = new QueryServerData();
	
	public void getWebContent(String url, Object controler){
		MultiThreadDataObject data = new MultiThreadDataObject(url, controler);
		WaitCallback callback = new WaitCallback() {
			public void execute(Object data) {
				MultiThreadDataObject tobj = (MultiThreadDataObject)data;
				Browser browser = (Browser)tobj.controler;
				LineContent lc = null;
				try {
					lc = browser.book_manager.getWebPage(tobj.url);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (lc!=null) browser.onLineContentUpdated(lc);
			}
		};
		
		try {
			ThreadPool.queueWorkItem(callback, data);
		} catch (Exception e) {
			System.out.println("waitTimeOut error:" + e.getMessage());
		}
	}
	
	public void connect(int timeout) throws Exception {
		WaitCallback callback = new WaitCallback() {
			public void execute(Object state) {
				for(int i=0; i<10; i++){
					System.out.println("Thread execute:" + i);
					QueryServerData qsd = (QueryServerData) state;
					qsd.Timeout = i;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("Thread execute END.");
			}
		};
		
		try {
			ThreadPool.queueWorkItem(callback, data);
		} catch (Exception e) {
			System.out.println("waitTimeOut error:" + e.getMessage());
		}
		//waitTimeout(timeout, callback, data);
		System.out.println("end of connect.");
	}
	public void printState(){
		System.out.println("State=" + data.Timeout);
	}

	private void waitTimeout(int timeout, WaitCallback callback, QueryServerData data) {
		try {
			ThreadPool.queueWorkItem(callback, data);
		} catch (Exception e) {
			System.out.println("waitTimeOut error:" + e.getMessage());
		}

		int timeElapsed = 0;
		while (timeElapsed < timeout && !data.IsCompleted) {
			try {
				Thread.sleep(100);
				timeElapsed += 100;
				System.out.print("time:" + timeElapsed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (timeElapsed >= timeout) {
			close();
			System.gc();
		}
	}
	
	public void close() {
		try {
			if (hc != null) {
				hc.close();
				hc = null;
			}
			if (out != null) {
				out.close();
				out = null;
			}
			if (in != null) {
				in.close();
				in = null;
			}
			dataArray = null;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
