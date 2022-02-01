package com.zinkworks.atm.impl.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zinkworks.atm.dtos.WithdrawalDTO;
import com.zinkworks.atm.entities.Note;
import com.zinkworks.atm.interfaces.components.IBalanceComponent;
import com.zinkworks.atm.interfaces.components.IPinValidationComponent;
import com.zinkworks.atm.interfaces.components.IWithdrawalComponent;
import com.zinkworks.atm.interfaces.services.IAtmService;
import com.zinkworks.atm.representations.BalanceReceipt;
import com.zinkworks.atm.representations.CashReceipt;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ATMServiceImpl implements IAtmService{
	
	private final IWithdrawalComponent withdrawalComponent;
	private final IPinValidationComponent pinComponent;
	private final IBalanceComponent balanceComponent;
	
	@Autowired
	public ATMServiceImpl(IWithdrawalComponent withdrawalComponent, IPinValidationComponent pinComponent,
			IBalanceComponent balanceComponent) {
		this.withdrawalComponent = withdrawalComponent;
		this.pinComponent = pinComponent;
		this.balanceComponent = balanceComponent;
	}

	@Override
	public CashReceipt requestWithdrawal(String accountNumber, Integer amountRequested) {
		log.info("withdrawal requested, amount: " + amountRequested);		
		
		withdrawalComponent.checkAccountHasFundsEnough(accountNumber, amountRequested);		
		
		List<Note> notesAvailable =	withdrawalComponent.checkATMHasNotesEnough(amountRequested);
		
		withdrawalComponent.checkNotesToPerformWithdrawal(notesAvailable, amountRequested);				
				
		List<WithdrawalDTO> moneyToDelivered = withdrawalComponent.selectNumberMinimumOfNotes(notesAvailable, amountRequested);		
		
		withdrawalComponent.executeWithdrawal(notesAvailable, accountNumber, amountRequested);		
		
		return new CashReceipt(moneyToDelivered, withdrawalComponent.getRemainingBalace(accountNumber));
		
	}

	@Override
	public BalanceReceipt requestBalance(String accountNumber) {
		return balanceComponent.getBalance(accountNumber);		
	}

	@Override
	public void validatePin(String accountNumber, Integer pin) {
		log.debug("Pin validation called. AccountNumber: %s, PIN: %d", accountNumber, pin);		
		pinComponent.validatePin(accountNumber, pin);					
	}

}
