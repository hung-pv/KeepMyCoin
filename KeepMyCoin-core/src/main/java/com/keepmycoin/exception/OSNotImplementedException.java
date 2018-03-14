package com.keepmycoin.exception;

public class OSNotImplementedException extends RuntimeException {
	private static final long serialVersionUID = 6066990020051214985L;

	public OSNotImplementedException(String methodName) {
		super(String.format("Method of '%s' has not been implemented for '%s' OS", methodName, System.getProperty("os.name")));
	}
}
