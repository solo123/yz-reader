package com.yazo.application.thread;

import com.yazo.CMCC.CmccSimulator;

public class ThreadJobCmccSimulator extends ThreadJob {
	public void run(){
		CmccSimulator sim = new CmccSimulator();
		sim.doRegister();
	}
}
