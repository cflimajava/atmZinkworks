package com.zinkworks.atm.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.zinkworks.atm.dao.AccountDAO;
import com.zinkworks.atm.dao.NoteDAO;
import com.zinkworks.atm.entities.Account;
import com.zinkworks.atm.exceptions.AtmAccountNotFoundExceptions;
import com.zinkworks.atm.impl.component.BalanceComponent;
import com.zinkworks.atm.representations.BalanceReceipt;

@ExtendWith(MockitoExtension.class)
public class BalanceComponentTest {
	
	@Mock
	private AccountDAO accountDAO;
	
	@Mock
	private NoteDAO noteDAO;
	
	@InjectMocks
	private BalanceComponent component;
	
	private final String accountNumber = "123456789";
	private Account accountWith1000;
	
	@BeforeEach
	public void init() {
		initMockData();
	}
	
	private void initMockData() {		
		accountWith1000 = new Account();
		accountWith1000.setBalance(new BigDecimal(800));
		accountWith1000.setOverdraft(new BigDecimal(200));
	}

	@Test
	public void test_getBalance_success() {
		
		when(noteDAO.getTotalAmountAvailable()).thenReturn(1000);
		when(accountDAO.getAccountByAccountNumber(any())).thenReturn(accountWith1000);
		
		BalanceReceipt balance = component.getBalance(accountNumber);
		
		assertNotNull(balance);
		assertEquals(balance.getTotalWithdrawalAvailable(), 
				accountWith1000.getBalance().add(accountWith1000.getOverdraft()).floatValue());
		assertEquals(balance.getTotalAmountATM(), 1000);
		
	}
	
	@Test
	public void test_getBalance_account_not_found() {
		
		when(noteDAO.getTotalAmountAvailable()).thenReturn(1000);
		when(accountDAO.getAccountByAccountNumber(any()))
			.thenThrow(new AtmAccountNotFoundExceptions("Account not found for 'accountNumber': 123456789"));
		
		AtmAccountNotFoundExceptions exception = assertThrows(AtmAccountNotFoundExceptions.class, 
				() -> component.getBalance(accountNumber));
		
		assertNotNull(exception);
		assertEquals(HttpStatus.NOT_FOUND.value(), exception.getStatus());
		
	}
}
