package com.zinkworks.atm.component;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.zinkworks.atm.dao.AccountDAO;
import com.zinkworks.atm.entities.Account;
import com.zinkworks.atm.exceptions.InvalidPinException;
import com.zinkworks.atm.impl.component.PinValidationComponent;

@ExtendWith(MockitoExtension.class)
public class PinValidationComponentTest {

	@Mock
	private AccountDAO accountDAO;

	@InjectMocks
	private PinValidationComponent component;

	private final String accountNumber = "123456789";
	private Account accountWithPin;

	@BeforeEach
	public void init() {
		initMockData();
	}

	private void initMockData() {
		accountWithPin = new Account();
		accountWithPin.setPin(2525);

	}

	@Test
	public void test_validatePin_success() {

		Integer pin = 2525;
		when(accountDAO.getAccountByAccountNumber(any())).thenReturn(accountWithPin);

		assertDoesNotThrow(() -> component.validatePin(accountNumber, pin));

	}
	
	@Test
	public void test_validatePin_with_wrong_pin() {
		
		final String MESSAGE_EXPECTED = "Invalid PIN for accountNumber: "+accountNumber;
		
		Integer pinIncorreto = 2020;
		when(accountDAO.getAccountByAccountNumber(any())).thenReturn(accountWithPin);
		
		InvalidPinException exception = assertThrows(InvalidPinException.class, 
				()-> component.validatePin(accountNumber, pinIncorreto));
		
		assertNotNull(exception);
		assertEquals(HttpStatus.UNAUTHORIZED.value(), exception.getStatus());
		assertEquals(MESSAGE_EXPECTED, exception.getMessage());
	}

}
