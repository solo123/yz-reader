package com.yazo.network;

public class ErrorCodes {
	
	public static final int CommandIDNotFound = 2;
	public static final int Error = 1;	
	public static final int SUCCESS = 0;
	
	private static final int UNSUPPORTED_ENCODING=-1001;
	private static final int ARGUMENT_OUT_OF_RANGE = -1002;
	private static final int ARGUMENT_NULL = -1003;
	
	private static final int CONNECT_FAILED = -12001;
	private static final int SEND_FAILED = -12002;
	private static final int RECEIVE_FAILED = -12003;
	public static final int TIMEOUT = -12004;
	private static final int SOCKET_EXCEPTION = -12005;
	
	private static final int PROTOCOL_NOT_FOUND = -15002;
	
	public static AppException protocolNotFound(short commandID){
		return new AppException(PROTOCOL_NOT_FOUND,"Protocol:"+commandID+" not found!");
	}
	
	public static AppException unsupportedEncoding(String encoding){
		return new AppException(UNSUPPORTED_ENCODING,"Encoding:"+encoding+" not support!");
	}
	
	public static AppException connectFailed(String message){
		return new AppException(CONNECT_FAILED,message);
	}
	
	public static AppException timeout(){
		return new AppException(TIMEOUT,"timeout");
	}
	
	public static AppException sendFailed(String message){
		return new AppException(SEND_FAILED,message);
	}
	
	public static AppException receiveFailed(String message){
		return new AppException(RECEIVE_FAILED,message);
	}
	
	public static AppException socketException(String message){
		return new AppException(SOCKET_EXCEPTION,message);
	}
	
	public static AppException ArgumentOutOfRange(String message)
    {
        return new AppException( ARGUMENT_OUT_OF_RANGE, message );
    }
	
	public static AppException ArgumentNULL( String message )
    {
        return new AppException( ARGUMENT_NULL, message );
    }
}
