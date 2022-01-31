package com.zinkworks.atm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.zinkworks.atm.dtos.ATMResponse;
import com.zinkworks.atm.dtos.CashReceipt;
import com.zinkworks.atm.interfaces.IAtmService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ATMController {
	
	@Autowired
	private IAtmService service;
	
	@GetMapping(value = "/withdrawal/{amount}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ATMResponse> withdrawalRequest(@PathVariable("amount") Integer amountRequested,
			@RequestHeader(name = "accountNumber") String accountNumber,
			@RequestHeader(name = "pin") Integer pin){		
		
		CashReceipt cashReceipt = service.requestWithdrawal(accountNumber, amountRequested);
		
		log.info("Withdrawal of amount "+amountRequested+" perfomed sucessfuly to accountNumber: "+accountNumber);
		
		return new ResponseEntity<ATMResponse>(new ATMResponse(cashReceipt), HttpStatus.OK);
	}
	
	@GetMapping(value = "/balance", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ATMResponse> balanceRequest(
			@RequestHeader(name = "accountNumber") String accountNumber,
			@RequestHeader(name = "pin") Integer pin){
		
		
		System.out.println("Chamou o controller 2");
		
		return new ResponseEntity<ATMResponse>(HttpStatus.OK);
	}
	

}
