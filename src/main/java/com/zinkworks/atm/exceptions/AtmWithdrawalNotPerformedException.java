package com.zinkworks.atm.exceptions;

import org.aspectj.bridge.AbortException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.EXPECTATION_FAILED)
public class AtmWithdrawalNotPerformedException extends AbortException {

	private static final long serialVersionUID = 5412399410218948055L;
	private final int status = HttpStatus.EXPECTATION_FAILED.value();

	public AtmWithdrawalNotPerformedException(String message) {
		super(message);
	}

	public int getStatus() {
		return status;
	}

}
