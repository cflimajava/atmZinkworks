package com.zinkworks.atm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.PRECONDITION_REQUIRED)
public class AtmNotNotesEnoughException extends IllegalArgumentException{

	private static final long serialVersionUID = -8426567488610835476L;
	private final int status = HttpStatus.PRECONDITION_REQUIRED.value();
	
	public AtmNotNotesEnoughException(String message) {
		super(message);
	}

	public int getStatus() {
		return status;
	}

}
