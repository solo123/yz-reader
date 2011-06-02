package com.yazo.application.thread;

import javax.microedition.lcdui.Form;

import com.yazo.CMCC.CmccSimulator;

public class ThreadJobCmccSimulator extends ThreadJob {
	public Form form = null;
	public ThreadJobCmccSimulator(Form form){
		this.form = form;
	}
	public void run(){
		CmccSimulator sim = new CmccSimulator();
		sim.setForm(form);
		sim.doTest();
	}
}
