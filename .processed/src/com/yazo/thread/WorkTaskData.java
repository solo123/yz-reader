package com.yazo.thread;


class WorkTaskData {
	public WaitCallback Callback;
	
	public Object State;
	
	public WorkTaskData(WaitCallback callback, Object state){
		this.Callback = callback;
		this.State = state;
	}
}
