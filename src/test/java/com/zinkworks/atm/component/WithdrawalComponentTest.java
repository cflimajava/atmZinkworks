package com.zinkworks.atm.component;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.zinkworks.atm.dao.AccountDAO;
import com.zinkworks.atm.dao.NoteDAO;
import com.zinkworks.atm.dtos.WithdrawalDTO;
import com.zinkworks.atm.entities.Account;
import com.zinkworks.atm.entities.Note;
import com.zinkworks.atm.exceptions.AtmAccountNoFundsEnoughException;
import com.zinkworks.atm.exceptions.AtmAmountRequestedValueNonMultipleException;
import com.zinkworks.atm.exceptions.AtmNotNotesEnoughException;
import com.zinkworks.atm.exceptions.AtmWithdrawalNotPerformedException;
import com.zinkworks.atm.impl.component.WithdrawalComponentImpl;

@ExtendWith(MockitoExtension.class)
public class WithdrawalComponentTest {
	
	@Mock
	private NoteDAO noteDAO;
	
	@Mock
	private AccountDAO accountDAO;
	
	@InjectMocks
	private WithdrawalComponentImpl component;
	
	private final String accountNumber = "123456789";
	private Account accountWith1000;
	private Account accountAfterUpdate;
	private List<Note> listNotesAvailable;
	private List<Note> listNotesUpdated;
	
	@BeforeEach
	public void init() {
		initMockData();
	}
	
	private void initMockData() {
		accountWith1000 = new Account();
		accountWith1000.setBalance(new BigDecimal(800));
		accountWith1000.setOverdraft(new BigDecimal(200));
		
		accountAfterUpdate = new Account();
		accountAfterUpdate.setBalance(new BigDecimal(100));
		accountAfterUpdate.setOverdraft(new BigDecimal(200));
		
		listNotesAvailable = Arrays.asList(	new Note(1,50, 10),
				new Note(2,20, 30), new Note(3,10, 30),	new Note(4,5, 20)	);
		
		listNotesUpdated = Arrays.asList(	new Note(50, 0),
				new Note(20, 20), new Note(10, 30),	new Note(5, 20)	);
	}

	@Test
	public void test_checkAccountHasFundsEnough_with_enough_balance() {
		
		when(accountDAO.getAccountByAccountNumber(anyString())).thenReturn(accountWith1000);
		
		assertDoesNotThrow (
				()-> component.checkAccountHasFundsEnough(accountNumber, 500));
		
	}
	
	@Test
	public void test_checkAccountHasFundsEnough_without_enough_balance() {
		
		when(accountDAO.getAccountByAccountNumber(accountNumber)).thenReturn(accountWith1000);
		
		final String MESSAGE_EXPECTED = "Insufficient available fund, amountRequested: 1500";
		
		AtmAccountNoFundsEnoughException exception = assertThrows(AtmAccountNoFundsEnoughException.class, 
				() -> component.checkAccountHasFundsEnough(accountNumber, 1500));
		
		assertEquals(exception.getStatus(), HttpStatus.PRECONDITION_FAILED.value());
		assertEquals(exception.getMessage(), MESSAGE_EXPECTED);
		
	}
	
	@Test
	public void test_checkATMHasNotesEnough_success() {	
		when(noteDAO.getAllNotesAvailable()).thenReturn(listNotesAvailable);
		when(noteDAO.getTotalAmountAvailable()).thenReturn(1500);
		
		assertDoesNotThrow (
				()->component.checkATMHasNotesEnough(1000));		
	}
	
	@Test
	public void test_checkATMHasNotesEnough_without_enough_note_available() {
		when(noteDAO.getAllNotesAvailable()).thenReturn(listNotesAvailable);
		
		final String MESSAGE_EXPECTED = "Amount Requested not availble";
		
		AtmNotNotesEnoughException exception = assertThrows(AtmNotNotesEnoughException.class, 
				() -> component.checkATMHasNotesEnough(1700));
		
		assertEquals(exception.getStatus(), HttpStatus.PRECONDITION_REQUIRED.value());
		assertEquals(exception.getMessage(), MESSAGE_EXPECTED);
		
	}
	
	@Test
	public void test_checkNotesToPerformWithdrawal_success() {		
		assertDoesNotThrow (
				()->component.checkNotesToPerformWithdrawal(listNotesAvailable, 1000));
	}
	
	@Test
	public void test_checkNotesToPerformWithdrawal_without_enough_note_available() {
		
		final String MESSAGE_EXPECTED = "Amount Requested need be multiple of 5";
		
		AtmAmountRequestedValueNonMultipleException exception = assertThrows(AtmAmountRequestedValueNonMultipleException.class, 
				() -> component.checkNotesToPerformWithdrawal(listNotesAvailable, 373));
		
		assertEquals(exception.getStatus(), HttpStatus.NOT_ACCEPTABLE.value());
		assertEquals(exception.getMessage(), MESSAGE_EXPECTED);
		
	}
	
	
	@Test
	public void test_selectNumberMinimumOfNotes_success() {
		
		List<WithdrawalDTO> notes = component.selectNumberMinimumOfNotes(listNotesAvailable, 195);
		
		assertEquals(notes.size(), 3);
		assertEquals(notes.get(0).getNoteValue(), 50);
		assertEquals(notes.get(0).getAmount(), 3);
		
		assertEquals(notes.get(1).getNoteValue(), 20);
		assertEquals(notes.get(1).getAmount(), 2);
		
		assertEquals(notes.get(2).getNoteValue(), 5);
		assertEquals(notes.get(2).getAmount(), 1);		
		
	}
	
	@Test
	public void test_selectNumberMinimumOfNotes_fail_notes_count() {
		
		final String MESSAGE_EXPECTED = "Amount Requested not availble";
		
		AtmNotNotesEnoughException exception = assertThrows(AtmNotNotesEnoughException.class ,
				() -> component.selectNumberMinimumOfNotes(listNotesAvailable, 193));
		
		assertEquals(exception.getStatus(), HttpStatus.PRECONDITION_REQUIRED.value());
		assertEquals(exception.getMessage(), MESSAGE_EXPECTED);
		
	}
	
	@Test
	public void test_executeWithdrawal_success() {
		
		when(accountDAO.getAccountByAccountNumber(accountNumber)).thenReturn(accountWith1000);
		when(noteDAO.getAllNotesAvailable()).thenReturn(listNotesAvailable);
		when(noteDAO.updateNotes(anyList())).thenReturn(listNotesUpdated);
		when(accountDAO.updateAccount(any(Account.class))).thenReturn(accountAfterUpdate);
		
		assertDoesNotThrow(
				() -> component.executeWithdrawal(listNotesAvailable, accountNumber, 700));		
		
	}
	
	@Test
	public void test_executeWithdrawal_fail_to_update_notes() {
		
		when(noteDAO.getAllNotesAvailable())
			.thenThrow(new AtmWithdrawalNotPerformedException("WITHDRAWAL REQUEST CANCELED - Was not possible update notes availables"));
		
		AtmWithdrawalNotPerformedException exception = assertThrows(AtmWithdrawalNotPerformedException.class ,
				() -> component.executeWithdrawal(listNotesAvailable, accountNumber, 700));
		
		assertEquals(exception.getStatus(), HttpStatus.EXPECTATION_FAILED.value());
		
	}
	
	@Test
	public void test_executeWithdrawal_fail_to_update_balance() {
		
		
		when(noteDAO.getAllNotesAvailable()).thenReturn(listNotesAvailable);
		when(noteDAO.updateNotes(listNotesUpdated)).thenReturn(listNotesUpdated);
		when(accountDAO.updateAccount(any(Account.class)))
			.thenThrow(new AtmWithdrawalNotPerformedException("WITHDRAWAL REQUEST CANCELED - Was not possible update Account balance"));
		
		AtmWithdrawalNotPerformedException exception = assertThrows(AtmWithdrawalNotPerformedException.class ,
				() -> component.executeWithdrawal(listNotesAvailable, accountNumber, 700));
		
		assertEquals(exception.getStatus(), HttpStatus.EXPECTATION_FAILED.value());
		verify(noteDAO, times(1)).updateNotes(anyList());
	}
	
	
}
