package com.zinkworks.atm.impl.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zinkworks.atm.dao.AccountDAO;
import com.zinkworks.atm.dao.NoteDAO;
import com.zinkworks.atm.entities.Account;
import com.zinkworks.atm.interfaces.components.IBalanceComponent;
import com.zinkworks.atm.representations.BalanceReceipt;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BalanceComponent implements IBalanceComponent{

	
	private final AccountDAO accountDAO;
	
	private final NoteDAO noteDAO;
	
	@Autowired
	public BalanceComponent(AccountDAO accountDAO, NoteDAO noteDAO) {
		this.accountDAO = accountDAO;
		this.noteDAO = noteDAO;
	}

	@Override
	public BalanceReceipt getBalance(String accountNumber) {
		log.debug("Request balance proccess started for accountNumber:" + accountNumber);
		
		Integer totalAmmountATM = noteDAO.getTotalAmountAvailable();
		
		Account account = accountDAO.getAccountByAccountNumber(accountNumber);
		return new BalanceReceipt(
				account.getBalance().floatValue(), 
				account.getOverdraft().floatValue(), 
				account.getBalance().add(account.getOverdraft()).floatValue(), 
				totalAmmountATM);
	}

}
