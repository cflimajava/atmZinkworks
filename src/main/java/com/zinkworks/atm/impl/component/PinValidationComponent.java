package com.zinkworks.atm.impl.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zinkworks.atm.dao.AccountDAO;
import com.zinkworks.atm.entities.Account;
import com.zinkworks.atm.exceptions.InvalidPinException;
import com.zinkworks.atm.interfaces.components.IPinValidationComponent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PinValidationComponent implements IPinValidationComponent{
	
	private final AccountDAO accountDAO;	
	
	@Autowired
	public PinValidationComponent(AccountDAO accountDAO) {
		this.accountDAO = accountDAO;
	}

	@Override
	public void validatePin(String accountNumber, Integer pin) {
		
		Account account = accountDAO.getAccountByAccountNumber(accountNumber);
		
		if(!account.getPin().equals(pin))
			throw new InvalidPinException("Invalid PIN for accountNumber: "+accountNumber);
		
		log.info("PIN validated successfully");		
	}

}
