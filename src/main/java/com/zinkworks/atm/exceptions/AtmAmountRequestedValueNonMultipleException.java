package com.zinkworks.atm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
public class AtmAmountRequestedValueNonMultipleException extends IllegalArgumentException {

	private static final long serialVersionUID = -2968582622128386731L;
	private final int status = HttpStatus.NOT_ACCEPTABLE.value();

	public AtmAmountRequestedValueNonMultipleException(String s) {
		super(s);
	}

	public int getStatus() {
		return status;
	}
}
