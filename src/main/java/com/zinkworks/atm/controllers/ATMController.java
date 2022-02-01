package com.zinkworks.atm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.zinkworks.atm.exceptions.AtmAccountNoFundsEnoughException;
import com.zinkworks.atm.exceptions.AtmAccountNotFoundExceptions;
import com.zinkworks.atm.exceptions.AtmAmountRequestedValueNonMultipleException;
import com.zinkworks.atm.exceptions.AtmNotNotesEnoughException;
import com.zinkworks.atm.exceptions.AtmWithdrawalNotPerformedException;
import com.zinkworks.atm.exceptions.InvalidPinException;
import com.zinkworks.atm.interfaces.services.IAtmService;
import com.zinkworks.atm.representations.BalanceReceipt;
import com.zinkworks.atm.representations.CashReceipt;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ATMController {
	
	@Autowired
	private IAtmService service;
	
	@ApiOperation(value = "Request withdrawal", notes = "Resource used to request a withdrawal")
	@ApiResponses({ 
			@ApiResponse(code = 401, message = "PIN wrong", response = InvalidPinException.class),
			@ApiResponse(code = 404, message = "Account not found", response = AtmAccountNotFoundExceptions.class),
			@ApiResponse(code = 406, message = "Amount Request is not a valid multiple value", response = AtmAmountRequestedValueNonMultipleException.class), 
			@ApiResponse(code = 412, message = "Account no funds enough", response = AtmAccountNoFundsEnoughException.class),
			@ApiResponse(code = 417, message = "Data cound not be upadte to perform withdrawal", response = AtmWithdrawalNotPerformedException.class),
			@ApiResponse(code = 428, message = "ATM has not notes enogh to perfome withdrawal", response = AtmNotNotesEnoughException.class)
	})
	@GetMapping(value = "/withdrawal/{amount}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CashReceipt> withdrawalRequest(
			@ApiParam(value = "Amount to be requested", required = true, type = "Integer") @PathVariable("amount") Integer amountRequested,
			@ApiParam(value = "Account number", required = true, type = "String") @RequestHeader(name = "accountNumber") String accountNumber,
			@ApiParam(value = "pin", required = true, type = "Integer")@RequestHeader(name = "pin") Integer pin){		
		
		CashReceipt cashReceipt = service.requestWithdrawal(accountNumber, amountRequested);
		
		log.info("Withdrawal of amount "+amountRequested+" perfomed sucessfuly to accountNumber: "+accountNumber);
		
		return new ResponseEntity<CashReceipt>(cashReceipt, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Request balance", notes = "Resource used to request a balance by accountNumber")
	@ApiResponses({ 
			@ApiResponse(code = 401, message = "PIN wrong", response = InvalidPinException.class),
			@ApiResponse(code = 404, message = "Account not found", response = AtmAccountNotFoundExceptions.class)
	})
	@GetMapping(value = "/balance", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<BalanceReceipt> balanceRequest(
			@ApiParam(value = "Account number", required = true, type = "String") @RequestHeader(name = "accountNumber") String accountNumber,
			@ApiParam(value = "pin", required = true, type = "Integer") @RequestHeader(name = "pin") Integer pin){		
		
		BalanceReceipt requestedBalance = service.requestBalance(accountNumber);
		
		log.info("Balance details request perfomed sucessfuly to accountNumber: "+accountNumber);
		
		return new ResponseEntity<BalanceReceipt>(requestedBalance, HttpStatus.OK);
	}
	

}
