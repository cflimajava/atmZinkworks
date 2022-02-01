package com.zinkworks.atm.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
import com.zinkworks.atm.exceptions.AtmAccountNotFoundExceptions;
import com.zinkworks.atm.exceptions.AtmAmountRequestedValueNonMultipleException;
import com.zinkworks.atm.exceptions.AtmNotNotesEnoughException;
import com.zinkworks.atm.exceptions.AtmWithdrawalNotPerformedException;
import com.zinkworks.atm.impl.component.BalanceComponent;
import com.zinkworks.atm.impl.component.PinValidationComponent;
import com.zinkworks.atm.impl.component.WithdrawalComponentImpl;
import com.zinkworks.atm.impl.service.ATMServiceImpl;
import com.zinkworks.atm.representations.BalanceReceipt;
import com.zinkworks.atm.representations.CashReceipt;

@ExtendWith(MockitoExtension.class)
public class ATMServiceTest {

	@Mock
	private WithdrawalComponentImpl withdrawalComponent;

	@Mock
	private PinValidationComponent pinComponent;

	@Mock
	private BalanceComponent balanceComponent;

	@Mock
	private AccountDAO accountDAO;

	@Mock
	private NoteDAO noteDAO;

	@InjectMocks
	private ATMServiceImpl service;

	private String accountNumberReq = "123456789";
	private Account accountWith1000;
	private List<Note> listNotesAvailable;
	private List<WithdrawalDTO> moneyToDelivered;
	private BalanceReceipt balanceReceipt;

	@BeforeEach
	public void init() {
		initMockData();
	}

	private void initMockData() {
		accountWith1000 = new Account();
		accountWith1000.setId(1);
		accountWith1000.setPin(1234);
		accountWith1000.setBalance(new BigDecimal(800));
		accountWith1000.setOverdraft(new BigDecimal(200));
		accountWith1000.setAccountNumber("123456789");

		listNotesAvailable = Arrays.asList(new Note(1, 50, 10), new Note(2, 20, 30), new Note(3, 10, 30),
				new Note(4, 5, 20));

		moneyToDelivered = Arrays.asList(new WithdrawalDTO(50, 10), new WithdrawalDTO(20, 10));
		
		balanceReceipt =  new BalanceReceipt(
				accountWith1000.getBalance().floatValue(), 
				accountWith1000.getOverdraft().floatValue(), 
				accountWith1000.getBalance().add(accountWith1000.getOverdraft()).floatValue(), 
				1500);

	}

	@Test
	public void test_requestWithdrawal_success() {

		Integer amountRequested = 700;

		doNothing().when(withdrawalComponent).checkAccountHasFundsEnough(accountNumberReq, amountRequested);
		when(withdrawalComponent.checkATMHasNotesEnough(amountRequested)).thenReturn(listNotesAvailable);
		doNothing().when(withdrawalComponent).checkNotesToPerformWithdrawal(listNotesAvailable, amountRequested);
		when(withdrawalComponent.selectNumberMinimumOfNotes(listNotesAvailable, amountRequested))
				.thenReturn(moneyToDelivered);
		doNothing().when(withdrawalComponent).executeWithdrawal(listNotesAvailable, accountNumberReq, amountRequested);

		CashReceipt cashReceipt = service.requestWithdrawal(accountNumberReq, amountRequested);

		assertNotNull(cashReceipt);
		assertNotNull(cashReceipt.getNotesInformation());
		assertEquals(cashReceipt.getNotesInformation().get(0).getNoteValue(), 50);
		assertEquals(cashReceipt.getNotesInformation().get(0).getAmount(), 10);
		assertEquals(cashReceipt.getRemainingBalance(), 0.0f);

	}

	@Test
	public void test_requestWithdrawal_throw_AtmAccountNoFundsEnoughException() {

		Integer amountRequested = 700;

		doThrow(new AtmAccountNoFundsEnoughException(
				"Insufficient available fund, amountRequested: " + amountRequested)).when(withdrawalComponent)
						.checkAccountHasFundsEnough(accountNumberReq, amountRequested);

		AtmAccountNoFundsEnoughException exception = assertThrows(AtmAccountNoFundsEnoughException.class,
				() -> service.requestWithdrawal(accountNumberReq, amountRequested));

		assertNotNull(exception);
		assertEquals(HttpStatus.PRECONDITION_FAILED.value(), exception.getStatus());

	}

	@Test
	public void test_requestWithdrawal_throw_AtmNotNotesEnoughException() {

		Integer amountRequested = 700;

		doNothing().when(withdrawalComponent).checkAccountHasFundsEnough(accountNumberReq, amountRequested);

		when(withdrawalComponent.checkATMHasNotesEnough(amountRequested))
				.thenThrow(new AtmNotNotesEnoughException("Amount Requested not availble"));

		AtmNotNotesEnoughException exception = assertThrows(AtmNotNotesEnoughException.class,
				() -> service.requestWithdrawal(accountNumberReq, amountRequested));

		assertNotNull(exception);
		assertEquals(HttpStatus.PRECONDITION_REQUIRED.value(), exception.getStatus());

	}
	
	@Test
	public void test_requestWithdrawal_throw_AtmAmountRequestedValueNonMultipleException() {

		Integer amountRequested = 700;

		doNothing().when(withdrawalComponent).checkAccountHasFundsEnough(accountNumberReq, amountRequested);
		when(withdrawalComponent.checkATMHasNotesEnough(amountRequested)).thenReturn(listNotesAvailable);
		
		doThrow(new AtmAmountRequestedValueNonMultipleException(
				"Amount Requested need be multiple of 5")).when(withdrawalComponent)
						.checkNotesToPerformWithdrawal(listNotesAvailable, amountRequested);

		AtmAmountRequestedValueNonMultipleException exception = assertThrows(AtmAmountRequestedValueNonMultipleException.class,
				() -> service.requestWithdrawal(accountNumberReq, amountRequested));

		assertNotNull(exception);
		assertEquals(HttpStatus.NOT_ACCEPTABLE.value(), exception.getStatus());

	}
	
	@Test
	public void test_requestWithdrawal_throw_AtmNotNotesEnoughException_by_selectNumberMinimumOfNotes_method() {

		Integer amountRequested = 700;

		doNothing().when(withdrawalComponent).checkAccountHasFundsEnough(accountNumberReq, amountRequested);
		when(withdrawalComponent.checkATMHasNotesEnough(amountRequested)).thenReturn(listNotesAvailable);
		doNothing().when(withdrawalComponent).checkNotesToPerformWithdrawal(listNotesAvailable, amountRequested);
		
		when(withdrawalComponent.selectNumberMinimumOfNotes(listNotesAvailable, amountRequested))
		.thenThrow(new AtmNotNotesEnoughException("Amount Requested not availble"));;

		AtmNotNotesEnoughException exception = assertThrows(AtmNotNotesEnoughException.class,
				() -> service.requestWithdrawal(accountNumberReq, amountRequested));

		assertNotNull(exception);
		assertEquals(HttpStatus.PRECONDITION_REQUIRED.value(), exception.getStatus());

	}
	
	
	@Test
	public void test_requestWithdrawal_throw_AtmWithdrawalNotPerformedException() {

		Integer amountRequested = 700;

		doNothing().when(withdrawalComponent).checkAccountHasFundsEnough(accountNumberReq, amountRequested);
		when(withdrawalComponent.checkATMHasNotesEnough(amountRequested)).thenReturn(listNotesAvailable);
		doNothing().when(withdrawalComponent).checkNotesToPerformWithdrawal(listNotesAvailable, amountRequested);
		when(withdrawalComponent.selectNumberMinimumOfNotes(listNotesAvailable, amountRequested))
		.thenReturn(moneyToDelivered);
		
		
		doThrow(new AtmWithdrawalNotPerformedException(
				"WITHDRAWAL REQUEST CANCELED - Was not possible update notes availables" )).when(withdrawalComponent)
						.executeWithdrawal(listNotesAvailable, accountNumberReq,amountRequested);

		AtmWithdrawalNotPerformedException exception = assertThrows(AtmWithdrawalNotPerformedException.class,
				() -> service.requestWithdrawal(accountNumberReq, amountRequested));

		assertNotNull(exception);
		assertEquals(HttpStatus.EXPECTATION_FAILED.value(), exception.getStatus());

	}
	
	@Test
	public void test_requestBalance_success() {
		
		when(balanceComponent.getBalance(accountNumberReq)).thenReturn(balanceReceipt);
		
		BalanceReceipt balanceReceipt = service.requestBalance(accountNumberReq);
		
		assertNotNull(balanceReceipt);
		assertEquals(accountWith1000.getBalance().floatValue(), balanceReceipt.getBalance());
		assertEquals(accountWith1000.getOverdraft().floatValue(), balanceReceipt.getOverdraft());
		
		assertEquals(accountWith1000.getBalance().add(accountWith1000.getOverdraft()).floatValue() , 
				balanceReceipt.getTotalWithdrawalAvailable());
		
	}
	
	@Test
	public void test_requestBalance_balanceComponent_throw_AtmAccountNotFoundExceptions() {
		
		when(balanceComponent.getBalance(accountNumberReq)).thenThrow(new AtmAccountNotFoundExceptions("Account not found for 'accountNumber': "));
		
		AtmAccountNotFoundExceptions exception = assertThrows(AtmAccountNotFoundExceptions.class,
				() -> service.requestBalance(accountNumberReq));
		
		assertNotNull(exception);
		assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatus());
		
	}
	

}
