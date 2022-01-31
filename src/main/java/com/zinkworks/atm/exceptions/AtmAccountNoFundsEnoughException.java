package com.zinkworks.atm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.PRECONDITION_FAILED)
public class AtmAccountNoFundsEnoughException extends IllegalArgumentException {

	private static final long serialVersionUID = -2850710451783978727L;
	private final int status = HttpStatus.PRECONDITION_FAILED.value();

	public AtmAccountNoFundsEnoughException(String message) {
		super(message);
	}

	public int getStatus() {
		return status;
	}
}
