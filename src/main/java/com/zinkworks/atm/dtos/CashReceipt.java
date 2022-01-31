package com.zinkworks.atm.dtos;

import java.util.List;

import lombok.Data;

@Data
public class CashReceipt {
	
	private List<WithdrawalDTO> noteInformation;
	private Float remainingBalance;
	
	public CashReceipt(List<WithdrawalDTO> noteInformation, Float remainingBalance) {
		this.noteInformation = noteInformation;
		this.remainingBalance = remainingBalance;
	}
	
	

}
