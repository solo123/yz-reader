package com.yazo.CMCC.biz;

public class CommandQueue {
	private SimCommand[] buf;
	private int i,j;
	private final static int buf_size = 20;
	
	public CommandQueue(){
		buf = new SimCommand[buf_size];
		i = j = -1;
		for(int k=0; k<buf_size; k++) buf[k] = null;
	}
	
	public void add(SimCommand cmd){
		synchronized (this) {
			if(++j>=buf_size) j = 0;
			buf[j] = cmd;
		}
	}
	public SimCommand get(){
		int k = -1;
		synchronized (this) {
			if (i!=j) k = ++i;
		}
		return buf[k];
	}
	public boolean hasElement(){
		return i!=j;
	}
	
}
