package com.zinkworks.atm.dtos;

import lombok.Data;

@Data
public class ATMResponse {
	
	private CashReceipt cashReceipt;

	public ATMResponse(CashReceipt cashReceipt) {
		this.cashReceipt = cashReceipt;
	}	

}
