package com.yazo.tools;

public class CallbackData {
	public ThreadCallback callback_object;
	public String command;
	public Object data1, data2, data3, data4;
	public int state;
	
	public CallbackData(){
		command = null;
		callback_object = null;
		data1 = data2 = data3 = data4 = null;
		state = 0;
	}
}
