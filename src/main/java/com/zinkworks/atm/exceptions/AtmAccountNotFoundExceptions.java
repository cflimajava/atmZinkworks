package com.zinkworks.atm.exceptions;

import javax.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class AtmAccountNotFoundExceptions extends EntityNotFoundException{

	private static final long serialVersionUID = 1L;
	private final int status = HttpStatus.NOT_FOUND.value();
		
	public AtmAccountNotFoundExceptions(String message) {
		super(message);
	}
	
	public int getStatus() {
		return status;
	}

}
