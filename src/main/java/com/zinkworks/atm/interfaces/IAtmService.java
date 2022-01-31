package com.zinkworks.atm.interfaces;

import com.zinkworks.atm.dtos.CashReceipt;
import com.zinkworks.atm.exceptions.AtmAccountNoFundsEnoughException;

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
	
	public void requestBalance(String accountNumber);
	
	public void validatePin(String accountNumber, Integer pin);

}
