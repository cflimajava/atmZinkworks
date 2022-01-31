package com.zinkworks.atm.interfaces;

import java.util.List;

import com.zinkworks.atm.dtos.WithdrawalDTO;
import com.zinkworks.atm.entities.Note;

public interface IWithdrawalComponent {

	public void checkAccountHasFundsEnough(String accountNumber, Integer amountRequested);

	public List<Note> checkATMHasNotesEnough(Integer amountRequested);

	public void checkNotesToPerformWithdrawal(List<Note> notesAvailable, Integer amountRequested);

	public List<WithdrawalDTO> selectNumberMinimumOfNotes(List<Note> notesAvailable, Integer amountRequested);

	public void executeWithdrawal(List<Note> notesAvailable, String accountNumber, Integer amountRequested);
	
	public Float getRemainingBalace(String accountNumber); 
	

}
