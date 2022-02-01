package com.zinkworks.atm.representations;

import lombok.Data;

@Data
public class BalanceReceipt {

	private Float balance;
	
	private Float overdraft;
	
	private Float totalWithdrawalAvailable;
	
	private Integer totalAmountATM;

	public BalanceReceipt(Float balance, Float overdraft, Float totalWithdrawalAvailable, Integer totalAmountATM) {
		this.balance = balance;
		this.overdraft = overdraft;
		this.totalWithdrawalAvailable = totalWithdrawalAvailable;
		this.totalAmountATM = totalAmountATM;
	}	
	
}
