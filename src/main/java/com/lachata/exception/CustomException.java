package com.lachata.exception;
/* Useless */
public class CustomException extends RuntimeException{
	private final String errCode;
	private final String errMessage;

	public CustomException(String errCode, String errMessage) {
		super(errMessage);
		this.errCode = errCode;
		this.errMessage = errMessage;
	}

	public String getMessage() {
		return "code: " + errCode + "msg : " + errMessage;
	}
}
