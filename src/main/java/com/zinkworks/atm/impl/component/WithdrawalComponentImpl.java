package com.zinkworks.atm.impl.component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zinkworks.atm.dao.AccountDAO;
import com.zinkworks.atm.dao.NoteDAO;
import com.zinkworks.atm.dtos.WithdrawalDTO;
import com.zinkworks.atm.entities.Account;
import com.zinkworks.atm.entities.Note;
import com.zinkworks.atm.exceptions.AtmAccountNoFundsEnoughException;
import com.zinkworks.atm.exceptions.AtmAmountRequestedValueNonMultipleException;
import com.zinkworks.atm.exceptions.AtmNotNotesEnoughException;
import com.zinkworks.atm.exceptions.AtmWithdrawalNotPerformedException;
import com.zinkworks.atm.interfaces.components.IWithdrawalComponent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WithdrawalComponentImpl implements IWithdrawalComponent{
	
	
	private final AccountDAO accountDAO;	
	
	private final NoteDAO noteDAO;
	
	@Autowired
	public WithdrawalComponentImpl(AccountDAO accountDAO, NoteDAO noteDAO) {
		this.accountDAO = accountDAO;
		this.noteDAO = noteDAO;
	}

	
	/**
	 * Check if account has funds enough to perform withdrawal requested
	 * 
	 * @param account
	 * @param amountRequested
	 * 
	 * @throws AtmAccountNoFundsEnoughException
	 */
	@Override
	public void checkAccountHasFundsEnough(String accountNumber, Integer amountRequested) {
		Account account = accountDAO.getAccountByAccountNumber(accountNumber);

		BigDecimal totalAccountAvailable = account.getBalance().add(account.getOverdraft());
		if (totalAccountAvailable.compareTo(new BigDecimal(amountRequested.toString())) < 0)
			throw new AtmAccountNoFundsEnoughException(
					"Insufficient available fund, amountRequested: " + amountRequested);
	}

	/**
	 * Check if ATM contains notes enough to perform withdrawal requested
	 * 
	 * @param notesAvailable
	 * @param amountRequested
	 * 
	 * @throws AtmNotNotesEnoughException
	 */
	@Override
	public List<Note> checkATMHasNotesEnough(Integer amountRequested) {

		List<Note> notesAvailable = noteDAO.getAllNotesAvailable();
		
		Integer totalAmmountAvailable = noteDAO.getTotalAmountAvailable();
		
		if (totalAmmountAvailable < amountRequested)
			throw new AtmNotNotesEnoughException("Amount Requested not availble");
		
		return notesAvailable;
		
	}

	/**
	 * Check if the amount requested is multiple of he minor note available
	 * 
	 * @param notesAvailable
	 * @param amountRequested
	 * 
	 * @throws AtmAmountRequestedValueNonMultipleException
	 */
	@Override
	public void checkNotesToPerformWithdrawal(List<Note> notesAvailable, Integer amountRequested) {
		Note smallestNoteAvailable = notesAvailable.stream()
				.sorted(Comparator.comparingInt(Note::getValue)).findFirst().get();
		
		if ((amountRequested % smallestNoteAvailable.getValue()) > 0)
			throw new AtmAmountRequestedValueNonMultipleException(
					"Amount Requested need be multiple of " + smallestNoteAvailable.getValue());
	}

	/**
	 * Algorithm to select the minimum number of notes
	 * 
	 * @param notesAvailable
	 * @param amountRequested
	 * 
	 * @return List<WithdrawalDTO>
	 * 
	 * @throws AtmNotNotesEnoughException
	 */
	@Override
	public List<WithdrawalDTO> selectNumberMinimumOfNotes(List<Note> notesAvailable, Integer amountRequested) {
		List<WithdrawalDTO> moneyToBeDelivered = new ArrayList<>();
		Integer amountRequestAux = amountRequested;
		for (Note n : notesAvailable) {
			int count = 0;
			while (amountRequestAux >= n.getValue() && amountRequestAux > 0 && n.getAmount() > 0) {
				if (n.getAmount() > 0) {
					amountRequestAux -= n.getValue();
					n.setAmount(n.getAmount() - 1);
					count++;
				}
			}
			if (count > 0)
				moneyToBeDelivered.add(new WithdrawalDTO(n.getValue(), count));
		}

		if (amountRequestAux > 0)
			throw new AtmNotNotesEnoughException("Amount Requested not availble");

		return moneyToBeDelivered;

	}

	/**
	 * Method implement to updated Note and Account table to perform withdrawal
	 * If any exception occurs a AtmWithdrawalNotPerformedException will be throw
	 * and the if necessary the notes backup will be restored 
	 * 
	 * @param notesAvailable
	 * @param account
	 * @param amountRequested
	 * 
	 * @throws AtmWithdrawalNotPerformedException
	 */
	@Override
	public void executeWithdrawal(List<Note> notesAvailable, String accountNumber, Integer amountRequested) {
		Account account = accountDAO.getAccountByAccountNumber(accountNumber);
		List<Note> backUpNotes = noteDAO.getAllNotesAvailable();
		
		try {
			// Update notes available
			noteDAO.updateNotes(notesAvailable);
			log.info("Notes available succesly updated");
		
			BigDecimal subtracted = account.getBalance().subtract(new BigDecimal(amountRequested));
			account.setBalance(subtracted);
			accountDAO.updateAccount(account);	
		}catch (Exception e) {
			log.error(e.getMessage());			
			noteDAO.updateNotes(backUpNotes);
			log.info("Notes available BACKUP RESTORED");
			throw new AtmWithdrawalNotPerformedException("WITHDRAWAL REQUEST CANCELED - Was not possible persist withdrawal changes");	
		}
		
	}

	/**
	 * Method to get the remaining balance account
	 * 
	 * @param notesAvailable
	 * 
	 * @return Float
	 * 
	 * @throws AtmAccountNoFundsEnoughException
	 */
	@Override
	public Float getRemainingBalace(String accountNumber) {
		Account account = accountDAO.getAccountByAccountNumber(accountNumber);		
		return account.getBalance().add(account.getOverdraft()).floatValue();
	}

}
