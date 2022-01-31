package com.zinkworks.atm.dao;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zinkworks.atm.entities.Account;
import com.zinkworks.atm.exceptions.AtmAccountNotFoundExceptions;
import com.zinkworks.atm.repositories.AccountRepository;

@Service
public class AccountDAO {
	
	@Autowired
	private AccountRepository repo;
	
	
	public Account getAccountByAccountNumber(String accountNumber){
		
		Account accountFound = Optional.ofNullable(repo.findByAccountNumber(accountNumber))
				.orElseThrow(() -> new AtmAccountNotFoundExceptions("Account not found for 'accountNumber': "+accountNumber));				
		
		return accountFound;
		
	}
	
	
	public Account updateAccount(Account accoutModified) {
		return repo.save(accoutModified);
	}
	

}
