package com.itachallenge.challenge.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ErrorResponseMessage {
    private int statusCode;
    private String message;

    public ErrorResponseMessage(int statusCode, String message) {
        this.message = message;
        this.statusCode = statusCode;
    }

	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}



}
