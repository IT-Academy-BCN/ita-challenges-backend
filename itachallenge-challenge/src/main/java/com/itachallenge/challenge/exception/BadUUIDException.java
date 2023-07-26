package com.itachallenge.challenge.exception;

public class BadUUIDException extends Exception {

	private static final long serialVersionUID = 1L;
	public BadUUIDException(String msg){
        super(msg);
    }
    public BadUUIDException(){}
}
