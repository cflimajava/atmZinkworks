package com.zinkworks.atm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class InvalidPinException extends SecurityException {

	private static final long serialVersionUID = -7050431129730438846L;
	private final int status = HttpStatus.UNAUTHORIZED.value();

	public InvalidPinException(String message) {
		super(message);
	}

	public int getStatus() {
		return status;
	}

}
