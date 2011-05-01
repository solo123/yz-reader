package com.yazo.net;

import java.io.InputStream;
import java.io.OutputStream;


public class QueryServerData {
	public InputStream InStream;
	public OutputStream OutStream;
	public int ReceiveBufferSize;
	public int Timeout;
	public Object Input;
	public Object Output;
	public boolean IsCompleted;
}
