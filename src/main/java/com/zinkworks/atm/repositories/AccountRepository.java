package com.zinkworks.atm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zinkworks.atm.entities.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer>{
	 
	public Account findByAccountNumber(String accountNumber);
	
}