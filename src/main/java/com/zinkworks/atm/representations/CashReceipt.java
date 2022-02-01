package com.zinkworks.atm.representations;

import java.util.List;

import com.zinkworks.atm.dtos.WithdrawalDTO;

import lombok.Data;

@Data
public class CashReceipt {
	
	private List<WithdrawalDTO> notesInformation;
	private Float remainingBalance;
	
	public CashReceipt(List<WithdrawalDTO> noteInformation, Float remainingBalance) {
		this.notesInformation = noteInformation;
		this.remainingBalance = remainingBalance;
	}

}
