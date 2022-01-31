package com.zinkworks.atm.impl.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zinkworks.atm.dtos.CashReceipt;
import com.zinkworks.atm.dtos.WithdrawalDTO;
import com.zinkworks.atm.entities.Note;
import com.zinkworks.atm.interfaces.IAtmService;
import com.zinkworks.atm.interfaces.IPinValidationComponent;
import com.zinkworks.atm.interfaces.IWithdrawalComponent;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ATMServiceImpl implements IAtmService{
	
	private final IWithdrawalComponent withdrawalComponent;
	private final IPinValidationComponent pinComponent;
	
	@Autowired
	public ATMServiceImpl(IWithdrawalComponent withdrawalComponent, IPinValidationComponent pinComponent) {
		this.withdrawalComponent = withdrawalComponent;
		this.pinComponent = pinComponent;
	}

	@Override
	public CashReceipt requestWithdrawal(String accountNumber, Integer amountRequested) {
		log.info("withdrawal requested, amount: " + amountRequested);		
		
		withdrawalComponent.checkAccountHasFundsEnough(accountNumber, amountRequested);		
		
		List<Note> notesAvailable =	withdrawalComponent.checkATMHasNotesEnough(amountRequested);
		
		withdrawalComponent.checkNotesToPerformWithdrawal(notesAvailable, amountRequested);				
				
		List<WithdrawalDTO> moneyToBeDelivered = withdrawalComponent.selectNumberMinimumOfNotes(notesAvailable, amountRequested);		
		
		withdrawalComponent.executeWithdrawal(notesAvailable, accountNumber, amountRequested);		
		
		return new CashReceipt(moneyToBeDelivered, withdrawalComponent.getRemainingBalace(accountNumber));
		
	}

	@Override
	public void requestBalance(String accountNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validatePin(String accountNumber, Integer pin) {
		log.debug("Pin validation called. AccountNumber: %s, PIN: %d", accountNumber, pin);		
		pinComponent.validatePin(accountNumber, pin);					
	}

}
