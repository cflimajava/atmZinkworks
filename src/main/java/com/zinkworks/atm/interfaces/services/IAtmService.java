package com.zinkworks.atm.interfaces.services;

import com.zinkworks.atm.exceptions.AtmAccountNoFundsEnoughException;
import com.zinkworks.atm.representations.BalanceReceipt;
import com.zinkworks.atm.representations.CashReceipt;

public interface IAtmService {
	
	/**
	 * Method implemented to encapsulate the all business logic used to process the withdrawal request
	 * @param accountNumber
	 * @param amount
	 * @return CashReceipt
	 * @throws AtmAccountNoFundsEnoughException, AtmNotNotesEnoughException
	 * 		AtmAmountRequestedValueNonMultipleException, AtmNotNotesEnoughException, 
	 * 		AtmWithdrawalNotPerformedException
	 */
	public CashReceipt requestWithdrawal(String accountNumber, Integer amount);
	
	public BalanceReceipt requestBalance(String accountNumber);
	
	public void validatePin(String accountNumber, Integer pin);

}
